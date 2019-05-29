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

import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.VisitRepository;

import javax.inject.Inject;

public class VisitsExists extends AbsInteractor<Boolean> {

    protected VisitRepository visitRepository;

    @Inject
    public VisitsExists(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, VisitRepository visitRepository) {
        super(threadExecutor, postExecutionThread);
        this.visitRepository = visitRepository;
    }

    public void execute(Callback<Boolean> callback) {
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            notifyResponse(visitRepository.existsVisits());

        } catch (Exception e) {
            notifyError(e);
        }
    }
}