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

package com.gruposca.sapev.so.activity.di;

import com.gruposca.sapev.presenter.PlanPresenter;
import com.gruposca.sapev.presenter.PlanPresenterImpl;
import com.gruposca.sapev.presenter.LoginPresenter;
import com.gruposca.sapev.presenter.LoginPresenterImpl;
import com.gruposca.sapev.presenter.MainPresenter;
import com.gruposca.sapev.presenter.MainPresenterImpl;
import com.gruposca.sapev.presenter.PlanSyncPresenter;
import com.gruposca.sapev.presenter.PlanSyncPresenterImpl;
import com.gruposca.sapev.so.activity.MainActivity;
import com.gruposca.sapev.so.dialog.CreateFakeDialog;
import com.gruposca.sapev.so.dialog.SyncPlanDialog;
import com.gruposca.sapev.so.fragment.PlanFragment;
import com.gruposca.sapev.so.fragment.LoginFragment;

import dagger.Module;
import dagger.Provides;

@Module (
        injects = {
                MainActivity.class,
                LoginFragment.class,
                PlanFragment.class,
                CreateFakeDialog.class,
                SyncPlanDialog.class
        },
        complete = false
)

public class MainActivityModule {

    @Provides
    public MainPresenter provideMainPresenter(MainPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public LoginPresenter provideLoginPresenter(LoginPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public PlanPresenter provideAreasPresenter(PlanPresenterImpl presenter) { return presenter; }

    @Provides
    public PlanSyncPresenter providePlanSyncPresenter(PlanSyncPresenterImpl presenter) { return presenter; }

}