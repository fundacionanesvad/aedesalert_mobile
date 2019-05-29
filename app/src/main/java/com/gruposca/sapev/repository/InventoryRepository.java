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

import com.gruposca.sapev.datastore.database.managers.ElementManager;
import com.gruposca.sapev.datastore.database.managers.InventoryManager;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.Inventory;
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InventoryRepository {

    private final InventoryManager inventoryManager;
    private final ElementManager elementManager;

    @Inject
    public InventoryRepository(InventoryManager inventoryManager, ElementManager elementManager) {
        this.inventoryManager = inventoryManager;
        this.elementManager = elementManager;
    }

    public Inventory getInventory(Integer containerId, String visitUuid) {
        return inventoryManager.getInventory(containerId, visitUuid);
    }

    public List<InventoryListQuery> getInventories(String visitUuid) {
        List<InventoryListQuery> list = inventoryManager.getInventories(visitUuid);
        if (list.size() == 0) {
            List<Element> elements = elementManager.getElements(Element.TABLE_CONTAINERS);
            for(Element element : elements) {
                Inventory inventory = new Inventory();
                inventory.container = element;
                inventory.visit.uuid = visitUuid;
                inventoryManager.saveInventory(inventory);
            }
            list = inventoryManager.getInventories(visitUuid);
        }
        return list;
    }

    public void saveInventory(Inventory inventory) {
        inventoryManager.saveInventory(inventory);
    }
}