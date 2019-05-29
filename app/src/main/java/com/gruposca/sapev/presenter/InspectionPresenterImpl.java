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
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.VisitGetByHouse;
import com.gruposca.sapev.interactor.VisitSave;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;

public class InspectionPresenterImpl implements InspectionPresenter {

    private final VisitSave visitSave;
    private final VisitGetByHouse visitGetByHouse;

    private InspectionPresenter.View view;
    private String houseUuid;

    private Visit visit;

    @Inject
    public InspectionPresenterImpl(VisitSave visitSave, VisitGetByHouse visitGetByHouse) {
        this.visitSave = visitSave;
        this.visitGetByHouse = visitGetByHouse;
    }

    @Override
    public void initialize(View view, String houseUuid) {
        this.view = view;
        this.houseUuid = houseUuid;
    }

    @Override
    public void load() {
        view.showLoading();
        if (visit == null) {
            visitGetByHouse.execute(houseUuid, new Callback<Visit>() {
                @Override
                public void onResponse(Visit response) {
                    if (response == null) {
                        visit = new Visit();
                        visit.result = new Element(Visit.RESULT_INSPECTION);
                        visit.house.uuid = houseUuid;
                        visitSave.execute(visit, new Callback<Void>() {
                            @Override
                            public void onResponse(Void response) {
                                view.showInspection(visit);
                            }

                            @Override
                            public void onError(ErrorBundle error) {
                                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INSPECTION_SAVE, error.toString());
                            }
                        });
                    } else {
                        visit = response;
                        view.showInspection(visit);
                    }
                }

                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INSPECTION_GETBYHOUSE, error.toString());
                }
            });
        } else {
            view.showInspection(visit);
        }
    }

    @Override
    public void validate() {
        view.showSummary(visit.uuid);
    }


}