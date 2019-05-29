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
public class House extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey
    public String uuid;

    @Column
    public String code;

    @Column
    public String qrCode;

    @Column
    public Double latitude;

    @Column
    public Double longitude;

    @Column
    public String streetName;

    @Column
    public String streetNumber;

    @Column
    public Integer number;

    @Column
    public Calendar lastVisitDate;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "lastVisitResultId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element lastVisitResult;

    @Column
    public Boolean visited;

    @Column
    public Boolean visitInProgress;

    @Column
    public Boolean temporary;

    @Column
    public Boolean focus;

    @Column
    public Boolean feverish;

    @Column
    public Integer personsNumber;

    @Column
    public Boolean isNewHouse;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "areaId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Area area;

    @Column
    public Calendar iniVisitDate;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "iniVisitResultId",
            columnType = Integer.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE)
    public Element iniVisitResult;

    @Column
    public Integer iniVisitScheduleId;

    public House() {
        uuid = UUID.randomUUID().toString();
        code = "";
        qrCode = "";
        latitude = null;
        longitude = null;
        streetName = "";
        streetNumber = "";
        number = 0;
        lastVisitDate = null;
        lastVisitResult = new Element();
        visited = false;
        visitInProgress = false;
        temporary = false;
        focus = false;
        feverish = false;
        personsNumber = 0;
        isNewHouse = true;
        area = new Area();
        iniVisitDate = null;
        iniVisitResult = new Element();
        iniVisitScheduleId = null;
    }

    public House(Parcel in) {
        uuid = in.readString();
        code = in.readString();
        qrCode = in.readString();
        if (in.readInt() == 1) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }
        streetName = in.readString();
        streetNumber = in.readString();
        number = in.readInt();
        if (in.readInt() == 1) {
            lastVisitDate = Calendar.getInstance();
            lastVisitDate.setTimeInMillis(in.readLong());
            lastVisitResult = in.readParcelable(Element.class.getClassLoader());
        }
        visited = in.readInt() == 1;
        visitInProgress = in.readInt() == 1;
        temporary = in.readInt() == 1;
        focus = in.readInt() == 1;
        feverish = in.readInt() == 1;
        personsNumber = in.readInt();
        isNewHouse = in.readInt() == 0;
        area = new Area();
        area.id = in.readInt();
        if (in.readInt() == 1) {
            iniVisitDate = Calendar.getInstance();
            iniVisitDate.setTimeInMillis(in.readLong());
            iniVisitResult = in.readParcelable(Element.class.getClassLoader());
            if (in.readInt() == 1)
                iniVisitScheduleId = in.readInt();
        }
    }

    public String getUbigeo() {
        return code.substring(0, code.length()-4);
    }


    public static final Creator<House> CREATOR = new Creator<House>() {
        @Override
        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        @Override
        public House[] newArray(int size) {
            return new House[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(uuid);
        parcel.writeString(code);
        parcel.writeString(qrCode);
        parcel.writeInt(latitude != null ? 1 : 0);
        if (latitude != null) {
            parcel.writeDouble(latitude);
            parcel.writeDouble(longitude);
        }
        parcel.writeString(streetName);
        parcel.writeString(streetNumber);
        parcel.writeInt(number);
        parcel.writeInt(lastVisitDate != null ? 1 : 0);
        if (lastVisitDate != null) {
            parcel.writeLong(lastVisitDate.getTimeInMillis());
            parcel.writeParcelable(lastVisitResult, 0);
        }
        parcel.writeInt(visited ? 1 : 0);
        parcel.writeInt(visitInProgress ? 1 : 0);
        parcel.writeInt(temporary ? 1 : 0);
        parcel.writeInt(focus ? 1 : 0);
        parcel.writeInt(feverish ? 1 : 0);
        parcel.writeInt(personsNumber);
        parcel.writeInt(isNewHouse ? 0 : 1);
        parcel.writeInt(area.id);
        parcel.writeInt(iniVisitDate != null ? 1 : 0);
        if (iniVisitDate != null) {
            parcel.writeLong(iniVisitDate.getTimeInMillis());
            parcel.writeParcelable(iniVisitResult, 0);
            parcel.writeInt(iniVisitScheduleId != null ? 1 : 0);
            if (iniVisitScheduleId != null)
                parcel.writeInt(iniVisitScheduleId);
        }
    }
}