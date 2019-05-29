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

import com.gruposca.sapev.presenter.FeverishListPresenter;
import com.gruposca.sapev.presenter.FeverishListPresenterImpl;
import com.gruposca.sapev.presenter.InspectionPresenter;
import com.gruposca.sapev.presenter.InspectionPresenterImpl;
import com.gruposca.sapev.presenter.InventoryListPresenter;
import com.gruposca.sapev.presenter.InventoryListPresenterImpl;
import com.gruposca.sapev.presenter.PersonListDialogPresenter;
import com.gruposca.sapev.presenter.PersonListDialogPresenterImpl;
import com.gruposca.sapev.presenter.PersonDetailPresenter;
import com.gruposca.sapev.presenter.PersonDetailPresenterImpl;
import com.gruposca.sapev.presenter.PersonListPresenter;
import com.gruposca.sapev.presenter.PersonListPresenterImpl;
import com.gruposca.sapev.presenter.SymptomListDialogPresenter;
import com.gruposca.sapev.presenter.SymptomListDialogPresenterImpl;
import com.gruposca.sapev.presenter.VisitsSummaryPresenter;
import com.gruposca.sapev.presenter.VisitsSummaryPresenterImpl;
import com.gruposca.sapev.so.activity.InspectionActivity;
import com.gruposca.sapev.so.activity.VisitsSummaryActivity;
import com.gruposca.sapev.so.dialog.FeverishPersonListDialog;
import com.gruposca.sapev.so.dialog.PersonDetailDialog;
import com.gruposca.sapev.so.dialog.SymptomsListDialog;
import com.gruposca.sapev.so.fragment.FeverishListFragment;
import com.gruposca.sapev.so.fragment.InventoryListFragment;
import com.gruposca.sapev.so.fragment.PersonListFragment;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                InspectionActivity.class,
                InventoryListFragment.class,
                PersonListFragment.class,
                FeverishListFragment.class,
                FeverishPersonListDialog.class,
                SymptomsListDialog.class,
                PersonDetailDialog.class
        },
        complete = false
)
public class InspectionActivityModule {

    @Provides
    public InspectionPresenter provideInspectionPresenter(InspectionPresenterImpl presenter) { return presenter; }

    @Provides
    public InventoryListPresenter provideInventoryListPresenter(InventoryListPresenterImpl presenter) { return presenter; }

    @Provides
    public PersonListPresenter providePersonListPresenter(PersonListPresenterImpl presenter) { return presenter; }

    @Provides
    public FeverishListPresenter provideFeverishListPresenter(FeverishListPresenterImpl presenter) { return presenter; }

    @Provides
    public PersonListDialogPresenter providePersonListDialogPresenter(PersonListDialogPresenterImpl presenter) { return presenter; }

    @Provides
    public SymptomListDialogPresenter provideSymptomListDialogPresenter(SymptomListDialogPresenterImpl presenter) { return presenter; }

    @Provides
    public PersonDetailPresenter providePersonDetailPresenter(PersonDetailPresenterImpl presenter) { return presenter; }

}