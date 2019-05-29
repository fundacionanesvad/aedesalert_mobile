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

import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.PlanExists;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainPresenterImpl implements MainPresenter {

    private MainPresenter.View view;
    private final PlanExists planExists;

    @Inject
    public MainPresenterImpl(PlanExists planExists) {
        this.planExists = planExists;
    }

    @Override
    public void initialize(View view) {
        this.view = view;
    }

    @Override
    public void load() {
        planExists.execute(new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    view.showPlan();
                } else {
                    view.showLogin();
                }
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_MAIN_PLAN, error.toString());
            }
        });
    }
}