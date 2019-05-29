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

package com.gruposca.sapev.interactor;

import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.InventoryRepository;
import com.gruposca.sapev.repository.RegistryRepository;

import java.util.List;

import javax.inject.Inject;

public class RegistryRestore extends AbsInteractor<List<InventoryListQuery>> {

    protected final RegistryRepository registryRepository;
    protected final InventoryRepository inventoryRepository;
    protected String registryUuid;
    protected String visitUuid;

    @Inject
    public RegistryRestore(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, RegistryRepository registryRepository, InventoryRepository inventoryRepository) {
        super(threadExecutor, postExecutionThread);
        this.registryRepository = registryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public void execute(String registryUuid, String visitUuid, Callback<List<InventoryListQuery>> callback) {
        this.registryUuid = registryUuid;
        this.visitUuid = visitUuid;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            registryRepository.restoreRegistry(registryUuid);
            notifyResponse(inventoryRepository.getInventories(visitUuid));
        } catch (Exception e) {
            notifyError(e);
        }
    }
}