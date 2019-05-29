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

import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Symptom;
import java.util.List;

public interface FeverishListPresenter {

    void setView(View view);
    void initialize(View view,String visitUuid);
    void load();
    void setPerson(String personUuid);
    void showFeverish();
    void saveSymptoms(String visitUuid, List<Symptom> listSymptoms);
    void deleteVisit(String visitUuid);

    interface View {
        void showLoading();
        void showErrorLoad(String module, String method, String errorLog);
        void showFeverish(List<Person> persons);
        void showFeverishPersons();
        void showSymptoms(String personUuid);
        void showRecommendations();
        void showFeverishList();
        void onDeleteVisit();
        void showSnackbar(int resId);
    }
}
