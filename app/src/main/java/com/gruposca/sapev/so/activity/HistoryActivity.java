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
import android.view.MenuItem;

import com.gruposca.sapev.R;
import com.gruposca.sapev.so.activity.di.HistoryActivityModule;
import com.gruposca.sapev.so.fragment.RegistryListFragment;

public class HistoryActivity extends AbsActivity {

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_default;
    }

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new HistoryActivityModule()
        };
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        RegistryListFragment fragment = new RegistryListFragment();
        fragment.setArguments(getIntent().getExtras());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getFragmentTagForFragmentManager())
                .commit();
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