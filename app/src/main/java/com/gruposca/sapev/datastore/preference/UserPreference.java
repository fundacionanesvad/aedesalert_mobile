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

package com.gruposca.sapev.datastore.preference;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPreference {

    private final String LOGIN = "UserPreference_LOGIN";
    private final String PASSWORD = "UserPreference_PASSWORD";
    private final String TOKEN = "UserPreference_TOKEN";
    private final String NAME = "UserPreference_NAME";
    private final String ID = "UserPreference_ID";

    private SharedPreferences preferences;
    private String login;
    private String password;
    private String token;
    private String name;
    private Integer id;

    @Inject
    public UserPreference(SharedPreferences preferences) {
        this.preferences = preferences;
        load();
    }

    private void load() {
        login = preferences.getString(LOGIN, null);
        password = preferences.getString(PASSWORD, null);
        token = preferences.getString(TOKEN, null);
        name = preferences.getString(NAME, null);
        id = preferences.getInt(ID, 0);
    }

    private void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN, login);
        editor.putString(PASSWORD, password);
        editor.putString(TOKEN, token);
        editor.putString(NAME, name);
        editor.putInt(ID, id);
        editor.commit();
    }

    /*public void clear() {
        login = null;
        password = null;
        token = null;
        name = null;
        id = 0;
        save();
    }*/

    public void setLogin(String login) {
        this.login = login;
        save();
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
        save();
    }

    public String getPassword() {
        return password;
    }

    public void setToken(String token) {
        this.token = token;
        save();
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
        save();
    }

    public Integer getId() {
        return id;
    }
}