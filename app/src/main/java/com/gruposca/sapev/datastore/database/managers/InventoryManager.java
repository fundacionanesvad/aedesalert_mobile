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
import com.gruposca.sapev.datastore.database.model.Inventory;
import com.gruposca.sapev.datastore.database.model.Inventory$Table;
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import javax.inject.Inject;

public class InventoryManager {

    @Inject
    public InventoryManager() {}

    public Inventory getInventory(Integer containerId, String visitUuid) {
        return new Select()
                .from(Inventory.class)
                .where(Condition.column(Inventory$Table.VISIT_VISITUUID).eq(visitUuid))
                .and(Condition.column(Inventory$Table.CONTAINER_CONTAINERID).eq(containerId))
                .querySingle();
    }

    public List<InventoryListQuery> getInventories(String visitUuid) {
        return new Select()
                .from(Inventory.class)
                .join(Element.class, Join.JoinType.LEFT)
                .on(Condition.column(ColumnAlias.columnWithTable(Inventory$Table.TABLE_NAME, Inventory$Table.CONTAINER_CONTAINERID))
                        .eq(ColumnAlias.columnWithTable(Element$Table.TABLE_NAME, Element$Table.ID)))
                .where(Condition.column(Inventory$Table.VISIT_VISITUUID).eq(visitUuid))
                .queryCustomList(InventoryListQuery.class);
    }

    public void saveInventory(Inventory inventory) {
        inventory.save();
    }
}