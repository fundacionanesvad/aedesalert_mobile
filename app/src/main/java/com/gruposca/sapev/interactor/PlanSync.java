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

import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.PlanRepository;

import javax.inject.Inject;

/**
 * Created by iut on 14/05/2015.
 */
public class PlanSync extends AbsInteractor<Boolean> {

    private PlanRepository planRepository;
    private Integer id;

    @Inject
    public PlanSync(ThreadExecutor threadExecutor, PostThreadExecutor postThreadExecutor, PlanRepository planRepository) {
        super(threadExecutor, postThreadExecutor);
        this.planRepository = planRepository;
    }

    public void execute(Integer id, Callback<Boolean> callback) {
        this.id = id;
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            notifyResponse(planRepository.syncPlan(id));
        } catch (Exception e) {
            notifyError(e);
        }
    }
}