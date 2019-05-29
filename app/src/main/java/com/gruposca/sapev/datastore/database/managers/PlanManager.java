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

import com.gruposca.sapev.datastore.database.model.Plan;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

@Singleton
public class PlanManager {

    @Inject
    public PlanManager() { }

    public Boolean hasPlan() {
        return new Select()
                .count()
                .from(Plan.class)
                .count() != 0;
    }

    public Plan getPlan() {
        return new Select()
                .from(Plan.class)
                .querySingle();
    }

    public void deletePlan() {
        new Delete()
                .from(Plan.class)
                .query();
    }

    public void savePlan(Plan plan) {
        plan.save();
    }
}