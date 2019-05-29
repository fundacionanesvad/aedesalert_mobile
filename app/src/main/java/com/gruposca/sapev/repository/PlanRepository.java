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
import com.gruposca.sapev.datastore.database.managers.HouseManager;
import com.gruposca.sapev.datastore.database.managers.InventoryManager;
import com.gruposca.sapev.datastore.database.managers.PersonManager;
import com.gruposca.sapev.datastore.database.managers.PlanManager;
import com.gruposca.sapev.datastore.database.managers.RegistryManager;
import com.gruposca.sapev.datastore.database.managers.SymptomManager;
import com.gruposca.sapev.datastore.database.managers.VisitManager;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.gruposca.sapev.datastore.net.SyncApi;
import com.gruposca.sapev.datastore.net.model.PlanDetailResult;
import com.gruposca.sapev.datastore.net.model.PlanResult;
import com.gruposca.sapev.datastore.net.model.SyncHouse;
import com.gruposca.sapev.datastore.net.model.SyncInventory;
import com.gruposca.sapev.datastore.net.model.SyncPerson;
import com.gruposca.sapev.datastore.net.model.SyncPlan;
import com.gruposca.sapev.datastore.net.model.SyncSample;
import com.gruposca.sapev.datastore.net.model.SyncSymptom;
import com.gruposca.sapev.datastore.net.model.SyncVisit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.client.Response;

@Singleton
public class PlanRepository {

    private final AreaRepository areaRepository;
    private final ElementRepository elementRepository;
    private final SyncApi syncApi;
    private final PlanManager planManager;
    private final AreaManager areaManager;
    private final HouseManager houseManager;
    private final VisitManager visitManager;
    private final InventoryManager inventoryManager;
    private final PersonManager personManager;
    private final SymptomManager symptomManager;
    private final RegistryManager registryManager;

    @Inject
    public PlanRepository(AreaRepository areaRepository, ElementRepository elementRepository, SyncApi syncApi, PlanManager planManager, RegistryManager registryManager,
                          AreaManager areaManager, HouseManager houseManager, VisitManager visitManager, InventoryManager inventoryManager, PersonManager personManager, SymptomManager symptomManager) {
        this.areaRepository = areaRepository;
        this.elementRepository = elementRepository;
        this.syncApi = syncApi;
        this.planManager = planManager;
        this.areaManager = areaManager;
        this.houseManager = houseManager;
        this.visitManager = visitManager;
        this.inventoryManager = inventoryManager;
        this.personManager = personManager;
        this.symptomManager = symptomManager;
        this.registryManager = registryManager;
    }

    public Boolean hasPlan() {
        return planManager.hasPlan();
    }

    public Plan getPlan() {
        return planManager.getPlan();
    }

    public List<PlanResult> getPlans() {
        return syncApi.getPlans();
    }

    public Boolean syncPlan(Integer id) {
        PlanDetailResult result = syncApi.getPlan(id);
        Plan plan = new Plan();
        plan.id = id;
        plan.planSize = result.planSize;
        plan.progress = result.progress;
        plan.type.id = result.typeId;
        plan.reconversionScheduleId = result.reconversionScheduleId;
        plan.larvicideName = result.larvicide.name.toLowerCase();
        plan.larvicideUnity = result.larvicide.unity.toLowerCase();
        plan.larvicideDose = result.larvicide.dose;
        plan.larvicideDoseName = result.larvicide.doseName.toLowerCase();
        plan.larvicideWaterVolume = result.larvicide.waterVolume;
        elementRepository.saveElements(result.elements);
        planManager.savePlan(plan);
        areaRepository.saveAreas(id, result.areas);
        return true;
    }

    public Boolean submitPlan() throws IOException {
        //Recupera el plan
        Plan plan = getPlan();
        SyncPlan syncPlan = new SyncPlan(plan.id);

        //Recupera las areas
        List<AreaListQuery> listAreas = areaManager.getAreas();
        for (AreaListQuery area : listAreas) {
            //Recupera las viviendas
            List<HouseListQuery> listHouses = houseManager.getHouseList(area.id);
            for (HouseListQuery house : listHouses) {
                if (house.visited) {
                    //Recupera la visita
                    Visit visit = visitManager.getVisitByHouse(house.uuid);
                    if (visit != null) {
                        //Crea la visita
                        SyncVisit syncVisit = new SyncVisit(visit.uuid,
                                visit.date.getTimeInMillis(),
                                visit.feverish.byteValue(),
                                visit.larvicide,
                                visit.comments,
                                visit.result.id,
                                plan.id,
                                house.uuid,
                                getListSyncInventory(visit.uuid));

                        SyncHouse syncHouse = new SyncHouse(house.uuid,
                                house.code,
                                house.qrCode,
                                house.latitude,
                                house.longitude,
                                house.streetName,
                                house.streetNumber,
                                area.id,
                                house.personsNumber,
                                syncVisit,
                                getListSyncsPerson(house.uuid, visit.uuid));

                        //Crea la viviendas
                        syncPlan.addHouse(syncHouse);
                    }
                }
            }
        }
        Response response = syncApi.syncPlan(syncPlan);
        return response.getStatus() == 201;
    }

    private List<SyncInventory> getListSyncInventory(String visitUuid) {
        List<SyncInventory> listSyncInventory = new ArrayList<>();
        List<InventoryListQuery> listInventory = inventoryManager.getInventories(visitUuid);
        for (InventoryListQuery inventory : listInventory) {
            SyncInventory syncInventory = new SyncInventory(inventory.uuid,
                    inventory.inspected.intValue(),
                    inventory.focus.intValue(),
                    inventory.treated.intValue(),
                    inventory.packet.intValue(),
                    inventory.destroyed.intValue(),
                    inventory.containerId,
                    visitUuid,
                    getListSyncSample(inventory.containerId, visitUuid));
            listSyncInventory.add(syncInventory);
        }
        return listSyncInventory;
    }


    private List<SyncPerson> getListSyncsPerson(String houseUuid, String visitUuid) {
        List<SyncPerson> listSyncPersons = new ArrayList<>();
        List<Person> listPersons = personManager.getAllPersons(houseUuid);
        for (int i = 0; i < listPersons.size(); i++) {
            Person person = listPersons.get(i);
            int offset = person.birthday.getTimeZone().getRawOffset();
            SyncPerson syncPerson = new SyncPerson(person.uuid,
                    person.name,
                    person.genre,
                    person.birthday.getTimeInMillis() + offset,
                    person.birthdayExact,
                    person.enabled,
                    person.cardId,
                    getListSyncsSymptoms(person.uuid, visitUuid));
            listSyncPersons.add(syncPerson);
        }
        return listSyncPersons;
    }

    private List<SyncSymptom> getListSyncsSymptoms(String personUuid, String visitUuid) {
        List<SyncSymptom> list = new ArrayList<>();
        List<Symptom> symptoms = symptomManager.getSymptoms(visitUuid, personUuid);
        for (Symptom symptom : symptoms) {
            if (symptom.selected) {
                SyncSymptom item = new SyncSymptom();
                item.uuid = symptom.uuid;
                item.symptomId = symptom.symptom.id;
                item.personUuid = symptom.person.uuid;
                item.visitUuid = symptom.visit.uuid;
                list.add(item);
            }
        }
        return list;
    }

    private List<SyncSample> getListSyncSample(Integer containerId, String visitUuid) {
        List<SyncSample> listSyncSample = new ArrayList<>();
        List<Registry> registries = registryManager.getSamplesByContainerId(containerId, visitUuid);
        for (Registry registry : registries) {
            SyncSample syncSample = new SyncSample(registry.uuid,
                    registry.sample,
                    registry.states);
            listSyncSample.add(syncSample);
        }
        return listSyncSample;
    }

    public void deletePlan() {
        planManager.deletePlan();
        elementRepository.deleteElements();
    }

    public void savePlan(Plan plan) {
        planManager.savePlan(plan);
    }
}