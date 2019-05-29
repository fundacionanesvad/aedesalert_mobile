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

package com.gruposca.sapev.interactor;

import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.PersonRepository;

import javax.inject.Inject;

public class PersonSave extends AbsInteractor<Boolean> {

    protected PersonRepository personRepository;
    protected Person person;

    @Inject
    public PersonSave(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, PersonRepository personRepository) {
        super(threadExecutor, postExecutionThread);
        this.personRepository = personRepository;
    }

    public void execute(Person person, Callback<Boolean> callback) {
        this.person = person;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            personRepository.savePerson(person);
            notifyResponse(true);
        } catch (Exception e) {
            notifyError(e);
        }
    }
}