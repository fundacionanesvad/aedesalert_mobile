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
import android.view.MenuItem;

import com.gruposca.sapev.R;
import com.gruposca.sapev.presenter.HouseDetailPresenter;
import com.gruposca.sapev.so.activity.di.HouseActivityModule;
import com.gruposca.sapev.so.fragment.HouseDetailFragment;
import com.gruposca.sapev.tool.Params;

public class HouseActivity extends AbsActivity {

    private HouseDetailPresenter.View view;

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_default;
    }

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new HouseActivityModule()
        };
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            HouseDetailFragment fragment = new HouseDetailFragment();
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            bundle.putString(Params.HOUSE_UUID, intent.getStringExtra(Params.HOUSE_UUID));
            bundle.putInt(Params.AREA_ID, intent.getIntExtra(Params.AREA_ID, 0));
            fragment.setArguments(bundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, fragment.getFragmentTagForFragmentManager())
                    .commit();
            view = fragment;
        } else {
            view = (HouseDetailFragment)getFragmentManager().findFragmentByTag(HouseDetailFragment.class.getCanonicalName());
        }
    }

    @Override
    public void onBackPressed() {
        view.confirmChanges();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                view.confirmChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}