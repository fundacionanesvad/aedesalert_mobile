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
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.CheckPersonisFeverish;
import com.gruposca.sapev.interactor.HouseGet;
import com.gruposca.sapev.interactor.HouseSave;
import com.gruposca.sapev.interactor.HouseUpdatePersonNumber;
import com.gruposca.sapev.interactor.PersonGet;
import com.gruposca.sapev.interactor.PersonGetList;
import com.gruposca.sapev.interactor.PersonSave;
import com.gruposca.sapev.interactor.PersonUpdateState;
import com.gruposca.sapev.interactor.VisitDelete;
import com.gruposca.sapev.repository.PersonRepository;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PersonListPresenterImpl implements PersonListPresenter {

    private PersonListPresenter.View view;
    private String houseUuid;
    private final PersonGetList personGetList;
    private final HouseGet houseGet;
    private final HouseUpdatePersonNumber houseUpdatePersonNumber;
    private final CheckPersonisFeverish checkPersonisFeverish;
    private final VisitDelete visitDelete;
    private final PersonUpdateState personUpdateState;

    @Inject
    public PersonListPresenterImpl(PersonGetList personGetList, HouseGet houseGet, HouseUpdatePersonNumber houseUpdatePersonNumber, CheckPersonisFeverish checkPersonisFeverish, PersonUpdateState personUpdateState, VisitDelete visitDelete) {
        this.personGetList = personGetList;
        this.houseGet = houseGet;
        this.houseUpdatePersonNumber = houseUpdatePersonNumber;
        this.checkPersonisFeverish = checkPersonisFeverish;
        this.personUpdateState = personUpdateState;
        this.visitDelete = visitDelete;
    }

    @Override
    public void initialize(View view, String houseUuid) {
        this.view = view;
        this.houseUuid = houseUuid;
        this.view.showLoading();
    }

    @Override
    public void load() {
        personGetList.execute(houseUuid, new Callback<List<Person>>() {
            @Override
            public void onResponse(List<Person> response) {
                view.showPersons(response);
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(this.getClass().toString(), Errors.METHOD_PERSONLIST_GETLIST, error.toString());
            }
        });

        houseGet.execute(houseUuid, new Callback<House>() {
            @Override
            public void onResponse(House response) {
                view.showPersonsNumber(response.personsNumber);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSONLIST_GETHOUSE, error.toString());
            }
        });
    }

    @Override
    public void setPersonsNumber(final Integer persons) {
        houseUpdatePersonNumber.execute(houseUuid, persons, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {

            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSONLIST_UPDATENUMBER, error.toString());
                view.showSnackbar(R.string.message_error_info);
            }
        });
    }

    @Override
    public void checkPersonFeverish(final String personUuid, String visitUuid) {
        checkPersonisFeverish.execute(personUuid, visitUuid, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    view.showNotDeletePersonDialog();
                } else {
                    view.showDeletePersonConfirmDialog(personUuid);
                }
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSONLIST_CHECKFEBRILE, error.toString());
            }
        });
    }

    @Override
    public void update(String personUuid) {
        personUpdateState.execute(personUuid, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                load();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSONLIST_UPDATESTATE, error.toString());
                view.showSnackbar(R.string.message_error_info);
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
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSONLIST_DELETEVISIT, error.toString());
                view.showSnackbar(R.string.visit_deleted_error);
            }
        });
    }
}