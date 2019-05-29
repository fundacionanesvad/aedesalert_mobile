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

package com.gruposca.sapev.so.fragment;

import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.HousesSummaryQuery;
import com.gruposca.sapev.presenter.HousesSummaryPresenter;

import java.util.Locale;

import javax.inject.Inject;
import butterknife.InjectView;

public class HousesSummaryFragment extends AbsFragment implements HousesSummaryPresenter.View{

    @Inject
    HousesSummaryPresenter presenter;
    @InjectView(R.id.larvicide) protected TextView larvicide;
    @InjectView(R.id.febriles) protected TextView febriles;
    @InjectView(R.id.persons) protected TextView persons;
    @InjectView(R.id.treated) protected TextView treated;
    @InjectView(R.id.destroyed) protected TextView destroyed;
    @InjectView(R.id.closed) protected TextView closed;
    @InjectView(R.id.inspected) protected TextView inspected;
    @InjectView(R.id.focus) protected TextView focus;
    @InjectView(R.id.reluctant) protected TextView reluctant;
    @InjectView(R.id.abandoned) protected TextView abandoned;
    @InjectView(R.id.reconverted) protected TextView reconverted;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_houses_summary;
    }

    @Override
    protected void onFragmentInitializeViews() {
        presenter.initialize(this);
        presenter.load();
    }

    @Override
    public void showSummary(HousesSummaryQuery housesSummaryQuery, Plan plan) {
        larvicide.setText(getString(R.string.weight_grams, housesSummaryQuery.larvicide, plan.larvicideUnity));
        febriles.setText(getString(housesSummaryQuery.febriles));
        persons.setText(getString(housesSummaryQuery.persons));
        treated.setText(getString(housesSummaryQuery.treated));
        destroyed.setText(getString(housesSummaryQuery.destroyed));
        closed.setText(getString(housesSummaryQuery.closed));
        inspected.setText(getString(housesSummaryQuery.inspected));
        focus.setText(getString(housesSummaryQuery.focus));
        reluctant.setText(getString(housesSummaryQuery.reluctant));
        abandoned.setText(getString(housesSummaryQuery.abandoned));
        reconverted.setText(getString(housesSummaryQuery.reconverted));
    }

    private String getString(Integer value) {
        return String.format(Locale.getDefault(), "%d", value);
    }
}