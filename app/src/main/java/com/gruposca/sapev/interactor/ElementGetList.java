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

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.ElementRepository;

import java.util.List;

import javax.inject.Inject;

public class ElementGetList extends AbsInteractor<List<Element>> {

    private final ElementRepository elementRepository;
    private int table;

    @Inject
    public ElementGetList(ThreadExecutor threadExecutor, PostThreadExecutor postThreadExecutor, ElementRepository elementRepository) {
        super(threadExecutor, postThreadExecutor);
        this.elementRepository = elementRepository;
    }

    public void execute(int table, Callback<List<Element>> callback) {
        this.table = table;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            notifyResponse(elementRepository.getElements(table));
        } catch (Exception e) {
            notifyError(e);
        }
    }
}