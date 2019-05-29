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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.gruposca.sapev.R;
import com.gruposca.sapev.so.activity.di.HouseListActivityModule;
import com.gruposca.sapev.so.fragment.HouseListFragment;
import com.gruposca.sapev.tool.Params;

public class HouseListActivity extends AbsActivity {

    @Override
    protected Object[] getInjectionModules() {
        return new Object[]{
                new HouseListActivityModule()
        };
    }

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_house_list;
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            HouseListFragment fragment = new HouseListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Params.AREA_ID, getIntent().getIntExtra(Params.AREA_ID, 0));
            bundle.putLong(Params.AREA_OTHER_INSPECTIONS, getIntent().getLongExtra(Params.AREA_OTHER_INSPECTIONS, 0));
            fragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, fragment.getFragmentTagForFragmentManager())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra(Params.AREA_NAME));
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
}