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

import com.gruposca.sapev.datastore.database.model.Area;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.AreaRepository;
import com.gruposca.sapev.repository.HouseRepository;

import javax.inject.Inject;

public class HouseCreate extends AbsInteractor<House>{

    private final HouseRepository houseRepository;
    private final AreaRepository areaRepository;
    private Integer areaId;

    @Inject
    public HouseCreate(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, HouseRepository houseRepository, AreaRepository areaRepository) {
        super(threadExecutor, postExecutionThread);
        this.houseRepository = houseRepository;
        this.areaRepository = areaRepository;
    }

    public void execute(Integer areaId, Callback<House> callback) {
        this.areaId = areaId;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            Area area = areaRepository.getArea(areaId);
            House house = new House();
            house.area = area;
            house.code = String.format("%s%04d", area.ubigeoCode, house.number);
            house.temporary = true;
            house.isNewHouse = true;
            houseRepository.saveHouse(house);
            notifyResponse(house);
        } catch (Exception e) {
            notifyError(e);
        }
    }
}