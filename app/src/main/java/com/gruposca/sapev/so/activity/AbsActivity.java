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

package com.gruposca.sapev.so.activity;

import android.os.Bundle;

import com.gruposca.sapev.BaseApplication;
import com.gruposca.sapev.R;
import com.gruposca.sapev.so.activity.di.ActivityModule;
import com.gruposca.sapev.tool.Logs;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

import butterknife.InjectView;
import butterknife.Optional;
import dagger.ObjectGraph;

public abstract class AbsActivity extends AppCompatActivity {

    private ObjectGraph activityObjectGraph;

    @InjectView(R.id.toolbar) @Optional
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            initializeInjection();
            super.onCreate(savedInstanceState);
            trace("onCreate");
            int activityLayoutResourceId = this.onActivityRequestLayout();
            if (activityLayoutResourceId != 0)
                setContentView(activityLayoutResourceId);
            inject(this);
            injectViews();
            if (toolbar != null)
                initializeToolbar(toolbar);
            onActivityCreate(savedInstanceState);
        } catch (Exception e) {
            manageFatalUnhandledException("onCreate", e);
        }
    }

    protected int onActivityRequestLayout() { return 0; }

    protected boolean displayHomeAsUpEnabled() { return true; }

    private void initializeToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled());
        onActivityToolbarInitialized(toolbar);
    }

    private void initializeInjection() {
        List<Object> activityModules = new LinkedList<Object>(Arrays.asList(this.getInjectionModules()));
        activityModules.add(new ActivityModule(this));
        activityObjectGraph = ((BaseApplication)getApplication()).plus(activityModules);
    }

    protected abstract Object[] getInjectionModules();

    public void inject(Object object) {
        activityObjectGraph.inject(object);
    }

    private void injectViews() {
        ButterKnife.inject(this);
    }

    protected void onActivityToolbarInitialized(Toolbar toolbar) { }

    protected void onActivityCreate(Bundle savedInstanceState) { }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            trace("onSaveInstanceState");
            onActivitySaveInstanceState(outState);
        } catch (Exception e) {
            manageFatalUnhandledException("onSaveInstanceState", e);
        } finally {
            super.onSaveInstanceState(outState);
        }
    }

    protected void onActivitySaveInstanceState(Bundle outState) { }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            trace("onRestoreInstanceState");
            onActivityRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            manageFatalUnhandledException("onRestoreInstanceState", e);
        } finally {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    protected void onActivityRestoreInstanceState(Bundle savedInstanceState) { }

    @Override
    protected void onResume() {
        try {
            trace("onResume");
            onActivityResume();
        } catch (Exception e) {
            manageFatalUnhandledException("onResume", e);
        } finally {
            super.onResume();
        }
    }

    protected void onActivityResume() { }

    @Override
    protected void onPause() {
        try {
            trace("onPause");
            onActivityPause();
        } catch (Exception e) {
            manageFatalUnhandledException("onPause", e);
        } finally {
            super.onPause();
        }
    }

    protected void onActivityPause() { }

    @Override
    protected void onStop() {
        try {
            trace("onStop");
            onActivityStop();
        } catch (Exception e) {
            manageFatalUnhandledException("onStop", e);
        } finally {
            super.onStop();
        }
    }

    protected void onActivityStop() { }

    @Override
    protected void onDestroy() {
        try {
            trace("onDestroy");
            onActivityDestroy();
        } catch (Exception e) {
            manageFatalUnhandledException("onDestroy", e);
        } finally {
            super.onDestroy();
        }
    }

    protected void onActivityDestroy() { }

    public void showSnackbar(int resId, Object... formatArgs) {
        View view = toolbar;
        if (view != null) {
            Snackbar.make(view, getString(resId, formatArgs), Snackbar.LENGTH_LONG).show();
        }
    }

    public void showSnackbar(int resId) {
        View view = toolbar;
        if (view != null)
            Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    protected final void manageFatalUnhandledException(String method, Throwable error) {
        String module = ((Object)this).getClass().getSimpleName();
        Logs.log(Logs.ERROR, module, method, error.getMessage(), error);
        throw new RuntimeException(error);
    }

    protected final void trace(String method) {
        String module = ((Object)this).getClass().getSimpleName();
        Logs.log(Logs.VERBOSE, module, method);
    }
}