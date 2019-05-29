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

package com.gruposca.sapev.presenter;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.preference.UserPreference;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.LoginSubmit;
import com.gruposca.sapev.interactor.PlanDelete;
import com.gruposca.sapev.interactor.PlanSubmit;
import com.gruposca.sapev.interactor.ProgressCallback;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlanSyncPresenterImpl implements PlanSyncPresenter {

    private PlanSyncPresenter.View view;

    private final PlanDelete planDelete;
    private final LoginSubmit loginSubmit;
    private final PlanSubmit planSubmit;
    private final UserPreference userPreference;

    @Inject
    public PlanSyncPresenterImpl(PlanDelete planDelete, LoginSubmit loginSubmit, PlanSubmit planSubmit, UserPreference userPreference) {
        this.planDelete = planDelete;
        this.loginSubmit = loginSubmit;
        this.planSubmit = planSubmit;
        this.userPreference = userPreference;
    }

    @Override
    public void initialize(View view) {
        this.view = view;
    }

    @Override
    public void syncPlan() {
        view.setProgressState(R.string.connecting);
        view.setProgressValue(0);
        Logs.log(Logs.INFO, null, null, Logs.SYNC_LOGIN);
        loginSubmit.execute(userPreference.getLogin(), userPreference.getPassword(), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean valid) {
                if (valid) {
                    Logs.log(Logs.INFO, null, null, Logs.SYNC_LOGIN_OK);
                    submitPlan();
                } else {
                    view.onError(R.string.plan_sync_error_user, null);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                view.onError(R.string.plan_sync_error_user_conexion, error);
            }
        });
    }

    private void submitPlan() {
        planSubmit.execute(new ProgressCallback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if(response){
                    deletePlan();
                } else {
                    view.onError(R.string.plan_sync_error, null);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                view.onError(R.string.plan_sync_error_conexion, error);
            }

            @Override
            public void onProgress(int value) {
                view.setProgressValue(value);
                if (value == 100) {
                    view.setProgressState(R.string.processing_plan);
                } else if (value > 0) {
                    view.setProgressState(R.string.uploading_plan);
                }
            }
        });
    }

    public void deletePlan() {
        planDelete.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                view.onFinish();
            }

            @Override
            public void onError(ErrorBundle error) {
                view.onError(R.string.plan_sync_error_delete_plan, error);
            }
        });
    }
}
