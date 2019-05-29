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

package com.gruposca.sapev.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.HouseCreate;
import com.gruposca.sapev.interactor.HouseDelete;
import com.gruposca.sapev.interactor.HouseGet;
import com.gruposca.sapev.interactor.HouseHasVisitInProgress;
import com.gruposca.sapev.interactor.HouseSave;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.interactor.StreetNameGetList;
import com.gruposca.sapev.interactor.VisitDelete;
import com.gruposca.sapev.interactor.VisitGetByHouse;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;

public class HouseDetailPresenterImpl implements HouseDetailPresenter {

    private static final String PARCEL_HOUSE = "PARCEL_HOUSE";
    private static final String PARCEL_VISIT = "PARCEL_VISIT";
    private static final String PARCEL_HAS_CHANGES = "PARCEL_HAS_CHANGES";

    private final HouseCreate houseCreate;
    private final HouseGet houseGet;
    private final HouseSave houseSave;
    private final HouseDelete houseDelete;
    private final StreetNameGetList streetNameGetList;
    private final VisitGetByHouse visitGetByHouse;
    private final HouseHasVisitInProgress houseHasVisitInProgress;
    private final VisitDelete visitDelete;
    private final PlanGet planGet;

    private HouseDetailPresenter.View view;
    private String houseUuid;
    private Integer areaId;
    private House house;
    private Visit visit;
    private Plan plan;
    private Boolean hasChanges;

    @Inject
    public HouseDetailPresenterImpl(HouseCreate houseCreate, HouseGet houseGet, HouseSave houseSave, VisitGetByHouse visitGetByHouse, StreetNameGetList streetNameGetList, HouseHasVisitInProgress houseHasVisitInProgress, VisitDelete visitDelete, HouseDelete houseDelete, PlanGet planGet) {
        this.houseCreate = houseCreate;
        this.houseGet = houseGet;
        this.houseSave = houseSave;
        this.visitGetByHouse = visitGetByHouse;
        this.streetNameGetList = streetNameGetList;
        this.houseHasVisitInProgress = houseHasVisitInProgress;
        this.visitDelete = visitDelete;
        this.houseDelete = houseDelete;
        this.planGet = planGet;
    }

    @Override
    public void initialize(View view, String houseUuid, Integer areaId) {
        this.view = view;
        this.houseUuid = houseUuid;
        this.areaId = areaId;
        this.hasChanges = false;
        view.showLoading();
   }

    @Override
    public void load(Bundle state) {
        planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(Plan response) {
                plan = response;
                view.showButtons(response.reconversionScheduleId != null , response.type.name);

            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_GET_PLAN, error.toString());
            }
        });
        if (state == null) {
            if (houseUuid.equals("")) {
                houseCreate.execute(areaId, new Callback<House>() {
                    @Override
                    public void onResponse(House response) {
                        house = response;
                        houseUuid = house.uuid;
                        view.showHouse(house);
                        view.showVisit(house);
                        showVisitButton();
                    }

                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_CREATE, error.toString());
                    }
                });
            } else {
                houseGet.execute(houseUuid, new Callback<House>() {
                    @Override
                    public void onResponse(House response) {
                        house = response;
                        if (response.lastVisitDate == null) {
                            view.showHouse(house);
                            view.showVisit(house);
                            showVisitButton();
                        } else {
                            visitGetByHouse.execute(houseUuid, new Callback<Visit>() {
                                @Override
                                public void onResponse(Visit response) {
                                    visit = response;
                                    view.showHouse(house);
                                    if (response == null) {
                                        view.showVisit(house);
                                    } else {
                                        view.showVisit(visit);
                                    }
                                    showVisitButton();
                                }

                                @Override
                                public void onError(ErrorBundle error) {
                                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_GETVISIT, error.toString());
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_GET, error.toString());
                    }
                });
            }

            streetNameGetList.execute(new Callback<List<String>>() {
                @Override
                public void onResponse(List<String> response) {
                    view.populateStreetAutocomplete(response);
                }

                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_STREETNAMES, error.toString());
                }
            });
        } else {
            house = state.getParcelable(PARCEL_HOUSE);
            visit = state.getParcelable(PARCEL_VISIT);
            hasChanges = state.getBoolean(PARCEL_HAS_CHANGES);
            view.showHouse(house);
            if (visit == null) {
                view.showVisit(house);
            } else {
                view.showVisit(visit);
            }
        }
    }

    @Override
    public void saveState(Bundle state) {
        state.putParcelable(PARCEL_HOUSE, house);
        state.putParcelable(PARCEL_VISIT, visit);
        state.putBoolean(PARCEL_HAS_CHANGES, hasChanges);
    }

    @Override
    public void saveHouse(final Boolean finish) {
        if (isValid()) {
            house.temporary = false;
            houseSave.execute(house, new Callback<Void>() {
                @Override
                public void onResponse(Void response) {
                    hasChanges = false;
                    view.invalidateOptionMenu();
                    if (finish) {
                        view.onHouseSaved();
                    } else {
                        view.showSnackbar(R.string.house_saved);
                    }
                }

                @Override
                public void onError(ErrorBundle error) {
                    Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_SAVE, error.toString());
                    view.showSnackbar(R.string.house_saved_error);
                }
            });
        } else {
            view.validate();
        }
    }

    @Override
    public void confirmVisit(final int result) {
        house.temporary = false;
        houseSave.execute(house, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                hasChanges = false;
                view.invalidateOptionMenu();
                houseHasVisitInProgress.execute(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        if (response) {
                            view.confirmVisit(result);
                        } else {
                            createVisit(result);
                        }
                    }
                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_VISITINPROGRESS, error.toString());
                        view.showSnackbar(R.string.house_saved_error);
                    }
                });
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_SAVE, error.toString());
                view.showSnackbar(R.string.house_saved_error);
            }
        });
    }

    @Override
    public void createVisit(int result) {
        view.onCreateVisit(house.uuid, result);
    }

    @Override
    public void convertVisit(final int result) {
        visitDelete.execute(visit.uuid, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.onCreateVisit(house.uuid, result);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_DELETEVISIT, error.toString());
                view.showSnackbar(R.string.error_registry_units_empty);
            }
        });
    }

    @Override
    public void deleteHouse() {
        houseDelete.execute(house, new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                view.onDeleteHouse();
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_HOUSE_DELETEHOUSE, error.toString());
                view.showSnackbar(R.string.house_deleted_error);
            }
        });
    }

    @Override
    public Boolean hasChanges() {
        return hasChanges;
    }


    @Override
    public void setStreetName(String streetName) {
        if (!streetName.equals(house.streetName)) {
            hasChanges = true;
            house.streetName = streetName;
            showVisitButton();
            view.invalidateOptionMenu();
        }
    }

    @Override
    public void setStreetNumber(String streetNumber) {
        if (!streetNumber.equals(house.streetNumber)) {
            hasChanges = true;
            house.streetNumber = streetNumber;
            showVisitButton();
            view.invalidateOptionMenu();
        }
    }

    @Override
    public void setQrCode(String qr) {
        if (!qr.equals(house.qrCode)) {
            hasChanges = true;
            house.qrCode = qr;
            view.invalidateOptionMenu();
        }
    }

    private void showVisitButton() {
        view.showVisitButton(isValid(), house.visited);
    }

    @Override
    public Boolean isValid() {
        return house != null && !TextUtils.isEmpty(house.streetName) && !TextUtils.isEmpty(house.streetNumber);
    }

    @Override
    public Boolean hasQr() {
        return house != null && !TextUtils.isEmpty(house.qrCode);
    }

    @Override
    public Boolean isNew() {
        return house != null && house.isNewHouse && !TextUtils.isEmpty(house.streetName) && !TextUtils.isEmpty(house.streetNumber);
    }

    @Override
    public Boolean hasVisit() {
        return visit != null;
    }

    @Override
    public void editVisit() {
        view.onCreateVisit(visit.house.uuid, visit.result.id);
    }
}