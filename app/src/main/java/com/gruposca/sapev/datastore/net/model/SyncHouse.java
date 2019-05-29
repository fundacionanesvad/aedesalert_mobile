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

package com.gruposca.sapev.datastore.net.model;

import java.util.List;

public class SyncHouse {

    private String    uuid;
    private String    code;
    private String    qrcode;
    private Double    latitude;
    private Double    longitude;
    private String    streetName;
    private String    streetNumber;
    private Integer   areaId;
    private Integer   personsNumber;
    private SyncVisit visit;
    private List<SyncPerson> listSyncPerson;

    public SyncHouse (String uuid, String code, String qrcode, Double latitude, Double longitude, String streetName, String streetNumber, Integer areaId, Integer personsNumber, SyncVisit visit, List<SyncPerson> listSyncPerson){

        this.uuid           = uuid;
        this.code           = code;
        this.qrcode         = qrcode;
        this.latitude       = latitude;
        this.longitude      = longitude;
        this.streetName     = streetName;
        this.streetNumber   = streetNumber;
        this.areaId         = areaId;
        this.personsNumber  = personsNumber;
        this.visit          = visit;
        this.listSyncPerson = listSyncPerson;
    }
}
