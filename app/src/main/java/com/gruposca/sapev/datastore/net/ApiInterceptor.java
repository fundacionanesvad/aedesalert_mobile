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

import com.gruposca.sapev.datastore.preference.UserPreference;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;

@Singleton
public class ApiInterceptor implements RequestInterceptor {

    private UserPreference userPreference;

    @Inject
    public ApiInterceptor(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (userPreference.getToken() != null) {
            request.addHeader("Authorization", userPreference.getToken());
        }
    }
}