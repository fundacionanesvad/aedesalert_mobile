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

import java.util.UUID;

@Table(databaseName = SapevDatabase.NAME)
public class Symptom extends BaseModel {

    @Column
    @PrimaryKey
    public String uuid;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "symptomId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element symptom;

    @Column
    public Boolean selected;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "personUuid",
            columnType = String.class,
            foreignColumnName = "uuid")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Person person;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "visitUuid",
            columnType = String.class,
            foreignColumnName = "uuid")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Visit visit;

    public Symptom() {
        uuid = UUID.randomUUID().toString();
        symptom = new Element();
        selected = false;
        person = new Person();
        visit = new Visit();
    }
}