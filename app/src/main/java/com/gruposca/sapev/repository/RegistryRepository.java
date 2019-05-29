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

package com.gruposca.sapev.repository;

import com.gruposca.sapev.datastore.database.managers.PlanManager;
import com.gruposca.sapev.datastore.database.managers.RegistryManager;
import com.gruposca.sapev.datastore.database.model.Inventory;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.query.RegistryListQuery;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegistryRepository {

    private final InventoryRepository inventoryRepository;
    private final VisitRepository visitRepository;
    private final RegistryManager registryManager;
    private final PlanManager planManager;

    @Inject
    public RegistryRepository(InventoryRepository inventoryRepository, VisitRepository visitRepository, RegistryManager registryManager, PlanManager planManager) {
        this.inventoryRepository = inventoryRepository;
        this.visitRepository = visitRepository;
        this.registryManager = registryManager;
        this.planManager = planManager;
    }

    public List<RegistryListQuery> getInventories(String visitUuid) {
        return registryManager.getRegistries(visitUuid);
    }

    public void saveRegistry(Registry registry) {
        registryManager.saveRegistry(registry);
        updateInventory(registry);
    }

    public void deleteRegistry(String uuid) {
        Registry registry = registryManager.getRegistry(uuid);
        registry.deleted = true;
        registryManager.saveRegistry(registry);
        updateInventory(registry);
    }

    public void restoreRegistry(String uuid) {
        Registry registry = registryManager.getRegistry(uuid);
        registry.deleted = false;
        registryManager.saveRegistry(registry);
        updateInventory(registry);
    }

    private void updateInventory(Registry registry) {
        int change = registry.deleted ? -registry.units : registry.units;
        Inventory inventory = inventoryRepository.getInventory(registry.container.id, registry.visit.uuid);
        inventory.inspected += change;
        if (registry.focus) {
            inventory.focus += change;
        }
        if (registry.treated) {
            inventory.treated += change;
            inventory.packet += (change * registry.packets);
        }
        if (registry.destroyed) {
            inventory.destroyed += change;
        }
        inventoryRepository.saveInventory(inventory);
        updateVisit(registry);
    }

    private void updateVisit(Registry registry) {
        int change = registry.deleted ? -registry.units : registry.units;
        Visit visit = visitRepository.getVisit(registry.visit.uuid);
        visit.inspected += change;
        if (registry.focus) {
            visit.focus += change;
            visit.samples += change;
        }
        if (registry.treated) {
            visit.treated += change;
            Plan plan = planManager.getPlan();
            visit.larvicide += (change * registry.packets * plan.larvicideDose);
        }
        if (registry.destroyed) {
            visit.destroyed += change;
        }
        visitRepository.saveVisit(visit);
    }

    public Boolean hasSample(String sampleCode) {
        return registryManager.hasSample(sampleCode);
    }
}