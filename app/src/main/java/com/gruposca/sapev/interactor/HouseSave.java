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
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.HouseRepository;

import javax.inject.Inject;

/**
 * Created by cf1 on 05/10/2015.
 */
public class HouseSave extends AbsInteractor<Void> {

    private HouseRepository houseRepository;
    private House house;

    @Inject
    public HouseSave(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, HouseRepository houseRepository) {
        super(threadExecutor, postExecutionThread);
        this.houseRepository = houseRepository;
    }

    public void execute(House house, Callback<Void> callback) {
        this.house = house;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            House old = houseRepository.getHouse(house.uuid);
            if (old != null) {
                house.latitude = old.latitude;
                house.longitude = old.longitude;
            }
            houseRepository.saveHouse(house);
            notifyResponse(null);
        } catch (Exception e) {
            notifyError(e);
        }
    }
}
