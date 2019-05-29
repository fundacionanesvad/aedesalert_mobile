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

package com.gruposca.sapev.datastore.database.migrations;

import com.gruposca.sapev.datastore.database.SapevDatabase;
import com.gruposca.sapev.datastore.database.model.Area;
import com.gruposca.sapev.tool.Logs;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Migration(version = 5, databaseName = SapevDatabase.NAME)
public class Migration5Area extends AlterTableMigration<Area> {

    public Migration5Area() {
        super(Area.class);
    }

    @Override
    public void onPreMigrate() {
        Logs.log(Logs.WARN, "Migration5Area", "onPreMigrate");
        addColumn(Boolean.class, "substitute");
        addColumn(Integer.class, "pin");
    }
}