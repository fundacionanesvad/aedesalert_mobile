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

import com.gruposca.sapev.datastore.database.model.Area;
import com.gruposca.sapev.datastore.database.model.Area$Table;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.House$Table;
import com.gruposca.sapev.datastore.database.model.Plan$Table;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.model.Visit$Table;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AreaManager {

    @Inject
    public AreaManager() { }

    public Area getArea(int id) {
        return new Select()
                .from(Area.class)
                .where(Condition.column(Plan$Table.ID).eq(id))
                .querySingle();
    }

    public List<AreaListQuery> getAreas() {
        List<AreaListQuery> areas = new Select()
                .from(Area.class)
                .where(Condition.column(Area$Table.SUBSTITUTE).eq(false))
                .orderBy(Area$Table.NAME)
                .queryCustomList(AreaListQuery.class);
        for(AreaListQuery area : areas) {
            area.inspections = new Select()
                    .count()
                    .from(House.class)
                    .join(Visit.class, Join.JoinType.LEFT)
                    .on(Condition.column(ColumnAlias.columnWithTable(House$Table.TABLE_NAME, House$Table.UUID))
                            .eq(ColumnAlias.columnWithTable(Visit$Table.TABLE_NAME, Visit$Table.HOUSE_HOUSEUUID)))
                    .where(Condition.column(ColumnAlias.columnWithTable(House$Table.TABLE_NAME, House$Table.AREA_AREAID)).eq(area.id))
                    .and(Condition.column(ColumnAlias.columnWithTable(Visit$Table.TABLE_NAME, Visit$Table.RESULT_RESULTID)).eq(Visit.RESULT_INSPECTION))
                    .count();
            area.sector="pepe";
        }
        return areas;
    }

    public Boolean unlockArea(int pin) {
        List<Area> areas = new Select()
                .from(Area.class)
                .where(Condition.column(Area$Table.PIN).eq(pin))
                .queryList();
        if (areas.size() != 0) {
            for(Area area : areas) {
                area.substitute = false;
                area.pin = null;
                area.save();
            }
            return true;
        }
        return false;
    }

    public void saveArea(Area area) {
        area.save();
    }
}