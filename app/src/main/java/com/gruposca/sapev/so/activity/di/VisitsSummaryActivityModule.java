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

import com.gruposca.sapev.presenter.HousesSummaryPresenter;
import com.gruposca.sapev.presenter.HousesSummaryPresenterImpl;
import com.gruposca.sapev.presenter.InventorySummaryPresenter;
import com.gruposca.sapev.presenter.InventorySummaryPresenterImpl;
import com.gruposca.sapev.presenter.SamplesSummaryPresenter;
import com.gruposca.sapev.presenter.SamplesSummaryPresenterImpl;
import com.gruposca.sapev.presenter.VisitsSummaryPresenter;
import com.gruposca.sapev.presenter.VisitsSummaryPresenterImpl;
import com.gruposca.sapev.so.activity.VisitsSummaryActivity;
import com.gruposca.sapev.so.fragment.HousesSummaryFragment;
import com.gruposca.sapev.so.fragment.InventorySummaryFragment;
import com.gruposca.sapev.so.fragment.SamplesSummaryFragment;


import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
               VisitsSummaryActivity.class,
               HousesSummaryFragment.class,
               SamplesSummaryFragment.class,
               InventorySummaryFragment.class
        },
        complete = false
)
public class VisitsSummaryActivityModule {

    @Provides
    public VisitsSummaryPresenter provideVisitsSummaryPresenter(VisitsSummaryPresenterImpl presenter) { return presenter; }

    @Provides
    public HousesSummaryPresenter provideHousesSummaryPresenter(HousesSummaryPresenterImpl presenter) { return presenter; }

    @Provides
    public SamplesSummaryPresenter provideSamplesSummaryPresenter(SamplesSummaryPresenterImpl presenter) { return presenter; }

    @Provides
    public InventorySummaryPresenter provideInventorySummaryPresenter(InventorySummaryPresenterImpl presenter) { return presenter; }

}