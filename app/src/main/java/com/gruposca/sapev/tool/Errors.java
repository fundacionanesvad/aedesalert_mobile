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

package com.gruposca.sapev.tool;

public class Errors {

    public static String METHOD_LOGIN      = "login()";
    public static String METHOD_GET_PLAN   = "getAvailablePlans()";
    public static String METHOD_DELETEPLAN = "deletePlan()";
    public static String METHOD_SEND_LOGS  = "sendLogs()";

    public static String METHOD_SAVE_SYMPTOMS        = "saveSymptoms";
    public static String METHOD_FEBRILES_GETLIST     = "feverishGetList";
    public static String METHOD_FEBRILES_DELETEVISIT = "visitDelete";

    public static String METHOD_HOUSE_CREATE          = "houseCreate";
    public static String METHOD_HOUSE_GET             = "houseGet";
    public static String METHOD_HOUSE_GETVISIT        = "visitGetByHouse";
    public static String METHOD_HOUSE_STREETNAMES     = "streetNameGetList";
    public static String METHOD_HOUSE_SAVE            = "houseSave";
    public static String METHOD_HOUSE_VISITINPROGRESS = "houseHasVisitInProgress";
    public static String METHOD_HOUSE_DELETEVISIT     = "visitDelete";
    public static String METHOD_HOUSE_DELETEHOUSE     = "houseDelete";
    public static String METHOD_HOUSE_GETLIST         = "getHouseList";
    public static String METHOD_HOUSE_GETBYQR         = "houseGetByQR";
    public static String METHOD_HOUSE_SUMMARY         = "housesSummaryGet";

    public static String METHOD_INSPECTION_GETBYHOUSE = "visitGetByHouse";
    public static String METHOD_INSPECTION_SAVE       = "visitSave";
    public static String METHOD_INSPECTION_SUMMARY    = "visitGet";
    public static String METHOD_INSPECTION_FINISH     = "visitFinish";
    public static String METHOD_INSPECTION_UPDATEQR   = "houseUpdateQR";

    public static String METHOD_INVENTORY_GETLIST         = "inventoryGetList";
    public static String METHOD_INVENTORY_DELETEREGISTRY  = "registryDelete";
    public static String METHOD_INVENTORY_RESTOREREGISTRY = "registryRestore";
    public static String METHOD_INVENTORY_DELDETEVISIT    = "visitDelete";
    public static String METHOD_INVENTORY_SUMMARY         = "inventorySummaryGetList";

    public static String METHOD_MAIN_PLAN = "planExists";

    public static String METHOD_PERSONLIST_DIALOG       = "personsFeverless";
    public static String METHOD_PERSONLIST_GETLIST      = "personGetList";
    public static String METHOD_PERSONLIST_GETHOUSE     = "houseGet";
    public static String METHOD_PERSONLIST_UPDATENUMBER = "houseUpdatePersonNumber";
    public static String METHOD_PERSONLIST_CHECKFEBRILE = "checkPersonFeverish";
    public static String METHOD_PERSONLIST_DELETEVISIT  = "visitDelete";
    public static String METHOD_PERSONLIST_UPDATESTATE  = "personUpdateState";

    public static String METHOD_PERSON_GET  = "personGet";
    public static String METHOD_PERSON_SAVE = "personSave";


    public static String METHOD_PLAN_AREA_GETLIST  = "areaGetList";
    public static String METHOD_PLAN_DELETE        = "planDelete";
    public static String METHOD_PLAN_VISITS        = "visitsExists";
    public static String METHOD_PLAN_SYNC          = "syncPlan";
    public static String METHOD_PLAN_HASCONNECTION = "hasConnection";

    public static String METHOD_REGISTRY_GETLIST = "registryGetList";

    public static String METHOD_SAMPLESSUMMARY_GETLIST = "samplesSummaryGetList";

    public static String METHOD_SYMPTOM_GETLIST = "symptomGetList";

    public static String METHOD_VISIT_CREATE     = "visitCreate";
    public static String METHOD_VISIT_GETBYHOUSE = "visitGetByHouse";
    public static String METHOD_VISIT_DELETE     = "visitDeleteByHouse";

    public static String METHOD_REGISTRY_ELEMENT_GETLIST = "elementGetList";
    public static String METHOD_REGISTRY_SAVE            = "registrySave";
    public static String METHOD_REGISTRY_HASSAMPLE       = "registryHasSample";
    public static String METHOD_REGISTRY_PLANGET         = "planGet";

}
