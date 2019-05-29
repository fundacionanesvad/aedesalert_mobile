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

public class SyncVisit {

    public String uuid;
    public Long date;
    public Byte feverish;
    public Double larvicide;
    public String comments;
    public Integer resultId;
    public Integer planId;
    public String houseUuid;
    public List<SyncInventory> listSyncInventories;

    public SyncVisit (){}

    public SyncVisit (String uuid, Long date, Byte feverish, Double larvicide, String comments, Integer resultId, Integer planId, String houseUuid, List<SyncInventory> listSyncInventories){
        this.uuid = uuid;
        this.date = date;
        this.feverish = feverish;
        this.larvicide = larvicide;
        this.comments = comments;
        this.resultId = resultId;
        this.planId = planId;
        this.houseUuid = houseUuid;
        this.listSyncInventories = listSyncInventories;
    }
}