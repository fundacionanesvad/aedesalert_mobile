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
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Calendar;
import java.util.UUID;

@Table(databaseName = SapevDatabase.NAME)
public class Visit extends BaseModel implements Parcelable {

    public static final int RESULT_INSPECTION = 2001;
    public static final int RESULT_CLOSED = 2002;
    public static final int RESULT_RELUCTANT = 2003;
    public static final int RESULT_DESERTED = 2004;

    @Column
    @PrimaryKey
    public String uuid;

    @Column
    public Calendar date;

    @Column
    public Integer inspected;

    @Column
    public Integer focus;

    @Column
    public Integer treated;

    @Column
    public Integer destroyed;

    @Column
    public Integer samples;

    @Column
    public Double larvicide;

    @Column
    public Integer feverish;

    @Column
    public String comments;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "resultId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element result;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "houseUuid",
            columnType = String.class,
            foreignColumnName = "uuid")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public House house;

     public Visit() {
        uuid = UUID.randomUUID().toString();
        date = Calendar.getInstance();
        inspected = 0;
        focus = 0;
        treated = 0;
        destroyed = 0;
        samples = 0;
        larvicide = 0.0;
        feverish = 0;
        comments = "";
        result = new Element();
        house = new House();
    }

    public Visit(Parcel in) {
        uuid = in.readString();
        date = Calendar.getInstance();
        date.setTimeInMillis(in.readLong());
        inspected = in.readInt();
        focus = in.readInt();
        treated = in.readInt();
        destroyed = in.readInt();
        samples = in.readInt();
        larvicide = in.readDouble();
        feverish = in.readInt();
        comments = in.readString();
        result = in.readParcelable(Element.class.getClassLoader());
        house = new House();
        house.uuid = in.readString();
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(uuid);
        parcel.writeLong(date.getTimeInMillis());
        parcel.writeInt(inspected);
        parcel.writeInt(focus);
        parcel.writeInt(treated);
        parcel.writeInt(destroyed);
        parcel.writeInt(samples);
        parcel.writeDouble(larvicide);
        parcel.writeInt(feverish);
        parcel.writeString(comments);
        parcel.writeParcelable(result, 0);
        parcel.writeString(house.uuid);
    }
}