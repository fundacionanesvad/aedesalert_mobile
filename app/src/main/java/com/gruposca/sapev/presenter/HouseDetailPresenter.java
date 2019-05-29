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
import android.view.Menu;

import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Visit;

import java.util.List;

public interface HouseDetailPresenter {

    void initialize(View view, String houseUuid, Integer areaId);
    void load(Bundle state);
    void saveState(Bundle state);
    void setStreetName(String streetName);
    void setStreetNumber(String streetNumber);
    void setQrCode(String qr);
    void saveHouse(Boolean finish);
    void confirmVisit(int result);
    void createVisit(int result);
    void convertVisit(int result);
    void deleteHouse();
    Boolean hasChanges();
    Boolean isValid();
    Boolean hasQr();
    Boolean isNew();
    Boolean hasVisit();

    void editVisit();

    interface View {
        void showLoading();
        void showSnackbar(int resId);
        void populateStreetAutocomplete(List<String> streets);
        void showHouse(House house);
        void showVisit(House house);
        void showVisit(Visit visitModel);
        void showVisitButton(Boolean visible, Boolean visited);
        void invalidateOptionMenu();
        void onHouseSaved();
        void onCreateVisit(String houseUuid, int result);
        void confirmVisit(int result);
        void confirmChanges();
        void validate();
        void onDeleteHouse();
        void showButtons(Boolean reconversion,String planTypeName);
    }
}