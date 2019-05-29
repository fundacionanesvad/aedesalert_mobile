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

import android.text.format.DateUtils;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.net.model.PlanResult;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.LoginSubmit;
import com.gruposca.sapev.interactor.PlanDelete;
import com.gruposca.sapev.interactor.PlanGetList;
import com.gruposca.sapev.interactor.PlanSync;
import com.gruposca.sapev.interactor.SendLogs;
import com.gruposca.sapev.tool.Errors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginPresenterImpl implements LoginPresenter {

    private LoginPresenter.View view;
    private final LoginSubmit loginSubmit;
    private final PlanGetList planGetList;
    private final PlanSync planSync;
    private final PlanDelete planDelete;
    private final SendLogs sendLogs;

    @Inject
    public LoginPresenterImpl(LoginSubmit loginSubmit, PlanGetList planGetList, PlanSync planSync, PlanDelete planDelete, SendLogs sendLogs) {
        this.loginSubmit = loginSubmit;
        this.planGetList = planGetList;
        this.planSync = planSync;
        this.planDelete = planDelete;
        this.sendLogs = sendLogs;
    }

    @Override
    public void initialize(View view) {
        this.view = view;
        view.showLogin();
        view.showKeyboard();
    }

    @Override
    public void login(final String login, String password) {
        if (view.controlErrors()) {
            view.hideKeyboard();
            view.showLoading();
            view.showInfo(R.string.validating_user);

            loginSubmit.execute(login, password, new Callback<Boolean>() {

                @Override
                public void onResponse(Boolean valid) {
                    if (valid) {
                        getAvailablePlans();
                    } else {
                        view.showError(this.getClass().toString(), Errors.METHOD_LOGIN, R.string.login_error);
                    }
                }

                @Override
                public void onError(ErrorBundle error) {
                    view.showError(this.getClass().toString(), Errors.METHOD_LOGIN, R.string.network_error);
                }
            });
        }
    }

    @Override
    public void sendLogs() {
        view.showSnackbar(R.string.message_send_email);
        sendLogs.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response)
                    view.showSnackbar(R.string.send_email_ok);
                else
                    view.showSnackbar(R.string.send_email_logs_error);
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showSnackbar(R.string.send_email_logs_error);
            }
        });
    }

    private void getAvailablePlans() {
        view.showInfo(R.string.retrieving_available_plans);
        planGetList.execute(new Callback<List<PlanResult>>() {
            @Override
            public void onResponse(List<PlanResult> response) {
                if (response.size() == 0) {
                    view.showError(this.getClass().toString(), Errors.METHOD_GET_PLAN, R.string.plan_list_empty);
                } else {
                    for(PlanResult plan : response) {
                        if (DateUtils.isToday(plan.date.getTimeInMillis())) {
                            syncPlan(plan.id);
                            return;
                        }
                    }
                    view.showError(this.getClass().toString(), Errors.METHOD_GET_PLAN, R.string.plan_today_none);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showError(this.getClass().toString(), Errors.METHOD_GET_PLAN, R.string.plan_list_error_load);
            }
        });
    }

    private void syncPlan(Integer id) {
        view.showInfo(R.string.downloading_plan);
        planSync.execute(id, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    view.showPlan();
                } else {
                    deletePlan(R.string.plan_list_error_sync);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                deletePlan(R.string.plan_list_error_sync);
            }
        });
    }

    private void deletePlan(final int errorResId) {
        planDelete.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                view.showError(this.getClass().toString(), Errors.METHOD_DELETEPLAN, errorResId);
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showError(this.getClass().toString(), Errors.METHOD_DELETEPLAN, errorResId);
            }
        });
    }
}