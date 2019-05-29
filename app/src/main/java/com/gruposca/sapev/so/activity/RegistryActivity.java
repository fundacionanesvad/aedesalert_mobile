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
import com.gruposca.sapev.so.activity.di.RegistryActivityModule;
import com.gruposca.sapev.so.dialog.RegistryCancelDialog;
import com.gruposca.sapev.so.fragment.RegistryFragment;
import com.gruposca.sapev.tool.Params;

public class RegistryActivity extends AbsActivity {

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_default;
    }

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new RegistryActivityModule()
        };
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        RegistryFragment fragment = new RegistryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Params.VISIT_UUID, getIntent().getStringExtra(Params.VISIT_UUID));
        bundle.putInt(Params.CONTAINER_ID, getIntent().getIntExtra(Params.CONTAINER_ID, 0));
        fragment.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, fragment.getFragmentTagForFragmentManager())
                .commit();
        setTitle(getIntent().getStringExtra(Params.CONTAINER_NAME));
    }

    @Override
    public void onBackPressed() {
        confirmFinish();
    }

    private void confirmFinish() {
        RegistryCancelDialog dialog = new RegistryCancelDialog();
        dialog.setOnConfirmListener(new RegistryCancelDialog.OnConfirmListener() {
            @Override
            public void confirm() {
                finish();
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}