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

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.IntegerList;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;

import java.util.List;

public interface RegistryPresenter {

    void initialize(View view, String visitUuid, Integer containerId);
    void load();
    void setComment(String comment);
    void setFocus(Boolean focus);
    void setStates(IntegerList states);
    void setTreated(Boolean treated);
    void setPackets(Integer packets);
    void setDestroyed(Boolean destroyed);
    void setUnits(Integer units);
    void confirmSave();
    void save();
    Integer calculatePackets(Integer depth, Integer width, Integer height);
    Integer calculatePackets(Integer diameter, Integer height);
    Integer calculatePackets(Integer liters);


    interface View {
        void onPlanLoaded(Plan plan);
        void onStatesLoaded(List<Element> states);
        void updateView(Registry registry);
        void confirmSave(Registry registry, Plan plan);
        void showSnackbar(int resId);
        void populateCommentAutocomplete();
        void close();
        void showUnitsDialog(Integer number);
    }
}