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

package com.gruposca.sapev.datastore.database.managers;

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.Element$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import javax.inject.Inject;

public class ElementManager {

    @Inject
    public ElementManager() { }

    public Element getElement(Integer elementId) {
        return new Select()
                .from(Element.class)
                .where(Condition.column(Element$Table.ID).eq(elementId))
                .querySingle();
    }

    public List<Element> getElements(int table) {
        return new Select()
                .from(Element.class)
                .where(Condition.column(Element$Table.TABLEID).eq(table))
                .queryList();
    }

    public void saveElement(Element element) {
        element.save();
    }

    public void deleteElements() {
        new Delete().from(Element.class).query();
    }
}