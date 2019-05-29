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

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.Element$Table;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.House$Table;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.gruposca.sapev.datastore.database.query.StreetNameQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import javax.inject.Inject;

public class HouseManager {

    @Inject
    public HouseManager() { }

    public House getHouse(String houseUuid) {
        return new Select()
                .from(House.class)
                .where(Condition.column(House$Table.UUID).eq(houseUuid))
                .querySingle();
    }

    public House getHouseByQR(String qrCode) {
        return new Select()
                .from(House.class)
                .where(Condition.column(House$Table.QRCODE).eq(qrCode))
                .and(Condition.column(House$Table.TEMPORARY).eq(false))
                .querySingle();
    }

    public List<HouseListQuery> getHouseList(int areaId) {
        return new Select()
                .from(House.class)
                .join(Element.class, Join.JoinType.LEFT)
                .on(Condition.column(ColumnAlias.columnWithTable(House$Table.TABLE_NAME, House$Table.LASTVISITRESULT_LASTVISITRESULTID))
                        .eq(ColumnAlias.columnWithTable(Element$Table.TABLE_NAME, Element$Table.ID)))
                .where(Condition.column(House$Table.AREA_AREAID).eq(areaId))
                .and(Condition.column(House$Table.TEMPORARY).eq(false))
                .queryCustomList(HouseListQuery.class);
    }

    public void saveHouse(House house) {
        house.save();
    }

    public List<StreetNameQuery> getStreetNames() {
        return new Select().distinct().rawColumns(House$Table.STREETNAME)
                .from(House.class)
                .where().orderBy(House$Table.STREETNAME)
                .queryCustomList(StreetNameQuery.class);
    }

    public Boolean hasVisitInProgress() {
        return new Select()
                .count()
                .from(House.class)
                .where(Condition.column(House$Table.VISITINPROGRESS).eq(true))
                .count() != 0;
    }

    public void deleteHouse(House house) {
        house.delete();
    }

}