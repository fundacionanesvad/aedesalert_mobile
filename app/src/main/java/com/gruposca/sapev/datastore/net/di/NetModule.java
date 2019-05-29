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

package com.gruposca.sapev.datastore.net.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.gruposca.sapev.BuildConfig;
import com.gruposca.sapev.datastore.net.ApiInterceptor;
import com.gruposca.sapev.datastore.net.LoginApi;
import com.gruposca.sapev.datastore.net.ProgressConverter;
import com.gruposca.sapev.datastore.net.SyncApi;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module(
        complete = false,
        library = true
)

public class NetModule {

    @Provides
    @Singleton
    public ProgressConverter provideProgressConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Calendar.class, new JsonDeserializer<Calendar>() {
            @Override
            public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Calendar returnedValue = GregorianCalendar.getInstance(Locale.getDefault());
                int offset = returnedValue.getTimeZone().getRawOffset();
                returnedValue.setTimeInMillis(json.getAsLong() - offset);
                return returnedValue;
            }
        });
        Gson gson = gsonBuilder.create();
        return new ProgressConverter(gson);
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(ApiInterceptor requestInterceptor, ProgressConverter converter, OkHttpClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.API_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setConverter(converter)
                .setClient(new OkClient(client))
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        return client;
    }

    @Provides
    public LoginApi provideLoginApi(RestAdapter restAdapter) {
        return  restAdapter.create(LoginApi.class);
    }

    @Provides
    public SyncApi provideSyncApi(RestAdapter restAdapter) {
        return  restAdapter.create(SyncApi.class);
    }
}