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
import com.gruposca.sapev.datastore.database.model.Inventory;
import com.gruposca.sapev.datastore.database.model.Inventory$Table;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.datastore.database.model.Registry$Table;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.model.Visit$Table;
import com.gruposca.sapev.datastore.database.query.HousesSummaryQuery;
import com.gruposca.sapev.datastore.database.query.InventorySummaryQuery;
import com.gruposca.sapev.datastore.database.query.SampleListQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import javax.inject.Inject;


public class VisitsSummaryManager {

    @Inject
    public VisitsSummaryManager() {
    }

    public HousesSummaryQuery getHousesSummary() {

        Plan plan = new Select()
                .from(Plan.class)
                .querySingle();

        List<HousesSummaryQuery> items =
                new Select(ColumnAlias.columnsWithFunction("SUM", "V."+Visit$Table.LARVICIDE).as("larvicide"),
                        ColumnAlias.columnsWithFunction("SUM", "V."+Visit$Table.FEVERISH).as("febriles"),
                        ColumnAlias.columnsWithFunction("SUM", "H."+House$Table.PERSONSNUMBER).as("persons"))
                        .from(Visit.class).as("V")
                        .join(House.class, Join.JoinType.INNER).as("H")
                        .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                        .where(Condition.column("V." + Visit$Table.INSPECTED).isNot(0))
                        .queryCustomList(HousesSummaryQuery.class);

        int treated = (int) new Select()
                .count()
                .from(Visit.class)
                .where(Condition.column(Visit$Table.TREATED).isNot(0)).count();

        int destroyed = (int) new Select()
                .count()
                .from(Visit.class)
                .where(Condition.column(Visit$Table.DESTROYED).isNot(0)).count();

        int closed =(int) new Select()
                 .count()
                 .from(Visit.class).as("V")
                 .join(House.class, Join.JoinType.INNER).as("H")
                 .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                 .where(Condition.column("H." + House$Table.LASTVISITRESULT_LASTVISITRESULTID).eq(Visit.RESULT_CLOSED)).count();

        int abandoned =(int) new Select()
                .count()
                .from(Visit.class).as("V")
                .join(House.class, Join.JoinType.INNER).as("H")
                .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                .where(Condition.column("H." + House$Table.LASTVISITRESULT_LASTVISITRESULTID).eq(Visit.RESULT_DESERTED)).count();

        int reluctant =(int) new Select()
                .count()
                .from(Visit.class).as("V")
                .join(House.class, Join.JoinType.INNER).as("H")
                .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                .where(Condition.column("H." + House$Table.LASTVISITRESULT_LASTVISITRESULTID).eq(Visit.RESULT_RELUCTANT)).count();

        int inspected =(int) new Select()
                .count()
                .from(Visit.class).as("V")
                .join(House.class, Join.JoinType.INNER).as("H")
                .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                .where(Condition.column("H." + House$Table.LASTVISITRESULT_LASTVISITRESULTID).eq(Visit.RESULT_INSPECTION)).count();

        int focus =(int) new Select()
                .count()
                .from(Visit.class)
                .where(Condition.column(Visit$Table.FOCUS).isNot(0)).count();

        int reconverted = (int)new Select()
                .count()
                .from(Visit.class).as("V")
                .join(House.class, Join.JoinType.INNER).as("H")
                .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                .where(Condition.column("H." + House$Table.LASTVISITRESULT_LASTVISITRESULTID).eq(Visit.RESULT_INSPECTION))
                .and(Condition.column("H." + House$Table.INIVISITRESULT_INIVISITRESULTID).in(Visit.RESULT_CLOSED, Visit.RESULT_RELUCTANT))
                .and(Condition.column("H." + House$Table.INIVISITSCHEDULEID).eq(plan.reconversionScheduleId))
                .count();

        HousesSummaryQuery housesSummaryQuery = new HousesSummaryQuery();
        housesSummaryQuery.larvicide = items.get(0).larvicide != null ? items.get(0).larvicide : 0;
        housesSummaryQuery.febriles = items.get(0).febriles != null ? items.get(0).febriles : 0;
        housesSummaryQuery.treated = treated;
        housesSummaryQuery.destroyed = destroyed;
        housesSummaryQuery.persons = items.get(0).persons != null ? items.get(0).persons : 0;
        housesSummaryQuery.closed = closed;
        housesSummaryQuery.abandoned = abandoned;
        housesSummaryQuery.reluctant = reluctant;
        housesSummaryQuery.focus = focus;
        housesSummaryQuery.inspected = inspected;
        housesSummaryQuery.reconverted = reconverted;

        return housesSummaryQuery;
    }


    public List<SampleListQuery> getSampleList() {
        return new Select("R."+Registry$Table.SAMPLE, "H."+House$Table.STREETNAME, "H."+House$Table.STREETNUMBER,"R."+Registry$Table.STATES)
                .from(Registry.class).as("R")
                .join(Visit.class, Join.JoinType.INNER).as("V")
                .on(Condition.column("R." + Registry$Table.VISIT_VISITUUID).eq("V." + Visit$Table.UUID))
                .join(House.class, Join.JoinType.INNER).as("H")
                .on(Condition.column("H." + House$Table.UUID).eq("V." + Visit$Table.HOUSE_HOUSEUUID))
                .where(Condition.column("R." + Registry$Table.SAMPLE).isNot(""))
                .orderBy("R."+Registry$Table.SAMPLE)
                .queryCustomList(SampleListQuery.class);
    }

    public List<InventorySummaryQuery> getInventoryList() {
        return new Select(
                ColumnAlias.columnsWithFunction("SUM", "I."+Inventory$Table.DESTROYED).as("destroyed"),
                ColumnAlias.columnsWithFunction("SUM", "I."+Inventory$Table.FOCUS).as("focus"),
                ColumnAlias.columnsWithFunction("SUM", "I."+Inventory$Table.INSPECTED).as("inspected"),
                ColumnAlias.columnsWithFunction("SUM", "I."+Inventory$Table.TREATED).as("treated"),
                ColumnAlias.column("E."+Element$Table.ID),
                ColumnAlias.column("E."+Element$Table.NAME))
                .from(Inventory.class).as("I")
                .join(Element.class, Join.JoinType.LEFT).as("E")
                .on(Condition.column("I." + Inventory$Table.CONTAINER_CONTAINERID).eq("E." + Element$Table.ID))
                .where().groupBy("E."+Element$Table.ID,"E."+Element$Table.NAME)
                .queryCustomList(InventorySummaryQuery.class);
    }
}