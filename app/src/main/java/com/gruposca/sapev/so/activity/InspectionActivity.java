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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.github.clans.fab.FloatingActionMenu;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.presenter.InspectionPresenter;
import com.gruposca.sapev.so.activity.di.InspectionActivityModule;
import com.gruposca.sapev.so.adapter.PagesAdapter;
import com.gruposca.sapev.so.fragment.FeverishListFragment;
import com.gruposca.sapev.so.fragment.InventoryListFragment;
import com.gruposca.sapev.so.fragment.PersonListFragment;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;

import javax.inject.Inject;

import butterknife.InjectView;

public class InspectionActivity extends AbsActivity implements InspectionPresenter.View  {

    private final static int REQUEST_SUMMARY = 2;

    @Inject
    InspectionPresenter presenter;
    @InjectView(R.id.pager)
    protected ViewPager mPager;
    @InjectView(R.id.tab_layout)
    protected TabLayout tabLayout;
    protected PagesAdapter mPagerAdapter;

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new InspectionActivityModule()
        };
    }

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_inspection;
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        presenter.initialize(this, getIntent().getStringExtra(Params.HOUSE_UUID));
        presenter.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                presenter.validate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showInspection(Visit visit) {
        Bundle bundle = new Bundle();
        bundle.putString(Params.HOUSE_UUID, visit.house.uuid);
        bundle.putString(Params.VISIT_UUID, visit.uuid);
        bundle.putBoolean(Params.HOUSE_NEW, visit.house.isNewHouse);

        mPagerAdapter = new PagesAdapter(this, getFragmentManager(), mPager);
        mPagerAdapter.addTab(getString(R.string.inventory_list_title), InventoryListFragment.class, bundle);
        mPagerAdapter.addTab(getString(R.string.person_list_title), PersonListFragment.class, bundle);
        mPagerAdapter.addTab(getString(R.string.feverish_list_title), FeverishListFragment.class, bundle);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    public void showSummary(String visitUuid) {
        Intent intent = new Intent(this, InspectionSummaryActivity.class);
        intent.putExtra(Params.VISIT_UUID, visitUuid);
        startActivityForResult(intent, REQUEST_SUMMARY);
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