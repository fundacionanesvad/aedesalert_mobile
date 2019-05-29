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

import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.RegistryListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.interactor.RegistryGetList;
import com.gruposca.sapev.tool.Errors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegistryListPresenterImpl implements RegistryListPresenter {

    private RegistryListPresenter.View view;
    private final RegistryGetList registryGetList;
    private final PlanGet planGet;

    @Inject
    public RegistryListPresenterImpl(RegistryGetList registryGetList, PlanGet planGet) {
        this.registryGetList = registryGetList;
        this.planGet = planGet;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void initialize(final String visitUuid) {
        view.showLoading();
        planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(final Plan plan) {
                registryGetList.execute(visitUuid, new Callback<List<RegistryListQuery>>() {
                    @Override
                    public void onResponse(List<RegistryListQuery> response) {
                        view.showRegistryList(response, plan);
                    }

                    @Override
                    public void onError(ErrorBundle error) {
                        view.showErrorLoad(this.getClass().toString(), Errors.METHOD_REGISTRY_GETLIST, error.toString());
                    }
                });
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(this.getClass().toString(), Errors.METHOD_GET_PLAN, error.toString());
            }
        });
    }
}