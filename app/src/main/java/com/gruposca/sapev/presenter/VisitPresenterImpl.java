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

import android.os.Bundle;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.VisitCreate;
import com.gruposca.sapev.interactor.VisitDelete;
import com.gruposca.sapev.interactor.VisitDeleteByHouse;
import com.gruposca.sapev.interactor.VisitFinish;
import com.gruposca.sapev.interactor.VisitGetByHouse;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VisitPresenterImpl implements VisitPresenter {

    private static final String PARCEL_VISIT = "PARCEL_VISIT";

    private final VisitGetByHouse visitGetByHouse;
    private final VisitCreate visitCreate;
    private final VisitFinish visitFinish;
    private VisitPresenter.View view;
    private Visit visit;
    private String houseUuid;
    private Integer resultId;
    private final VisitDeleteByHouse visitDeleteByHouse;

    @Inject
    public VisitPresenterImpl(VisitGetByHouse visitGetByHouse, VisitCreate visitCreate, VisitFinish visitFinish, VisitDeleteByHouse visitDeleteByHouse) {
        this.visitGetByHouse = visitGetByHouse;
        this.visitCreate = visitCreate;
        this.visitFinish = visitFinish;
        this.visitDeleteByHouse = visitDeleteByHouse;
    }

    @Override
    public void initialize(View view, String houseUuid, Integer resultId) {
        this.view = view;
        this.houseUuid = houseUuid;
        this.resultId = resultId;
    }

    @Override
    public void load(Bundle state) {
        if (state == null) {
            visitGetByHouse.execute(houseUuid, new Callback<Visit>() {
                @Override
                public void onResponse(Visit response) {
                    if (response == null) {
                        visitCreate.execute(resultId, houseUuid, new Callback<Visit>() {
                            @Override
                            public void onResponse(Visit response) {
                                visit = response;
                                view.initToolbar(visit);
                            }

                            @Override
                            public void onError(ErrorBundle error) {
                                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_VISIT_CREATE, error.toString());
                            }
                        });
                    } else {
                        visit = response;
                        view.initToolbar(visit);
                    }
                }

                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_VISIT_GETBYHOUSE, error.toString());
                }
            });
        } else {
            visit = state.getParcelable(PARCEL_VISIT);
            view.initToolbar(visit);
        }
    }

    @Override
    public void saveState(Bundle state) {
        state.putParcelable(PARCEL_VISIT, visit);
    }


    @Override
    public void cancelVisit() {
        view.visitCanceled();
    }

    @Override
    public void saveVisit() {
        visitFinish.execute(visit, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.visitSaved();
            }

            @Override
            public void onError(ErrorBundle error) {
                ///TODO
            }
        });
    }

    @Override
    public void deleteVisit() {
        visitDeleteByHouse.execute(visit.house.uuid, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.onDeleteVisit();
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_VISIT_DELETE, error.toString());
            }
        });
    }
}