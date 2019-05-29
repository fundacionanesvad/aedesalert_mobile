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

package com.gruposca.sapev.datastore.database.model;

import com.gruposca.sapev.datastore.database.SapevDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Calendar;
import java.util.UUID;

@Table(databaseName = SapevDatabase.NAME)
public class Person extends BaseModel {

    public static final String GENRE_MALE = "M";
    public static final String GENRE_FEMALE = "F";

    @Column
    @PrimaryKey
    public String uuid;

    @Column
    public String name;

    @Column
    public String cardId;

    @Column
    public String genre;

    @Column
    public Calendar birthday;

    @Column
    public Boolean birthdayExact;

    @Column
    public Boolean enabled;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "houseUuid",
            columnType = String.class,
            foreignColumnName = "uuid")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public House house;

    public Person() {
        uuid = UUID.randomUUID().toString();
        name = "";
        cardId = "";
        genre = GENRE_MALE;
        birthday = null;
        birthdayExact = true;
        enabled = true;
        house = new House();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Person)
            return ((Person)o).uuid.equals(uuid);
        else
            return super.equals(o);
    }

    public Integer getAge() {
        Integer age;
        Calendar now = Calendar.getInstance();
        if (birthdayExact) {
            age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
            if (birthday.get(Calendar.MONTH) > now.get(Calendar.MONTH) ||
                    (birthday.get(Calendar.MONTH) == now.get(Calendar.MONTH) && birthday.get(Calendar.DATE) > now.get(Calendar.DATE))) {
                age--;
            }
        } else {
            age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        }
        return age;
    }
}