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
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.datastore.database.model.Registry$Table;
import com.gruposca.sapev.datastore.database.query.RegistryListQuery;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import javax.inject.Inject;

public class RegistryManager {

    @Inject
    public RegistryManager() { }

    public List<RegistryListQuery> getRegistries(String visitUuid) {
        return new Select()
                .from(Registry.class)
                .join(Element.class, Join.JoinType.INNER)
                .on(Condition.column(ColumnAlias.columnWithTable(Registry$Table.TABLE_NAME, Registry$Table.CONTAINER_CONTAINERID))
                        .eq(ColumnAlias.columnWithTable(Element$Table.TABLE_NAME, Element$Table.ID)))
                .where(Condition.column(Registry$Table.VISIT_VISITUUID).eq(visitUuid))
                .and(Condition.column(Registry$Table.DELETED).eq(false))
                .orderBy(false, Registry$Table.DATE)
                .queryCustomList(RegistryListQuery.class);
    }

    public Registry getRegistry(String uuid) {
        return new Select()
                .from(Registry.class)
                .where(Condition.column(Registry$Table.UUID).eq(uuid))
                .querySingle();
    }

    public void saveRegistry(Registry registry) {
        registry.save();
    }

    public List<Registry> getSamplesByContainerId(Integer containerId, String visitUuid) {
        return new Select()
                .from(Registry.class)
                .where(Condition.column(Registry$Table.CONTAINER_CONTAINERID).eq(containerId))
                .and(Condition.column(Registry$Table.VISIT_VISITUUID).eq(visitUuid))
                .and(Condition.column(Registry$Table.SAMPLE).isNotNull())
                .and(Condition.column(Registry$Table.SAMPLE).isNot(""))
                .and(Condition.column(Registry$Table.DELETED).eq(0))
                .queryList();
    }

    public Boolean hasSample(String sampleCode) {
        return new Select()
                .count()
                .from(Registry.class)
                .where(Condition.column(Registry$Table.SAMPLE).eq(sampleCode))
                .and(Condition.column(Registry$Table.DELETED).is(false))
                .count() != 0;
    }
}