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
import com.gruposca.sapev.datastore.database.converters.IntegerListConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.TypeConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Table(databaseName = SapevDatabase.NAME)
public class Registry extends BaseModel {

    @Column
    @PrimaryKey
    public String uuid;

    @Column
    public String comment;

    @Column
    public Boolean focus;

    @Column
    public String sample;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "stateId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element state;

    @Column
    public Boolean treated;

    @Column
    public Integer packets;

    @Column
    public Boolean destroyed;

    @Column
    public Calendar date;

    @Column
    public Boolean deleted;

    @Column
    public Integer units;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "containerId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element container;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "visitUuid",
            columnType = String.class,
            foreignColumnName = "uuid")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Visit visit;

    @Column
    public IntegerList states;

    public Registry() {
        uuid = UUID.randomUUID().toString();
        comment = "";
        focus = false;
        sample = "";
        state = new Element();
        treated = false;
        packets = 0;
        destroyed = false;
        date = Calendar.getInstance();
        deleted = false;
        units = 1;
        container = new Element();
        visit = new Visit();
        states = new IntegerList();
    }
}