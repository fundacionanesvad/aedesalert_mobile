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

import java.util.Calendar;
import java.util.List;

public class HouseResult {

    public String uuid;
    public Integer number;
    public String code;
    public String qrcode;
    public Double latitude;
    public Double longitude;
    public String streetName;
    public String streetNumber;
    public Calendar lastVisitDate;
    public Integer lastVisitResult;
    public Integer lastVisitScheduleId;
    public Boolean lastVisitFocus;
    public Boolean lastVisitFeverish;
    public Integer personsNumber;
    public List<PersonResult> persons;
}