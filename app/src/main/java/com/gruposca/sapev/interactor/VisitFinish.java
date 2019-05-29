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

import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.HouseRepository;
import com.gruposca.sapev.repository.VisitRepository;

import javax.inject.Inject;

public class VisitFinish extends AbsInteractor<Void> {

    protected VisitRepository visitRepository;
    protected HouseRepository houseRepository;
    protected Visit visit;

    @Inject
    public VisitFinish(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, VisitRepository visitRepository, HouseRepository houseRepository) {
        super(threadExecutor, postExecutionThread);
        this.visitRepository = visitRepository;
        this.houseRepository = houseRepository;
    }

    public void execute(Visit visit, Callback<Void> callback) {
        this.visit = visit;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            visitRepository.saveVisit(visit);
            House house = houseRepository.getHouse(visit.house.uuid);
            house.visited = true;
            house.visitInProgress = false;
            house.lastVisitDate = visit.date;
            house.lastVisitResult = visit.result;
            house.feverish = visit.feverish != 0;
            house.focus = visit.focus != 0;
            houseRepository.saveHouse(house);
            notifyResponse(null);
        } catch (Exception e) {
            notifyError(e);
        }
    }
}