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

import com.gruposca.sapev.datastore.database.query.InventorySummaryQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.InventorySummaryGetList;
import com.gruposca.sapev.tool.Errors;

import java.util.List;

import javax.inject.Inject;

public class InventorySummaryPresenterImpl implements InventorySummaryPresenter {

    private View view;
    private final InventorySummaryGetList inventorySummaryGetList;

    @Inject
    public InventorySummaryPresenterImpl(InventorySummaryGetList inventorySummaryGetList) {
        this.inventorySummaryGetList = inventorySummaryGetList;
    }

    @Override
    public void setView(View view) {
        this.view = view;
        view.showLoading();
    }

    @Override
    public void loadInventoryList() {
        inventorySummaryGetList.execute(new Callback<List<InventorySummaryQuery>>() {
            @Override
            public void onResponse(List<InventorySummaryQuery> response) {
                view.showInventories(response);
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(this.getClass().toString(), Errors.METHOD_INVENTORY_SUMMARY, error.toString());
            }
        });
    }
}