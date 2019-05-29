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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.InventorySummaryQuery;
import com.gruposca.sapev.presenter.InventorySummaryPresenter;
import com.gruposca.sapev.so.adapter.InventorySummaryAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;
import butterknife.InjectView;

public class InventorySummaryFragment extends AbsFragment implements InventorySummaryPresenter.View {

    @Inject
    InventorySummaryPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.error) protected TextView error;
    @InjectView(R.id.content) protected View content;
    @InjectView(R.id.list) protected RecyclerView list;
    @InjectView(R.id.empty) protected TextView empty;

    private InventorySummaryAdapter adapter;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_inventory_summary;
    }


    @Override
    protected void onFragmentInitializeViews() {
        presenter.setView(this);
        presenter.loadInventoryList();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public void showErrorLoad(String module, String method, String errorLog) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        Logs.log(Logs.ERROR, module, method, errorLog);
    }

    @Override
    public void showInventories(List<InventorySummaryQuery> inventories) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if(inventories.size()==0){
            empty.setVisibility(View.VISIBLE);
        }else {
            empty.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new InventorySummaryAdapter(getActivity());
                list.setAdapter(adapter);
            }
            adapter.animateTo(inventories, true);
        }
    }

}