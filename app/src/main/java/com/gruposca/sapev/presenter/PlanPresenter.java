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

package com.gruposca.sapev.presenter;

import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;

import java.util.List;

public interface PlanPresenter {

    void initialize(View view);
    void load();
    void deletePlan();
    void existsVisits();
    void sendLogs();
    void unlockArea(Integer pin);

    interface View {
        void showLoading();
        void showAreas(List<AreaListQuery> areas);
        void updateTitle(Plan plan);
        void updateSubtitle(long inspections);
        void updateSector(String sector);
        void showSnackbar(int resId);
        void showLogin();
        void confirmSyncPlan();
        void showNotVisits();
    }
}