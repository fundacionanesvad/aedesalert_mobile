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

@Table(databaseName = SapevDatabase.NAME)
public class Plan extends BaseModel {

    @Column
    @PrimaryKey
    public int id;

    @Column
    public int progress;

    @Column
    public int planSize;

    @Column
    public Integer reconversionScheduleId;

    @Column
    public String larvicideName;

    @Column
    public String larvicideUnity;

    @Column
    public Double larvicideDose;

    @Column
    public String larvicideDoseName;

    @Column
    public Integer larvicideWaterVolume;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "typeId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element type;

    public Plan () {
        this.type = new Element();
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getProgress() {return progress;}
    public void setProgress(int progress) {this.progress = progress;}

    public int getPlanSize() {return planSize;}
    public void setPlanSize(int planSize) {this.planSize = planSize;}
}