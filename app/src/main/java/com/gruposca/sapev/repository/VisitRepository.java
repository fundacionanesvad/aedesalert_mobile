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
import com.gruposca.sapev.datastore.database.managers.VisitManager;
import com.gruposca.sapev.datastore.database.model.Visit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VisitRepository {

    private final VisitManager visitManager;
    private final ElementManager elementManager;

    @Inject
    public VisitRepository(VisitManager visitManager, ElementManager elementManager) {
        this.visitManager = visitManager;
        this.elementManager = elementManager;
    }

    public Visit createVisit(Integer resultId, String houseUuid) {
        Visit visit = new Visit();
        visit.result = elementManager.getElement(resultId);
        visit.house.uuid = houseUuid;
        return visit;
    }

    public Visit getVisit(String visitUuid) {
        return visitManager.getVisit(visitUuid);
    }

    public Visit getVisitByHouse(String houseUuid) {
        return visitManager.getVisitByHouse(houseUuid);
    }

    public void saveVisit(Visit visit) {
        visitManager.saveVisit(visit);
    }

    public void deleteVisit(String visitUuid) {visitManager.deleteVisit(visitUuid);}

    public void deleteVisitByHouse(String houseUuid) {
        visitManager.deleteVisitByHouse(houseUuid);
    }

    public Boolean existsVisits() {
        return visitManager.existsVisits();
    }

}