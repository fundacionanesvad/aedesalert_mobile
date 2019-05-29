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

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.IntegerList;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.ElementGetList;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.interactor.RegistryHasSample;
import com.gruposca.sapev.interactor.RegistrySave;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegistryPresenterImpl implements RegistryPresenter {

    private static final float VOLUME_LITER_RATIO = 1000;
    private static final int NUM_SAMPLES = 20;

    private final ElementGetList elementGetList;
    private final RegistrySave registrySave;
    private final PlanGet planGet;
    private final RegistryHasSample registryHasSample;


    private RegistryPresenter.View view;
    private Registry registry;
    private List<Element> states;
    private String sample;
    private Plan plan;
    private List<String> listCodes;

    @Inject
    public RegistryPresenterImpl(ElementGetList elementGetList, RegistrySave registrySave, PlanGet planGet, RegistryHasSample registryHasSample) {
        this.elementGetList = elementGetList;
        this.registrySave = registrySave;
        this.planGet = planGet;
        this.registryHasSample = registryHasSample;
    }

    @Override
    public void initialize(View view, String visitUuid, Integer containerId) {
        this.view = view;
        this.registry = new Registry();
        this.registry.visit.uuid = visitUuid;
        this.registry.container.id = containerId;
        generateSampleCode();
    }

    @Override
    public void load() {
        elementGetList.execute(Element.TABLE_STATES, new Callback<List<Element>>() {
            @Override
            public void onResponse(List<Element> response) {
                states = response;
                view.onStatesLoaded(response);
                view.updateView(registry);
                view.populateCommentAutocomplete();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_REGISTRY_ELEMENT_GETLIST, error.toString());
            }
        });
    }

    @Override
    public void setComment(String comment) {
        registry.comment = comment;
    }

    @Override
    public void setFocus(Boolean focus) {
        registry.focus = focus;
        registry.sample = focus ? sample : "";
        registry.states = new IntegerList();
        if (focus && registry.units > 1) {
            view.showUnitsDialog(registry.units - 1);
            registry.units = 1;
        }
        view.updateView(registry);
    }

    @Override
    public void setStates(IntegerList states) {
        registry.states = states;
    }

    @Override
    public void setTreated(Boolean treated) {
        registry.treated = treated;
        registry.packets = 0;
        if (treated && registry.units > 1) {
            view.showUnitsDialog(registry.units - 1);
            registry.units = 1;
        }
        view.updateView(registry);
    }

    @Override
    public void setPackets(Integer packets) {
        registry.packets = packets;
    }

    @Override
    public void setDestroyed(Boolean destroyed) {
        registry.destroyed = destroyed;
    }

    @Override
    public void setUnits(Integer units) {
        registry.units = units;
    }

    @Override
    public void confirmSave() {
        if (registry.focus && registry.states.size() == 0) {
            view.showSnackbar(R.string.error_registry_states_empty);
        } else if (registry.treated && registry.packets == 0) {
            view.showSnackbar(R.string.error_registry_packets_empty);
        } else if (registry.units == 0) {
            view.showSnackbar(R.string.error_registry_units_empty);
        } else {
            view.confirmSave(registry, plan);
        }
    }

    @Override
    public void save() {
        registrySave.execute(registry, new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                view.close();
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_REGISTRY_SAVE, error.toString());
                view.showSnackbar(R.string.error_registry_save);
            }
        });
    }

    private void generateSampleCode() {
       planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(Plan response) {
                plan = response;
                listCodes = getListCodes();
                registryHasSample.execute(listCodes, new Callback<String>() {
                    @Override
                    public void onResponse(String response) {
                        sample = response;
                    }
                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_REGISTRY_HASSAMPLE, error.toString());
                        sample = "";
                    }
                });
                view.onPlanLoaded(plan);
            }

            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_REGISTRY_PLANGET, error.toString());
                sample = "";
            }
        });
    }

    private List<String> getListCodes() {
        List<String> listCodes = new ArrayList<>();
        int codeIni = (NUM_SAMPLES * plan.id) - (NUM_SAMPLES - 1);
        for (int i = 0; i < NUM_SAMPLES; i++) {
            String code = Integer.toString(codeIni, 16);
            while (code.length() < 6) {
                code = "0" + code;
            }
            codeIni++;
            listCodes.add(code.toUpperCase());
        }
        return listCodes;
    }

    public Integer calculatePackets(Integer depth, Integer width, Integer height) {
        Integer number = Math.round(depth * width * height / VOLUME_LITER_RATIO / plan.larvicideWaterVolume);
        if (number == 0)
            number = 1;
        return number;
    }

    public Integer calculatePackets(Integer diameter, Integer height) {
        Integer number = (int) Math.round(height * Math.PI * Math.pow(diameter / 2, 2) / VOLUME_LITER_RATIO / plan.larvicideWaterVolume);
        if (number == 0)
            number = 1;
        return number;
    }

    public Integer calculatePackets(Integer liters) {
        Integer number = (int) Math.round(liters / plan.larvicideWaterVolume);
        if (number == 0)
            number = 1;
        return number;
    }
}