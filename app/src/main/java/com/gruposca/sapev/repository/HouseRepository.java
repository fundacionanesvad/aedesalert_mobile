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

import com.gruposca.sapev.datastore.database.managers.HouseManager;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.gruposca.sapev.datastore.database.query.StreetNameQuery;
import com.gruposca.sapev.datastore.net.model.HouseResult;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HouseRepository {

    private final PersonRepository personRepository;
    private final HouseManager houseManager;

    @Inject
    public HouseRepository(PersonRepository personRepository, HouseManager houseManager) {
        this.personRepository = personRepository;
        this.houseManager = houseManager;
    }

    public House getHouse(String houseUuid) {
        return houseManager.getHouse(houseUuid);
    }

    public House getHouseByQR(String qrCode) {
        return houseManager.getHouseByQR(qrCode);
    }

    public List<HouseListQuery> getHouseList(int areaId) {
        return houseManager.getHouseList(areaId);
    }

    public void saveHouses(Integer areaId, List<HouseResult> houses) {
        if (houses != null) {
            for (HouseResult item : houses) {
                House house = new House();
                house.uuid = item.uuid;
                house.number = item.number;
                house.code = item.code;
                house.qrCode = item.qrcode;
                house.latitude = item.latitude;
                house.longitude = item.longitude;
                house.streetName = item.streetName;
                house.streetNumber = item.streetNumber;
                house.lastVisitDate = item.lastVisitDate;
                house.lastVisitResult.id = item.lastVisitResult;
                house.personsNumber = item.personsNumber;
                house.area.id = areaId;
                house.focus = item.lastVisitFocus;
                house.feverish = item.lastVisitFeverish;
                house.isNewHouse = false;
                house.iniVisitDate = item.lastVisitDate;
                house.iniVisitResult.id = item.lastVisitResult;
                house.iniVisitScheduleId = item.lastVisitScheduleId;
                saveHouse(house);
                personRepository.savePersons(house.uuid, item.persons);
            }
        }
    }

    public void saveHouse(House house) {
        houseManager.saveHouse(house);
    }


    public void deleteHouse(House house) {
        houseManager.deleteHouse(house);
    }

    public List<String> getStreetNames() {
        List<String> streetNames = new ArrayList<>();
        List<StreetNameQuery> list = houseManager.getStreetNames();
        for(int i = 0; i < list.size(); i++){
             StreetNameQuery streetNameQuery = list.get(i);
            if(streetNameQuery.streetName != null) {
                streetNames.add(streetNameQuery.streetName.trim());
            }
        }
        return streetNames;
    }

    public Boolean hasVisitInProgress() {
        return houseManager.hasVisitInProgress();
    }
}