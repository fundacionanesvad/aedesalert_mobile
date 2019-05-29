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

package com.gruposca.sapev.datastore.net;

import com.gruposca.sapev.datastore.net.model.PlanDetailResult;
import com.gruposca.sapev.datastore.net.model.PlanResult;
import com.gruposca.sapev.datastore.net.model.SyncHouse;
import com.gruposca.sapev.datastore.net.model.SyncPlan;
import com.gruposca.sapev.interactor.Callback;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SyncApi {
    @GET("/sync")
    List<PlanResult> getPlans();

    @GET("/sync/{id}")
    PlanDetailResult getPlan(@Path("id") Integer id);

    @POST("/sync")
    Response syncPlan(@Body SyncPlan plan);
}