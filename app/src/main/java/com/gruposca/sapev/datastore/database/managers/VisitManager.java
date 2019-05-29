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
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.model.Visit$Table;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;


import java.util.List;

import javax.inject.Inject;

public class VisitManager {

    @Inject
    public VisitManager() {

    }

    public Visit getVisit(String visitUuid) {
        return new Select()
                .from(Visit.class)
                .where(Condition.column(Visit$Table.UUID).eq(visitUuid))
                .querySingle();
    }

    public Visit getVisitByHouse(String houseUuid) {
        return new Select()
                .from(Visit.class)
                .where(Condition.column(Visit$Table.HOUSE_HOUSEUUID).eq(houseUuid))
                .querySingle();
    }

    public void saveVisit(Visit visit) {
        visit.save();
    }

    public void deleteVisitByHouse(String houseUuid) {
        getVisitByHouse(houseUuid).delete();
    }

    public void deleteVisit(String visitUuid) {
        getVisit(visitUuid).delete();
    }

    public boolean existsVisits() {
       return new Select()
               .count()
               .from(Visit.class)
               .join(House.class, Join.JoinType.INNER)
               .on(Condition.column(ColumnAlias.columnWithTable(Visit$Table.TABLE_NAME, Visit$Table.HOUSE_HOUSEUUID))
                       .eq(ColumnAlias.columnWithTable(House$Table.TABLE_NAME, House$Table.UUID)))
               .where(Condition.column(House$Table.VISITED).eq(true)).count() != 0;
    }

}