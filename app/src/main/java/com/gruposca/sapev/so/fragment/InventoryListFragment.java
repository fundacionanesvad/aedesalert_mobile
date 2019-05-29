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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.gruposca.sapev.presenter.InventoryListPresenter;
import com.gruposca.sapev.so.activity.HistoryActivity;
import com.gruposca.sapev.so.activity.RegistryActivity;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.InventoryListAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.dialog.VisitDeleteDialog;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class InventoryListFragment extends AbsFragment implements InventoryListPresenter.View, AbsAdapter.OnItemClickListener {

    private static final int REQUEST_HISTORY = 1;
    private static final int REQUEST_REGISTRY = 2;

    @Inject
    InventoryListPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.error) protected TextView error;
    @InjectView(R.id.content) protected View content;
    @InjectView(R.id.list) protected RecyclerView list;

    private InventoryListAdapter adapter;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_inspection_inventory_list;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.inventory_list_title;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.setView(this, getArguments().getString(Params.VISIT_UUID));
        presenter.loadInventoryList();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inspection, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra(Params.VISIT_UUID, getArguments().getString(Params.VISIT_UUID));
                startActivityForResult(intent, REQUEST_HISTORY);
                break;
            case R.id.action_delete_visit:
                deleteVisit();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void showInventories(List<InventoryListQuery> inventories) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        if (adapter == null) {
            adapter = new InventoryListAdapter(getActivity());
            adapter.setOnItemClickListener(this);
            list.setAdapter(adapter);
        }
        adapter.animateTo(inventories, true);
    }

    @Override
    public void onDeleteVisit() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildAdapterPosition(view);
        InventoryListQuery inventory = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), RegistryActivity.class);
        intent.putExtra(Params.VISIT_UUID, inventory.visitUuid);
        intent.putExtra(Params.CONTAINER_ID, inventory.containerId);
        intent.putExtra(Params.CONTAINER_NAME, inventory.containerName);
        startActivityForResult(intent, REQUEST_REGISTRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_HISTORY:
                    final String registryUuid = data.getStringExtra(Params.REGISTRY_UUID);
                    presenter.deleteRegistry(registryUuid);
                    Snackbar.make(getView(), R.string.inventory_registry_deleted, 10000)
                            .setAction(R.string.action_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.restoreRegistry(registryUuid);
                                }
                            })
                            .show();
                    break;

                case REQUEST_REGISTRY:
                    presenter.loadInventoryList();
                    break;
            }
        }
    }

    private void deleteVisit (){
        VisitDeleteDialog dialog = new VisitDeleteDialog();
        dialog.setOnDeleteVisitConfirmListener(new VisitDeleteDialog.OnDeleteVisitConfirmListener() {
            @Override
            public void confirm() {
                presenter.deleteVisit(getArguments().getString(Params.VISIT_UUID));
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }
}