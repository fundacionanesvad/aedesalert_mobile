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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;
import com.gruposca.sapev.presenter.HouseListPresenter;
import com.gruposca.sapev.so.activity.HouseActivity;
import com.gruposca.sapev.so.activity.InspectionActivity;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.HouseListAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.dialog.QRNotFoundDialog;
import com.gruposca.sapev.so.dialog.QRNotValidDialog;
import com.gruposca.sapev.tool.Params;
import com.gruposca.sapev.tool.QRs;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class HouseListFragment extends AbsFragment implements HouseListPresenter.View, AbsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private static final int REQUEST_HOUSE = 1;
    private static final int REQUEST_VISIT = 2;
    private static final int REQUEST_QR_SCANNER = 3;

    @Inject
    HouseListPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.list) protected RecyclerView list;
    @InjectView(R.id.empty) protected View empty;

    private HouseListAdapter adapter;
    private Menu menu;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_house_list;
    }

    @Override
    protected void onFragmentViewCreated(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);
        presenter.initialize(this, getArguments().getInt(Params.AREA_ID), getArguments().getLong(Params.AREA_OTHER_INSPECTIONS));
        presenter.load(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        trace("onCreateOptionsMenu");
        this.menu = menu;
        inflater.inflate(R.menu.menu_house_list, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        updateMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMenu() {
        if (menu != null) {
            menu.findItem(R.id.action_sort).setIcon(presenter.hasFilter() ? R.drawable.action_sort_selected : R.drawable.action_sort);
            menu.findItem(R.id.filter_inspected).setChecked(presenter.hasFilterInspected());
            menu.findItem(R.id.filter_requalify).setChecked(presenter.hasFilterRequalify());
            menu.findItem(R.id.filter_focus).setChecked(presenter.hasFilterFocus());
            menu.findItem(R.id.filter_feverish).setChecked(presenter.hasFilterFeverish());
            switch (presenter.getSortBy()) {
                case HouseListPresenter.SORT_ADDRESS:
                    menu.findItem(R.id.sort_address).setChecked(true);
                    break;
                case HouseListPresenter.SORT_DATE:
                    menu.findItem(R.id.sort_date).setChecked(true);
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, REQUEST_QR_SCANNER);
                } catch (Exception e) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android"));
                    startActivity(intent);
                }
                break;

            case R.id.filter_inspected:
                presenter.filterInspected(!item.isChecked());
                break;

            case R.id.filter_requalify:
                presenter.filterRequalify(!item.isChecked());
                break;

            case R.id.filter_focus:
                presenter.filterFocus(!item.isChecked());
                break;

            case R.id.filter_feverish:
                presenter.filterFeverish(!item.isChecked());
                break;

            case R.id.sort_address:
                presenter.sortBy(HouseListPresenter.SORT_ADDRESS);
                break;

            case R.id.sort_date:
                presenter.sortBy(HouseListPresenter.SORT_DATE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHouseList(List<HouseListQuery> items, Boolean forceRefresh) {
        progress.setVisibility(View.GONE);
        if (items.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            if (adapter == null) {
                trace("HouseListAdapter");
                adapter = new HouseListAdapter(getActivity());
                adapter.setOnItemClickListener(this);
                list.setAdapter(adapter);
            }
            adapter.animateTo(items, forceRefresh);
            if (!forceRefresh) {
                list.scrollToPosition(0);
            }
        }
        updateMenu();
    }

    @Override
    public void updateSubtitle(long inspections) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (inspections == 0)
                actionBar.setSubtitle(R.string.inspections_none );
            else if (inspections == 1)
                actionBar.setSubtitle(R.string.inspections_one);
            else
                actionBar.setSubtitle(getString(R.string.inspections_count, inspections));
        }
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildAdapterPosition(view);
        HouseListQuery item = adapter.getItem(position);
        if (item.visitInProgress) {
            showVisit(item.uuid);
        } else {
            showHouse(item.uuid);
        }
    }

    @OnClick(R.id.action_add)
    public void addHouseClick() {
        showHouse("");
    }

    @Override
    public void showFindHouseNoResult() {
        QRNotFoundDialog dialog = new QRNotFoundDialog();
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void showHouse(String houseUuid) {
        Intent intent = new Intent(getActivity(), HouseActivity.class);
        intent.putExtra(Params.HOUSE_UUID, houseUuid);
        intent.putExtra(Params.AREA_ID, getArguments().getInt(Params.AREA_ID));
        startActivityForResult(intent, REQUEST_HOUSE);
    }

    @Override
    public void showVisit(String houseUuid) {
        Intent intent = new Intent(getActivity(), InspectionActivity.class);
        intent.putExtra(Params.HOUSE_UUID, houseUuid);
        startActivityForResult(intent, REQUEST_VISIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_HOUSE:
            case REQUEST_VISIT:
                if (resultCode == Activity.RESULT_OK) {
                    presenter.load(null);
                }
                break;
            case REQUEST_QR_SCANNER:
                if (resultCode == Activity.RESULT_OK) {
                    String qr = data.getStringExtra("SCAN_RESULT");
                    if (QRs.validate(qr)) {
                        presenter.findHouseByQR(qr);
                    } else {
                        QRNotValidDialog dialog = new QRNotValidDialog();
                        dialog.show(getFragmentManager(), dialog.getTag());
                    }
                }
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenter.filterQuery(query.toLowerCase());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        presenter.filterQuery(newText.toLowerCase());
        return true;
    }
}