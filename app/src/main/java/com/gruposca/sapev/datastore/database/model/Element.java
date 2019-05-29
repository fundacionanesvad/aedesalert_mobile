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

import android.os.Parcel;
import android.os.Parcelable;

import com.gruposca.sapev.datastore.database.SapevDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = SapevDatabase.NAME)
public class Element extends BaseModel implements Parcelable {

    public static final int TABLE_CONTAINERS = 4;
    public static final int TABLE_STATES = 5;
    public static final int TABLE_SYMPTOMS = 6;

    @Column
    @PrimaryKey
    public Integer id;

    @Column
    public String name;

    @Column
    public Integer sort;

    @Column
    public Integer tableId;

    public Boolean checked;

    public Element() {
        checked = false;
    }

    public Element(Integer id) {
        this.id = id;
        this.checked = false;
    }

    public Element(Parcel in) {
        id = in.readInt();
        name = in.readString();
        sort = in.readInt();
        tableId = in.readInt();
        checked = false;
    }

    public static final Creator<Element> CREATOR = new Creator<Element>() {
        @Override
        public Element createFromParcel(Parcel in) {
            return new Element(in);
        }

        @Override
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(sort);
        parcel.writeInt(tableId);
    }
}