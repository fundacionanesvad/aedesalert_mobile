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
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.GetHouseList;
import com.gruposca.sapev.interactor.HouseGetByQR;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HouseListPresenterImpl implements HouseListPresenter {

    private static final String PARCEL_AREAS = "PARCEL_AREAS";
    private static final String PARCEL_FILTER_QUERY = "PARCEL_FILTER_QUERY";
    private static final String PARCEL_FILTER_REQUALIFY = "PARCEL_FILTER_REQUALIFY";
    private static final String PARCEL_FILTER_FOCUS = "PARCEL_FILTER_FOCUS";
    private static final String PARCEL_FILTER_FEVERISH = "PARCEL_FILTER_FEVERISH";
    private static final String PARCEL_SORT_BY = "PARCEL_SORT_BY";

    private final GetHouseList getHouseList;
    private final HouseGetByQR houseGetByQR;

    private HouseListPresenter.View view;
    private int areaId;
    private long otherInspections;
    private List<HouseListQuery> areas;
    private String filterQuery;
    private Boolean filterInspected;
    private Boolean filterRequalify;
    private Boolean filterFocus;
    private Boolean filterFeverish;
    private int sortBy;

    @Inject
    public HouseListPresenterImpl(GetHouseList getHouseList, HouseGetByQR houseGetByQR) {
        this.getHouseList = getHouseList;
        this.houseGetByQR = houseGetByQR;
    }

    @Override
    public void initialize(View view, int areaId, long otherInspections) {
        this.view = view;
        this.areaId = areaId;
        this.otherInspections = otherInspections;
        this.filterQuery = "";
        this.filterInspected = false;
        this.filterRequalify = false;
        this.filterFocus = false;
        this.filterFeverish = false;
        this.sortBy = SORT_ADDRESS;
        view.showLoading();
    }

    @Override
    public void load(Bundle state) {
        if (state == null) {
            getHouseList.execute(areaId, new Callback<List<HouseListQuery>>() {
                @Override
                public void onResponse(List<HouseListQuery> response) {
                    areas = response;
                    filterAndSort(true);
                }
                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_GETLIST, error.toString());
                    view.showSnackbar(R.string.house_list_error);
                }
            });
        } else {
            areas = state.getParcelableArrayList(PARCEL_AREAS);
            filterQuery = state.getString(PARCEL_FILTER_QUERY);
            filterRequalify = state.getBoolean(PARCEL_FILTER_REQUALIFY);
            filterFocus = state.getBoolean(PARCEL_FILTER_FOCUS);
            filterFocus = state.getBoolean(PARCEL_FILTER_FEVERISH);
            sortBy = state.getInt(PARCEL_SORT_BY);
            filterAndSort(false);
        }
    }

    @Override
    public void saveState(Bundle state) {
        state.putParcelableArrayList(PARCEL_AREAS, new ArrayList<>(areas));
        state.putString(PARCEL_FILTER_QUERY, filterQuery);
        state.putBoolean(PARCEL_FILTER_REQUALIFY, filterRequalify);
        state.putBoolean(PARCEL_FILTER_FOCUS, filterFocus);
        state.putBoolean(PARCEL_FILTER_FEVERISH, filterFeverish);
        state.putInt(PARCEL_SORT_BY, sortBy);
    }

    @Override
    public void findHouseByQR(String qrcode) {
        houseGetByQR.execute(qrcode, new Callback<House>() {
            @Override
            public void onResponse(House response) {
                if (response == null || response.area.id != areaId) {
                    view.showFindHouseNoResult();
                } else {
                    if (response.visitInProgress) {
                        view.showVisit(response.uuid);
                    } else {
                        view.showHouse(response.uuid);
                    }
                }
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_GETBYQR, error.toString());
                view.showSnackbar(R.string.house_qr_error);
            }
        });
    }

    @Override
    public void filterQuery(String value) {
        filterQuery = value;
        filterAndSort(false);
    }

    @Override
    public void filterInspected(Boolean value) {
        filterInspected = value;
        filterAndSort(false);
    }

    @Override
    public void filterRequalify(Boolean value) {
        filterRequalify = value;
        filterAndSort(false);
    }

    @Override
    public void filterFocus(Boolean value) {
        filterFocus = value;
        filterAndSort(false);
    }

    @Override
    public void filterFeverish(Boolean value) {
        filterFeverish = value;
        filterAndSort(false);
    }

    @Override
    public void sortBy(int value) {
        sortBy = value;
        filterAndSort(false);
    }

    @Override
    public Boolean hasFilter() {
        return filterInspected || filterRequalify || filterFocus || filterFeverish;
    }

    @Override
    public Boolean hasFilterInspected() {
        return filterInspected;
    }

    @Override
    public Boolean hasFilterRequalify() {
        return filterRequalify;
    }

    @Override
    public Boolean hasFilterFocus() {
        return filterFocus;
    }

    @Override
    public Boolean hasFilterFeverish() {
        return filterFeverish;
    }

    @Override
    public int getSortBy() {
        return sortBy;
    }

    private void filterAndSort(Boolean forceRefresh) {
        //Filter
        final List<HouseListQuery> filteredAreas = new ArrayList<>();
        for (HouseListQuery item : areas) {
            if (item.toString().toLowerCase().contains(filterQuery)) {
                if (!filterInspected && !filterFocus && !filterRequalify && !filterFeverish) {
                    filteredAreas.add(item);
                } else if (filterInspected && item.visited && item.lastVisitResultId == Visit.RESULT_INSPECTION) {
                    filteredAreas.add(item);
                } else if (filterFocus && item.focus) {
                    filteredAreas.add(item);
                } else if (filterRequalify && item.lastVisitResultId != null && (item.lastVisitResultId == Visit.RESULT_CLOSED || item.lastVisitResultId == Visit.RESULT_RELUCTANT)) {
                    filteredAreas.add(item);
                } else if (filterFeverish && item.feverish) {
                    filteredAreas.add(item);
                }
            }
        }
        //Sort
        switch (sortBy) {
            case SORT_ADDRESS:
                Collections.sort(filteredAreas, new Comparator<HouseListQuery>() {
                    public int compare(HouseListQuery item1, HouseListQuery item2) {
                        int compare = item1.streetName.compareToIgnoreCase(item2.streetName);
                        if (compare == 0)
                            compare = item1.getStreetNumberFormatted().compareToIgnoreCase(item2.getStreetNumberFormatted());
                        return compare;
                    }
                });
                break;
            case SORT_DATE:
                Collections.sort(filteredAreas, new Comparator<HouseListQuery>() {
                    public int compare(HouseListQuery item1, HouseListQuery item2) {
                        if (item1.lastVisitDate == null) {
                            if (item2.lastVisitDate == null)
                                return 0;
                            else
                                return 1;
                        } else if (item2.lastVisitDate == null) {
                            return -1;
                        }
                        return item1.lastVisitDate.compareTo(item2.lastVisitDate);
                    }
                });
                break;
        }
        view.showHouseList(filteredAreas, forceRefresh);

        //Count inspections
        long inspections = otherInspections;
        for(HouseListQuery area : areas) {
            if ((area.visited || area.visitInProgress) && area.lastVisitResultId == Visit.RESULT_INSPECTION)
                inspections++;
        }
        view.updateSubtitle(inspections);
    }
}