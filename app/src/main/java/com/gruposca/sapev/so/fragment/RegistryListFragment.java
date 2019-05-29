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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.RegistryListQuery;
import com.gruposca.sapev.presenter.RegistryListPresenter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.RegistryListAdapter;
import com.gruposca.sapev.so.dialog.RegistryDeleteDialog;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class RegistryListFragment extends AbsFragment implements RegistryListPresenter.View, RegistryListAdapter.OnItemClickListener {

    @Inject
    protected RegistryListPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.empty) protected View empty;
    @InjectView(R.id.error) protected TextView error;
    @InjectView(R.id.content) protected View content;
    @InjectView(R.id.list) protected RecyclerView list;

    private RegistryListAdapter adapter;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_registry_list;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.setView(this);
        presenter.initialize(getArguments().getString(Params.VISIT_UUID));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public void showErrorLoad(String module, String method, String errorLog) {
        progress.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        Logs.log(Logs.ERROR, module, method, errorLog);
    }

    @Override
    public void showRegistryList(List<RegistryListQuery> registries, Plan plan) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if (registries.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new RegistryListAdapter(getActivity(), plan.larvicideDoseName);
                adapter.setOnItemClickListener(this);
                list.setAdapter(adapter);
            }
            adapter.animateTo(registries);
        }
    }

    @Override
    public void onItemClick(View view) {
        final int position = list.getChildAdapterPosition(view);
        RegistryDeleteDialog dialog = new RegistryDeleteDialog();
        dialog.setOnConfirmListener(new RegistryDeleteDialog.OnConfirmListener() {
            @Override
            public void confirm() {
                RegistryListQuery registry = adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(Params.REGISTRY_UUID, registry.uuid);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }
}