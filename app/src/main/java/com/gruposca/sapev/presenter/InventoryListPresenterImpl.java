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

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.InventoryGetList;
import com.gruposca.sapev.interactor.RegistryDelete;
import com.gruposca.sapev.interactor.RegistryRestore;
import com.gruposca.sapev.interactor.VisitDelete;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;

public class InventoryListPresenterImpl implements InventoryListPresenter {

    private InventoryListPresenter.View view;
    private final InventoryGetList inventoryGetList;
    private final RegistryDelete registryDelete;
    private final RegistryRestore registryRestore;
    private String visitUuid;
    private final VisitDelete visitDelete;


    @Inject
    public InventoryListPresenterImpl(InventoryGetList inventoryGetList, RegistryDelete registryDelete, RegistryRestore registryRestore, VisitDelete visitDelete) {
        this.inventoryGetList = inventoryGetList;
        this.registryDelete = registryDelete;
        this.registryRestore = registryRestore;
        this.visitDelete = visitDelete;
    }

    @Override
    public void setView(View view, String visitUuid) {
        this.view = view;
        this.visitUuid = visitUuid;
        view.showLoading();
    }

    @Override
    public void loadInventoryList() {
        inventoryGetList.execute(visitUuid, new Callback<List<InventoryListQuery>>() {
            @Override
            public void onResponse(List<InventoryListQuery> response) {
                view.showInventories(response);
            }
            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(this.getClass().toString(), Errors.METHOD_INVENTORY_GETLIST, error.toString());
            }
        });
    }

    @Override
    public void deleteRegistry(String registryUuid) {
        registryDelete.execute(registryUuid, visitUuid, new Callback<List<InventoryListQuery>>() {
            @Override
            public void onResponse(List<InventoryListQuery> response) {
                view.showInventories(response);
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INVENTORY_DELETEREGISTRY, error.toString());
                view.showSnackbar(R.string.inventory_delete_error);
            }
        });
    }

    @Override
    public void restoreRegistry(String registryUuid) {
        registryRestore.execute(registryUuid, visitUuid, new Callback<List<InventoryListQuery>>() {
            @Override
            public void onResponse(List<InventoryListQuery> response) {
                view.showInventories(response);
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INVENTORY_RESTOREREGISTRY, error.toString());
                view.showSnackbar(R.string.inventory_restore_error);
            }
        });
    }

    @Override
    public void deleteVisit(String visitUuid) {
        visitDelete.execute(visitUuid, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.onDeleteVisit();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_INVENTORY_DELDETEVISIT, error.toString());
                view.showSnackbar(R.string.inventory_delete_visit_error);
            }
        });
    }
}