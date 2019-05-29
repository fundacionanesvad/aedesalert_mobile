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
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.AreaGetList;
import com.gruposca.sapev.interactor.AreaUnlock;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.PlanDelete;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.interactor.SendLogs;
import com.gruposca.sapev.interactor.VisitsExists;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlanPresenterImpl implements PlanPresenter {

    private PlanPresenter.View view;
    private final PlanGet planGet;
    private final AreaGetList areaGetList;
    private final PlanDelete planDelete;
    private final VisitsExists visitsExists;
    private final SendLogs sendLogs;
    private final AreaUnlock areaUnlock;

    @Inject
    public PlanPresenterImpl(PlanGet planGet, AreaGetList areaGetList, PlanDelete planDelete, VisitsExists visitsExists, SendLogs sendLogs, AreaUnlock areaUnlock) {
        this.planGet = planGet;
        this.areaGetList = areaGetList;
        this.planDelete = planDelete;
        this.visitsExists = visitsExists;
        this.sendLogs = sendLogs;
        this.areaUnlock = areaUnlock;
    }

    @Override
    public void initialize(View view) {
        this.view = view;
        view.showLoading();
    }

    @Override
    public void load() {
        planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(Plan response) {
                view.updateTitle(response);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_GET_PLAN, error.toString());
                view.showSnackbar(R.string.error_plan_load);
            }
        });
        areaGetList.execute(new Callback<List<AreaListQuery>>() {
            @Override
            public void onResponse(List<AreaListQuery> response) {
                view.showAreas(response);
                long inspections = 0L;
                String sector="";
                for (AreaListQuery area : response) {
                    inspections += area.inspections;
                    sector=area.sector;
                }
                view.updateSubtitle(inspections);
                view.updateSector(sector);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PLAN_AREA_GETLIST, error.toString());
                view.showSnackbar(R.string.error_plan_load);
            }
        });
    }

    @Override
    public void deletePlan() {
        planDelete.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) { view.showLogin(); }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PLAN_DELETE, error.toString());
            }
        });
    }

    @Override
    public void existsVisits() {
        Logs.log(Logs.INFO, null, null, Logs.SYNC_VISITS);
        visitsExists.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if(response){
                    Logs.log(Logs.INFO, null, null, Logs.SYNC_VISITS_OK);
                    view.confirmSyncPlan();
                }else{
                    Logs.log(Logs.INFO, null, null, Logs.SYNC_VISITS_ERROR);
                    view.showNotVisits();
                }
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PLAN_VISITS, error.toString());
            }
        });
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

    @Override
    public void unlockArea(Integer pin) {
        areaUnlock.execute(pin, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    view.showSnackbar(R.string.area_unlock_success);
                    load();
                } else {
                    view.showSnackbar(R.string.area_unlock_fail);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showSnackbar(R.string.area_unlock_error);
            }
        });
    }
}