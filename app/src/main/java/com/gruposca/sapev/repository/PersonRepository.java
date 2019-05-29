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

package com.gruposca.sapev.repository;

import com.gruposca.sapev.datastore.database.managers.PersonManager;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.net.model.PersonResult;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PersonRepository {

    private final PersonManager personManager;

    @Inject
    public PersonRepository(PersonManager personManager) {
        this.personManager = personManager;
    }

    public Person getPerson(String personUuid) {
        return personManager.getPerson(personUuid);
    }

    public void savePerson(Person person) {
        personManager.savePerson(person);
    }

    public void savePersons(String houseUuid, List<PersonResult> persons) {
        if (persons != null) {
            for (PersonResult item : persons) {
                Person person = new Person();
                person.uuid = item.uuid;
                person.name = item.name;
                person.genre = item.genre;
                person.birthday = item.birthday;
                person.birthdayExact = item.birthdayExact;
                person.enabled = item.enabled;
                person.cardId = item.cardId;
                person.house.uuid = houseUuid;
                savePerson(person);
            }
        }
    }

    public List<Person> getPersons(String houseUuid) {
        return personManager.getPersons(houseUuid);
    }

    public List<Person> getPersonsFeverish(String visitUuid) {
        return personManager.getPersonsFeverish(visitUuid);
    }

    public List<Person> getPersonsFeverless(String houseUuid, String visitUuid) {
        return personManager.getPersonsFeverless(houseUuid, visitUuid);
    }

    public Boolean checkPersonsIsFeverish(String personUuid, String visitUuid) {
        return personManager.isFeverish(personUuid, visitUuid);
    }
}