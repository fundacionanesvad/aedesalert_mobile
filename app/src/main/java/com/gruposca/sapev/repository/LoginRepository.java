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

package com.gruposca.sapev.repository;

import com.gruposca.sapev.datastore.net.LoginApi;
import com.gruposca.sapev.datastore.net.model.LoginResult;
import com.gruposca.sapev.datastore.net.model.User;
import com.gruposca.sapev.datastore.preference.UserPreference;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RetrofitError;
import retrofit.client.Response;

@Singleton
public class LoginRepository {

    private final LoginApi loginApi;
    private final UserPreference userPreference;

    @Inject
    public LoginRepository(LoginApi loginApi, UserPreference userPreference) {
        this.loginApi = loginApi;
        this.userPreference = userPreference;
    }

    public Boolean login(String login, String password) {
        try {
            LoginResult result = loginApi.login(new User(login, password));
            userPreference.setLogin(login);
            userPreference.setPassword(password);
            userPreference.setToken(result.authorizationToken);
            userPreference.setName(result.name);
            userPreference.setId(result.id);
            return true;
        } catch (RetrofitError e) {
            Response response = e.getResponse();
            if (response != null && response.getStatus() == 401) {
                return false;
            } else {
                throw e;
            }
        }
    }
}