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

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.query.SampleListQuery;

import java.util.List;

public interface SamplesSummaryPresenter {

    void initialize(View view);
    void load();

    interface View {
        void showLoading();
        void showErrorLoad(String module, String method, String errorLog);
        void showSampleList(List<SampleListQuery> items, List<Element> values);
    }
}