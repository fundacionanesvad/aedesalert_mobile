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

import com.gruposca.sapev.datastore.database.query.InventorySummaryQuery;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.VisitsSummaryRepository;
import java.util.List;

import javax.inject.Inject;

public class InventorySummaryGetList extends AbsInteractor<List<InventorySummaryQuery>> {

    private final VisitsSummaryRepository visitsSummaryRepository;

    @Inject
    public InventorySummaryGetList(ThreadExecutor threadExecutor, PostThreadExecutor postThreadExecutor, VisitsSummaryRepository visitsSummaryRepository) {
        super(threadExecutor, postThreadExecutor);
        this.visitsSummaryRepository = visitsSummaryRepository;
    }

    public void execute(Callback<List<InventorySummaryQuery>> callback) {
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            notifyResponse(visitsSummaryRepository.getInventoryList());
        } catch (Exception e) {
            notifyError(e);
        }
    }
}