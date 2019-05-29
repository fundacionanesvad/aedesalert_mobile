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

package com.gruposca.sapev.so.activity.di;

import com.gruposca.sapev.presenter.InspectionSummaryPresenter;
import com.gruposca.sapev.presenter.InspectionSummaryPresenterImpl;
import com.gruposca.sapev.so.activity.InspectionSummaryActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                InspectionSummaryActivity.class
        },
        complete = false
)
public class InspectionSummaryActivityModule {

    @Provides
    public InspectionSummaryPresenter provideInspectionPresenter(InspectionSummaryPresenterImpl presenter) { return presenter; }
}