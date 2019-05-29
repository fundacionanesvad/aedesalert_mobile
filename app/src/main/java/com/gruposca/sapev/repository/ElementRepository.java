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
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.net.model.ElementResult;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ElementRepository {

    private final ElementManager elementManager;

    @Inject
    public ElementRepository(ElementManager elementManager) {
        this.elementManager = elementManager;
    }

    public List<Element> getElements(int table) {
        return elementManager.getElements(table);
    }

    public void saveElement(Element element) {
        elementManager.saveElement(element);
    }

    public void deleteElements() {
        elementManager.deleteElements();
    }

    public void saveElements(List<ElementResult> elements) {
        if (elements != null) {
            for (ElementResult item : elements) {
                Element element = new Element();
                element.id = item.id;
                element.name = item.name;
                element.sort = item.sort;
                element.tableId = item.tableId;
                saveElement(element);
            }
        }
    }
}