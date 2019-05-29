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

package com.gruposca.sapev.datastore.database.managers;

import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Person$Table;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.datastore.database.model.Symptom$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import javax.inject.Inject;

public class PersonManager {

    @Inject
    public PersonManager() { }

    public void savePerson(Person person) {
        person.save();
    }

    public Person getPerson(String personUuid) {
        return new Select()
                .from(Person.class)
                .where(Condition.column(Person$Table.UUID).eq(personUuid))
                .querySingle();
    }

    public List<Person> getPersons(String houseUuid) {
        return new Select()
                .from(Person.class)
                .where(Condition.column(Person$Table.HOUSE_HOUSEUUID).eq(houseUuid))
                .and(Condition.column(Person$Table.ENABLED).eq(true))
                .orderBy(Person$Table.NAME)
                .queryList();
    }


    public List<Person> getAllPersons(String houseUuid) {
        return new Select()
                .from(Person.class)
                .where(Condition.column(Person$Table.HOUSE_HOUSEUUID).eq(houseUuid))
                .orderBy(Person$Table.NAME)
                .queryList();
    }

    public List<Person> getPersonsFeverish(String visitUuid) {
        return new Select(ColumnAlias.columnWithTable("P", Person$Table.UUID),
                ColumnAlias.columnWithTable("P", Person$Table.NAME),
                ColumnAlias.columnWithTable("P", Person$Table.GENRE),
                ColumnAlias.columnWithTable("P", Person$Table.BIRTHDAY),
                ColumnAlias.columnWithTable("P", Person$Table.BIRTHDAYEXACT),
                ColumnAlias.columnWithTable("P", Person$Table.ENABLED),
                ColumnAlias.columnWithTable("P", Person$Table.HOUSE_HOUSEUUID))
                .from(Person.class).as("P")
                .join(Symptom.class, Join.JoinType.INNER).as("S")
                .on(Condition.column("S." + Symptom$Table.PERSON_PERSONUUID).eq("P." + Person$Table.UUID),
                        Condition.column("S." + Symptom$Table.SELECTED).eq(1))
                //.on(Condition.column(ColumnAlias.columnWithTable("P", Person$Table.UUID)).eq("S." + Symptom$Table.PERSON_PERSONUUID))
                .where(Condition.column(Symptom$Table.VISIT_VISITUUID).eq(visitUuid))
                .and(Condition.column(Person$Table.ENABLED).eq(true))
                .orderBy("P." + Person$Table.NAME)
                .groupBy(ColumnAlias.columnWithTable("P", Person$Table.UUID))
                .queryList();
    }

    public List<Person> getPersonsFeverless(String houseUuid, String visitUuid) {
        return new Select(ColumnAlias.columnWithTable("P", Person$Table.UUID),
                ColumnAlias.columnWithTable("P", Person$Table.NAME),
                ColumnAlias.columnWithTable("P", Person$Table.GENRE),
                ColumnAlias.columnWithTable("P", Person$Table.BIRTHDAY),
                ColumnAlias.columnWithTable("P", Person$Table.BIRTHDAYEXACT),
                ColumnAlias.columnWithTable("P", Person$Table.ENABLED),
                ColumnAlias.columnWithTable("P", Person$Table.HOUSE_HOUSEUUID))
                .from(Person.class).as("P")
                .join(Symptom.class, Join.JoinType.LEFT).as("S")
                .on(Condition.column("S." + Symptom$Table.PERSON_PERSONUUID).eq("P." + Person$Table.UUID),
                        Condition.column("S." + Symptom$Table.SELECTED).eq(1))
                .where(Condition.column("P." + Person$Table.HOUSE_HOUSEUUID).eq(houseUuid))
                .and(Condition.column("P." + Person$Table.ENABLED).eq(true))
                .and(Condition.column("S." + Symptom$Table.SYMPTOM_SYMPTOMID).isNull())
                        //.or(Condition.column("S." + Visit$Table.UUID).isNot(visitUuid))
                .orderBy("P." + Person$Table.NAME)
                .groupBy(ColumnAlias.columnWithTable("P", Person$Table.UUID))
                .queryList();
    }

    public boolean isFeverish(String personUuid, String visitUuid) {
        return new Select()
                .count()
                .from(Symptom.class)
                .where(Condition.column(Symptom$Table.PERSON_PERSONUUID).eq(personUuid))
                .and(Condition.column(Symptom$Table.VISIT_VISITUUID).eq(visitUuid))
                .and(Condition.column(Symptom$Table.SELECTED).eq(true))
                .count() != 0;
    }
}