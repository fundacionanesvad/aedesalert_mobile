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
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.FeverishGetList;
import com.gruposca.sapev.interactor.SaveSymptoms;
import com.gruposca.sapev.interactor.VisitDelete;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FeverishListPresenterImpl implements FeverishListPresenter {

    private View view;
    private final FeverishGetList feverishGetList;
    private final SaveSymptoms saveSymptoms;
    private String personUuid;
    private String visitUuid;
    private Boolean recommendationShown;
    private final VisitDelete visitDelete;

    @Inject
    public FeverishListPresenterImpl(FeverishGetList feverishGetList, SaveSymptoms saveSymptoms, VisitDelete visitDelete) {
        this.feverishGetList = feverishGetList;
        this.saveSymptoms = saveSymptoms;
        this.visitDelete = visitDelete;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void initialize(View view, String visitUuid) {
        this.view = view;
        this.view.showLoading();
        this.visitUuid = visitUuid;
        this.recommendationShown = false;
    }

    @Override
    public void load() {
        feverishGetList.execute(visitUuid, new Callback<List<Person>>() {
            @Override
            public void onResponse(List<Person> response) {
                view.showFeverish(response);
            }
            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(getClass().getSimpleName(), Errors.METHOD_FEBRILES_GETLIST, error.toString());
            }
        });
    }

    @Override
    public void setPerson(String personUuid) {
        this.personUuid = personUuid;
        view.showSymptoms(personUuid);
    }

    @Override
    public void showFeverish() {
        view.showFeverishPersons();
    }

    @Override
    public void saveSymptoms(String visitUuid, final List<Symptom> listSymptoms) {
        this.visitUuid = visitUuid;
        saveSymptoms.execute(listSymptoms, visitUuid, personUuid, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (recommendationShown) {
                    view.showFeverishList();
                } else {
                    int count = 0;
                    for (Symptom symptom : listSymptoms) {
                        if (symptom.selected)
                            count++;
                    }
                    if (count > 2) {
                        recommendationShown = true;
                        view.showRecommendations();
                    } else {
                        view.showFeverishList();
                    }
                }
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_SAVE_SYMPTOMS, error.toString());
                view.showSnackbar(R.string.symptom_save_error);
            }
        });
    }

    @Override
    public void deleteVisit(String visitUuid) {
        visitDelete.execute(visitUuid, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.onDeleteVisit();
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_FEBRILES_DELETEVISIT, error.toString());
                view.showSnackbar(R.string.visit_deleted_error);
            }
        });

    }
}