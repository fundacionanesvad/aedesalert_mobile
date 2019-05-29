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
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.HouseUpdateQR;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.interactor.VisitFinish;
import com.gruposca.sapev.interactor.VisitGet;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;

public class InspectionSummaryPresenterImpl implements InspectionSummaryPresenter {

    private final VisitGet visitGet;
    private final VisitFinish visitFinish;
    private final HouseUpdateQR houseUpdateQR;
    private final PlanGet planGet;
    private InspectionSummaryPresenter.View view;
    private String visitUuid;
    private Visit visit;

    @Inject
    public InspectionSummaryPresenterImpl(VisitGet visitGet, VisitFinish visitFinish, HouseUpdateQR houseUpdateQR, PlanGet planGet) {
        this.visitGet = visitGet;
        this.visitFinish = visitFinish;
        this.houseUpdateQR = houseUpdateQR;
        this.planGet = planGet;
    }

    @Override
    public void initialize(View view, String visitUuid) {
        this.view = view;
        this.visitUuid = visitUuid;
        this.view.initToolbar();
    }

    @Override
    public void load() {
        planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(final Plan plan) {
                visitGet.execute(visitUuid, new Callback<Visit>() {
                    @Override
                    public void onResponse(Visit response) {
                        visit = response;
                        view.showSummary(response, plan);
                    }

                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INSPECTION_SUMMARY, error.toString());
                        view.showSnackbar(R.string.message_get_data_error);
                    }
                });
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_GET_PLAN, error.toString());
                view.showSnackbar(R.string.message_get_data_error);
            }
        });
    }

    @Override
    public void save(String comments) {
        visit.comments = comments;
        visitFinish.execute(visit, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.visitSaved();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INSPECTION_FINISH, error.toString());
                view.showSnackbar(R.string.visit_save_error);
            }
        });
    }

    @Override
    public void saveQrCode(String qrCode) {
        houseUpdateQR.execute(visit.house.uuid, qrCode, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.showSnackbar(R.string.qr_saved);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INSPECTION_UPDATEQR, error.toString());
                view.showSnackbar(R.string.visit_save_qr_error);
            }
        });
    }
}