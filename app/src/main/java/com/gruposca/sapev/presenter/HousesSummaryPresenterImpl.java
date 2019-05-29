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
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.HousesSummaryQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.HousesSummaryGet;
import com.gruposca.sapev.interactor.PlanGet;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;

public class HousesSummaryPresenterImpl implements HousesSummaryPresenter {

    private HousesSummaryPresenter.View view;
    private final HousesSummaryGet housesSummaryGet;
    private final PlanGet planGet;

    @Inject
    public HousesSummaryPresenterImpl(HousesSummaryGet housesSummaryGet, PlanGet planGet) {
        this.housesSummaryGet = housesSummaryGet;
        this.planGet = planGet;
    }

    @Override
    public void initialize(View view) {
        this.view = view;
    }

    @Override
    public void load() {
        planGet.execute(new Callback<Plan>() {
            @Override
            public void onResponse(final Plan plan) {
                housesSummaryGet.execute(new Callback<HousesSummaryQuery>() {
                    @Override
                    public void onResponse(HousesSummaryQuery response) {
                        view.showSummary(response, plan);
                    }

                    @Override
                    public void onError(ErrorBundle error) {
                        Logs.log(Logs.ERROR, getClass().toString(), Errors.METHOD_HOUSE_SUMMARY, error.toString());
                        view.showSnackbar(R.string.message_get_data_error);
                    }
                });
            }

            @Override
            public void onError(ErrorBundle error) {

            }
        });
    }
}