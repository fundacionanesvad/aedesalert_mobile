/*
 * Aedes Alert, Support to collect data to combat dengue
 * Copyright (C) 2017 Fundación Anesvad
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.gruposca.sapev.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gruposca.sapev.BaseApplication;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.HouseUpdateLocation;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String PARAM_HOUSE_UUID = "PARAM_HOUSE_UUID";
    private static final int REFRESH_INTERVAL = 1000;       //1 second
    private static final int MAX_RUNTIME = 5 * 60 * 1000;   //5 minutes
    private static final int MIN_ACCURACY = 20;             //20 meters

    private GoogleApiClient googleApiClient;
    private String houseUuid;
    private long startTime;

    @Inject
    protected HouseUpdateLocation houseUpdateLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        houseUuid = intent.getStringExtra(PARAM_HOUSE_UUID);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        trace("onCreate");
        ((BaseApplication) getApplication()).inject(this);
        startTime = System.currentTimeMillis();
        googleApiClient = new GoogleApiClient.Builder(LocationService.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(LocationService.this)
                .addOnConnectionFailedListener(LocationService.this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        trace("onDestroy");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        trace("onLocationChanged: " + location.toString());
        if (location.hasAccuracy() && location.getAccuracy() <= MIN_ACCURACY) {
            trace("onLocationAccepted");
            houseUpdateLocation.execute(houseUuid, location.getLatitude(), location.getLongitude(), new Callback<Void>() {
                @Override
                public void onResponse(Void response) {
                    stopSelf();
                }

                @Override
                public void onError(ErrorBundle error) {
                    stopSelf();
                }
            });
        }
        if (System.currentTimeMillis() - startTime > MAX_RUNTIME) {
            trace("onLocationMaxRuntime");
            stopSelf();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        trace("onConnectionFailed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        trace("onConnected");
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(REFRESH_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        trace("onConnectionSuspended");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected final void trace(String method) {
        String module = this.getClass().getSimpleName();
        Logs.log(Logs.VERBOSE, module, method);
    }
}