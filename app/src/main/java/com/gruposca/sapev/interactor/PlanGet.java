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

package com.gruposca.sapev.interactor;

import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.PlanRepository;

import javax.inject.Inject;

/**
 * Created by iut on 01/12/2016.
 */
public class PlanGet extends AbsInteractor<Plan>{


    private final PlanRepository planRepository;

    @Inject
    public PlanGet(ThreadExecutor threadExecutor, PostThreadExecutor postThreadExecutor,PlanRepository planRepository) {
        super(threadExecutor, postThreadExecutor);
        this.planRepository = planRepository;
    }

    public void execute(Callback<Plan> callback) {
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            notifyResponse(planRepository.getPlan());
        } catch (Exception e) {
            notifyError(e);
        }
    }
}
