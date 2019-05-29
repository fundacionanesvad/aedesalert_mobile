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

import android.os.Parcel;
import android.os.Parcelable;

import com.gruposca.sapev.datastore.database.SapevDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.raizlabs.android.dbflow.structure.BaseQueryModel;

import java.util.Calendar;
import java.util.Locale;

@QueryModel(databaseName = SapevDatabase.NAME)
public class HouseListQuery extends BaseQueryModel implements Parcelable {

    @Column
    public String uuid;

    @Column
    public Integer number;

    @Column
    public String code;

    @Column
    public String streetName;

    @Column
    public String streetNumber;

    @Column
    public Calendar lastVisitDate;

    @Column
    public Integer lastVisitResultId;

    @Column(name = "name")
    public String lastVisitResultName;

    @Column
    public Boolean visited;

    @Column
    public Boolean visitInProgress;

    @Column
    public Double latitude;

    @Column
    public Double longitude;

    @Column
    public String qrCode;

    @Column
    public Boolean focus;

    @Column
    public Boolean feverish;

    @Column
    public Integer personsNumber;

    public HouseListQuery() {}

    public HouseListQuery(Parcel in) {
        uuid = in.readString();
        number = in.readInt();
        code = in.readString();
        streetName = in.readString();
        streetNumber = in.readString();
        if (in.readInt() == 1) {
            lastVisitDate = Calendar.getInstance();
            lastVisitDate.setTimeInMillis(in.readLong());
            lastVisitResultId = in.readInt();
            lastVisitResultName = in.readString();
        }
        visited = in.readInt() == 1;
        visitInProgress = in.readInt() == 1;
        if (in.readInt() == 1) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }
        qrCode = in.readString();
        focus = in.readInt() == 1;
        feverish = in.readInt() == 1;
        personsNumber = in.readInt();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%04d - %s, %s", number, streetName, streetNumber);
    }

    private String streetNumberFormatted;
    public String getStreetNumberFormatted() {
        if (streetNumberFormatted == null) {
            try {
                int value = Integer.parseInt(streetNumber);
                streetNumberFormatted = String.format(Locale.getDefault(), "$1%04d", value);
            } catch (NumberFormatException e) {
                streetNumberFormatted = streetNumber;
            }
        }
        return streetNumberFormatted;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof HouseListQuery)
            return ((HouseListQuery)o).uuid.equals(uuid);
        else
            return super.equals(o);
    }

    public static final Creator<HouseListQuery> CREATOR = new Creator<HouseListQuery>() {
        @Override
        public HouseListQuery createFromParcel(Parcel in) {
            return new HouseListQuery(in);
        }

        @Override
        public HouseListQuery[] newArray(int size) {
            return new HouseListQuery[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeInt(number);
        parcel.writeString(code);
        parcel.writeString(streetName);
        parcel.writeString(streetNumber);
        parcel.writeInt(lastVisitDate != null ? 1 : 0);
        if (lastVisitDate != null) {
            parcel.writeLong(lastVisitDate.getTimeInMillis());
            parcel.writeInt(lastVisitResultId);
            parcel.writeString(lastVisitResultName);
        }
        parcel.writeInt(visited ? 1 : 0);
        parcel.writeInt(visitInProgress ? 1 : 0);
        parcel.writeInt(latitude != null ? 1 : 0);
        if (latitude != null) {
            parcel.writeDouble(latitude);
            parcel.writeDouble(longitude);
        }
        parcel.writeString(qrCode);
        parcel.writeInt(focus ? 1 : 0);
        parcel.writeInt(feverish ? 1 : 0);
        parcel.writeInt(personsNumber);
    }
}