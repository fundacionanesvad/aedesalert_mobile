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

import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.PersonGet;
import com.gruposca.sapev.interactor.PersonSave;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PersonDetailPresenterImpl implements PersonDetailPresenter {

    private PersonDetailPresenter.View view;
    private final PersonGet personGet;
    private final PersonSave personSave;
    private Person person;

    @Inject
    public PersonDetailPresenterImpl(PersonGet personGet, PersonSave personSave) {
        this.personGet = personGet;
        this.personSave = personSave;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void initialize(String personUuid, String houseUuid) {
        if (personUuid.equals("")) {
            person = new Person();
            person.house.uuid = houseUuid;
            view.showPerson(person);
            view.showBirthday(person);
        } else {
            personGet.execute(personUuid, new Callback<Person>() {
                @Override
                public void onResponse(Person response) {
                    person = response;
                    view.showPerson(person);
                    view.showBirthday(person);
                }

                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSON_GET, error.toString());
                    view.onCanceled();
                }
            });
        }
    }

    @Override
    public void setName(String name) {
        person.name = name;
    }

    @Override
    public void setCardId(String cardId) {
        person.cardId = cardId;
    }

    @Override
    public void setGenre(String genre) {
        person.genre = genre;
        view.showPerson(person);
    }

    @Override
    public void setAge() {
        if (person.birthdayExact) {
            person.birthdayExact = false;
            person.birthday = null;
        }
        view.showBirthday(person);
    }

    @Override
    public Calendar setAge(Integer age) {
        person.birthdayExact = false;
        Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, -age);
        person.birthday = now;
        return person.birthday;
    }

    @Override
    public void setBirthday() {
        if (!person.birthdayExact) {
            person.birthdayExact = true;
            person.birthday = null;
        }
        view.showBirthday(person);
    }

    @Override
    public void setBirthday(Calendar birthday) {
        person.birthdayExact = true;
        person.birthday = birthday;
    }

    @Override
    public void cancel() {
        view.onCanceled();
    }

    @Override
    public String getPersonUuid() {
        return person.uuid;
    }

    @Override
    public void save() {
        personSave.execute(person, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                view.onSaved();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PERSON_SAVE, error.toString());
                view.onCanceled();
            }
        });
    }
}