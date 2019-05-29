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

import com.gruposca.sapev.datastore.database.managers.AreaManager;
import com.gruposca.sapev.datastore.database.model.Area;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.gruposca.sapev.datastore.net.model.AreaResult;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AreaRepository {

    private final HouseRepository houseRepository;
    private final AreaManager areaManager;

    @Inject
    public AreaRepository(HouseRepository houseRepository, AreaManager areaManager) {
        this.houseRepository = houseRepository;
        this.areaManager = areaManager;
    }

    public Area getArea(int areaId) {
        return areaManager.getArea(areaId);
    }

    public List<AreaListQuery> getAreas() {
        return areaManager.getAreas();
    }

    public Boolean unlockArea(Integer pin) {
        return areaManager.unlockArea(pin);
    }

    public void saveArea(Area area) {
        areaManager.saveArea(area);
    }

    public void saveAreas(Integer planId, List<AreaResult> areas) {
        if (areas != null) {
            for (AreaResult item : areas) {
                Area area = new Area();
                area.id = item.id;
                area.name = item.name;
                area.ubigeoCode = item.ubigeoCode;
                area.pin = item.pin;
                area.substitute = item.substitute;
                area.plan.id = planId;
                saveArea(area);
                houseRepository.saveHouses(area.id, item.houses);
            }
        }
    }
}