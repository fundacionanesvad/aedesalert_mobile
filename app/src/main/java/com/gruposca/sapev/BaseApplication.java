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

package com.gruposca.sapev;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.gruposca.sapev.di.ApplicationModule;
import com.gruposca.sapev.tool.Logs;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.List;
import dagger.ObjectGraph;

public class BaseApplication extends Application {

      private ObjectGraph objectGraph;

      private static Context context;

      public static Context getContext() {
        return context;
      }

      public static boolean isDebuggable() {
        return ((getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
      }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Logs.log(Logs.DEBUG, "BaseApplication", "onCreate", "Starting application");
        initializeDependencyInjection();
        FlowManager.init(this);
        Logs.log(Logs.DEBUG, "BaseApplication", "onCreate", "Application started");
    }

    private void initializeDependencyInjection() {
        objectGraph = ObjectGraph.create(getInjectionModules());
        objectGraph.inject(this);
    }

    private Object[] getInjectionModules() {
        return new Object[] {
            new ApplicationModule(this)
        };
    }

    public ObjectGraph plus(List<Object> modules) {
        if (modules == null)
            throw new IllegalArgumentException("You can't pass Null modules.");

        return objectGraph.plus(modules.toArray());
    }

    public void inject(Object instance) {
        objectGraph.inject(instance);
    }
}