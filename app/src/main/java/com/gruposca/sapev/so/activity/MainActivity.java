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

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.gruposca.sapev.presenter.MainPresenter;
import com.gruposca.sapev.so.activity.di.MainActivityModule;
import com.gruposca.sapev.so.fragment.AbsFragment;
import com.gruposca.sapev.so.fragment.PlanFragment;
import com.gruposca.sapev.so.fragment.LoginFragment;
import com.gruposca.sapev.tool.Params;

import javax.inject.Inject;

public class MainActivity extends AbsActivity implements MainPresenter.View, LoginFragment.OnLoggedListener, PlanFragment.OnAreaSelectedListener, PlanFragment.OnPlanDoneListener {

    public final int REQUEST_HOUSES = 1;

    @Inject
    MainPresenter presenter;

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_default;
    }

    @Override
    protected boolean displayHomeAsUpEnabled() { return false; }

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
            new MainActivityModule()
        };
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        presenter.initialize(this);
        presenter.load();
    }

    private void addFragment(AbsFragment fragment) {
        if (toolbar != null) {
            toolbar.setTitle(fragment.getActionBarTitle());
            toolbar.setSubtitle("");
        }
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getFragmentTagForFragmentManager())
                .commit();
    }

    private boolean isNotActualFragment(Class c) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment == null || !fragment.getClass().equals(c);
    }

    @Override
    public void showLogin() {
        Fragment actual = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (actual == null || !(actual instanceof LoginFragment)) {
            if (toolbar != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.text));
                final Drawable overflowIcon = toolbar.getOverflowIcon();
                if (overflowIcon != null) {
                    int color = getResources().getColor(R.color.half_black);
                    final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    overflowIcon.setColorFilter(colorFilter);
                }
            }
            LoginFragment fragment = new LoginFragment();
            fragment.setOnLoggedListener(this);
            addFragment(fragment);
        }
    }

    @Override
    public void showPlan() {
        Fragment actual = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (actual == null || !(actual instanceof PlanFragment)) {
            if (toolbar != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                final Drawable overflowIcon = toolbar.getOverflowIcon();
                if (overflowIcon != null) {
                    int color = getResources().getColor(R.color.text);
                    final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    overflowIcon.setColorFilter(colorFilter);
                }
            }
            PlanFragment fragment = new PlanFragment();
            fragment.setOnAreaSelectedListener(this);
            fragment.setOnPlanDoneListener(this);
            addFragment(fragment);
        } else {
            PlanFragment fragment = (PlanFragment)actual;
            fragment.reload();
        }
    }

    @Override
    public void showHouses(AreaListQuery area, Long totalInspections) {
        Intent intent = new Intent(this, HouseListActivity.class);
        intent.putExtra(Params.AREA_ID, area.id);
        intent.putExtra(Params.AREA_NAME, area.name);
        intent.putExtra(Params.AREA_OTHER_INSPECTIONS, totalInspections - area.inspections);
        startActivityForResult(intent, REQUEST_HOUSES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_HOUSES:
                presenter.load();
                break;
        }
    }

    @Override
    public void onLogged() {
        showPlan();
    }

    @Override
    public void onAreaSelected(AreaListQuery area, Long totalInspections) {
        showHouses(area, totalInspections);
    }

    @Override
    public void onPlanDone() {
        showLogin();
    }

    @Override
    protected void onActivityDestroy() {
        System.exit(0);
    }
}