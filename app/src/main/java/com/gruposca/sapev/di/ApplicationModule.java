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

package com.gruposca.sapev.di;

import dagger.Module;
import dagger.Provides;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gruposca.sapev.BaseApplication;
import com.gruposca.sapev.executor.di.ExecutorModule;
import com.gruposca.sapev.interactor.di.InteractorModule;
import com.gruposca.sapev.repository.di.RepositoryModule;
import com.gruposca.sapev.service.di.ServiceModule;

@Module(
        injects = {
                BaseApplication.class
        },
        includes = {
                InteractorModule.class,
                RepositoryModule.class,
                ExecutorModule.class,
                ServiceModule.class,
                com.gruposca.sapev.repository.di.RepositoryModule.class
        },
        library = true
)
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(BaseApplication application) {
        context = application;
    }

    @Provides
    public Context provideApplicationContext() {
        return context;
    }

    @Provides
    public SharedPreferences provideSharedPreferences() { return PreferenceManager.getDefaultSharedPreferences(context); }
}