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

package com.gruposca.sapev.so.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import com.gruposca.sapev.R;
import com.gruposca.sapev.presenter.VisitsSummaryPresenter;
import com.gruposca.sapev.so.activity.di.VisitsSummaryActivityModule;
import com.gruposca.sapev.so.adapter.PagesAdapter;
import com.gruposca.sapev.so.fragment.HousesSummaryFragment;
import com.gruposca.sapev.so.fragment.InventorySummaryFragment;
import com.gruposca.sapev.so.fragment.SamplesSummaryFragment;


import javax.inject.Inject;

import butterknife.InjectView;

public class VisitsSummaryActivity extends AbsActivity implements VisitsSummaryPresenter.View  {

    private final static int REQUEST_SUMMARY = 2;

    @Inject
    VisitsSummaryPresenter presenter;
    @InjectView(R.id.pager)
    protected ViewPager mPager;
    @InjectView(R.id.tab_layout)
    protected TabLayout tabLayout;
    protected PagesAdapter mPagerAdapter;

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new VisitsSummaryActivityModule()
        };
    }

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_visits_summary;
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        presenter.initialize(this);
        presenter.load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showSummary() {
        Bundle bundle = new Bundle();

        mPagerAdapter = new PagesAdapter(this, getFragmentManager(), mPager);
        mPagerAdapter.addTab(getString(R.string.houses_summary_title), HousesSummaryFragment.class, bundle);
        mPagerAdapter.addTab(getString(R.string.inventory_summary_title), InventorySummaryFragment.class, bundle);
        mPagerAdapter.addTab(getString(R.string.samples_summary_title), SamplesSummaryFragment.class, bundle);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
             case REQUEST_SUMMARY:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}