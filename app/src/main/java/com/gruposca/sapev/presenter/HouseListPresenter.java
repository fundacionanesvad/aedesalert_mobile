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

import android.os.Bundle;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import java.util.List;

public interface HouseListPresenter {

    int SORT_ADDRESS = 2;
    int SORT_DATE = 3;

    void initialize(View view, int areaId, long otherInspections);
    void load(Bundle state);
    void saveState(Bundle state);
    void findHouseByQR(String qrCode);
    void filterQuery(String value);
    void filterInspected(Boolean value);
    void filterRequalify(Boolean value);
    void filterFocus(Boolean value);
    void filterFeverish(Boolean value);
    void sortBy(int value);
    Boolean hasFilter();
    Boolean hasFilterInspected();
    Boolean hasFilterRequalify();
    Boolean hasFilterFocus();
    Boolean hasFilterFeverish();
    int getSortBy();

    interface View {
        void showLoading();
        void showHouseList(List<HouseListQuery> items, Boolean forceRefresh);
        void updateSubtitle(long inspections);
        void showFindHouseNoResult();
        void showHouse(String houseUuid);
        void showVisit(String houseUuid);
        void showSnackbar(int resId);
    }
}