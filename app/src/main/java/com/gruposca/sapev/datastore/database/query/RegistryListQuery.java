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

package com.gruposca.sapev.datastore.database.query;

import com.gruposca.sapev.datastore.database.SapevDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;

import java.util.Calendar;

@QueryModel(databaseName = SapevDatabase.NAME)
public class RegistryListQuery extends BaseQueryModel {

    @Column
    public String uuid;

    @Column
    public Boolean focus;

    @Column
    public Boolean treated;

    @Column
    public Boolean destroyed;

    @Column
    public String sample;

    @Column
    public Integer packets;

    @Column
    public Calendar date;

    @Column
    public Integer units;

    @Column(name = "name")
    public String containerName;

    @Column
    public String comment;
}