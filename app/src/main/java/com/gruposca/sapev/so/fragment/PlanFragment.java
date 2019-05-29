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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.presenter.PlanPresenter;
import com.gruposca.sapev.so.activity.VisitsSummaryActivity;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.AreaListAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.dialog.CloseSessionDialog;
import com.gruposca.sapev.so.dialog.CreateFakeDialog;
import com.gruposca.sapev.so.dialog.NotConnectionDialog;
import com.gruposca.sapev.so.dialog.NotVisitsDialog;
import com.gruposca.sapev.so.dialog.SyncConfirmDialog;
import com.gruposca.sapev.so.dialog.SyncPlanDialog;
import com.gruposca.sapev.so.dialog.UnlockAreaDialog;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;
import java.util.List;

import javax.inject.Inject;
import butterknife.InjectView;

public class PlanFragment extends AbsFragment implements PlanPresenter.View, AbsAdapter.OnItemClickListener{

    private static final int REQUEST_PLAN = 1;

    @Inject
    PlanPresenter presenter;

    @InjectView(R.id.list) protected RecyclerView list;
    @InjectView(R.id.version) protected TextView version;
    @InjectView(R.id.progress) protected View progress;

    private OnAreaSelectedListener onAreaSelectedListener;
    private OnPlanDoneListener onPlanDoneListener;
    private AreaListAdapter adapter;
    private Long inspections;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_plan;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.initialize(this);
        presenter.load();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(getString(R.string.version, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_plan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close_session:
                closeSession();
                break;
            case R.id.action_generate_visit:
                CreateFakeDialog dialog = new CreateFakeDialog();
                dialog.show(getFragmentManager(), dialog.getTag());
                break;
            case R.id.action_sync:
                if (hasConnection()){
                    presenter.existsVisits();
                } else {
                    showNotConnection();
                }
                break;
            case R.id.action_summary:
                showSummary();
                break;
            case R.id.action_send_logs:
                if (hasConnection()){
                    presenter.sendLogs();
                } else {
                    showSnackbar(R.string.message_error_connection);
                }
                break;
            case R.id.action_unlock_area:
                unlockArea();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {
        showViews(false, true);
    }

    @Override
    public void showAreas(List<AreaListQuery> areas) {
        showViews(true, false);
        if (adapter == null) {
            adapter = new AreaListAdapter(getActivity());
            adapter.setOnItemClickListener(this);
            list.setAdapter(adapter);
        }
        adapter.animateTo(areas, true);
    }

    @Override
    public void updateTitle(Plan plan) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (plan.reconversionScheduleId == null)
                actionBar.setTitle(plan.type.name);
            else
                actionBar.setTitle(R.string.plan_reconversion);
        }
    }

    @Override
    public void updateSubtitle(long inspections) {
        this.inspections = inspections;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (inspections == 0)
                actionBar.setSubtitle(R.string.inspections_none);
            else if (inspections == 1)
                actionBar.setSubtitle(R.string.inspections_one);
            else
                actionBar.setSubtitle(getString(R.string.inspections_count, inspections));
        }
    }

    private void showViews(Boolean data, Boolean progress) {
        this.list.setVisibility(data ? View.VISIBLE : View.GONE);
        this.version.setVisibility(data ? View.VISIBLE : View.GONE);
        this.progress.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    private void closeSession() {
        CloseSessionDialog dialog = new CloseSessionDialog();
        dialog.setOnCloseSessionListener(new CloseSessionDialog.OnCloseSessionListener() {
            @Override
            public void onConfirm() {
                presenter.deletePlan();
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    private void unlockArea() {
        UnlockAreaDialog dialog = new UnlockAreaDialog();
        dialog.setOnUnlockConfirmListener(new UnlockAreaDialog.OnUnlockConfirmListener() {
            @Override
            public void onConfirm(Integer pin) {
                presenter.unlockArea(pin);
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }
    @Override
    public void updateSector(String sector) {

    }

    @Override
    public void showLogin() {
        Logs.log(Logs.INFO, null, null, Logs.SYNC_PLAN_OK);
        if (onPlanDoneListener != null)
            onPlanDoneListener.onPlanDone();
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildLayoutPosition(view);
        AreaListQuery area = adapter.getItem(position);
        if (onAreaSelectedListener != null)
            onAreaSelectedListener.onAreaSelected(area, inspections);
    }

    @Override
    public void confirmSyncPlan() {
        SyncConfirmDialog dialog = new SyncConfirmDialog();
        dialog.setOnSyncConfirmListener(new SyncConfirmDialog.OnSyncConfirmListener() {
            @Override
            public void onConfirm() {
                syncPlan();
            }

            @Override
            public void onCancel() {
                Logs.log(Logs.INFO, null, null, Logs.SYNC_CANCELED);
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void showNotVisits() {
        NotVisitsDialog dialog = new NotVisitsDialog();
        dialog.show(getFragmentManager(), dialog.getTag());
    }


    private void syncPlan() {
        Logs.log(Logs.INFO, null,null, Logs.SYNC_INI);
        final SyncPlanDialog dialog = new SyncPlanDialog();
        dialog.setOnSyncPlanListener(new SyncPlanDialog.OnSyncPlanListener() {
            @Override
            public void onFinish() {
                showLogin();
            }

            @Override
            public void onError(int message, ErrorBundle error) {
                dialog.dismiss();
                showSyncError(message, error);
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    private void showSyncError(Integer message, ErrorBundle error) {
        Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_PLAN_SYNC, getResources().getString(message) + " " + error.toString());
        showViews(true, false);
        Snackbar.make(getView(), getString(R.string.message_error_info), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmSyncPlan();
                    }
                })
                .show();
    }

    public void setOnAreaSelectedListener(OnAreaSelectedListener l) {
        onAreaSelectedListener = l;
    }

    public interface OnAreaSelectedListener {
        void onAreaSelected(AreaListQuery area, Long totalInspections);
    }

    public void setOnPlanDoneListener(OnPlanDoneListener l) {
        onPlanDoneListener = l;
    }

    public interface OnPlanDoneListener {
        void onPlanDone();
    }

    private boolean hasConnection(){
        Logs.log(Logs.INFO, null, null, Logs.SYNC_DATA_CONNECT);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            Logs.log(Logs.INFO, null, null, Logs.SYNC_DATA_CONNECT_OK);
            return true;
        } else {
            Logs.log(Logs.INFO, getClass().getSimpleName(), Errors.METHOD_PLAN_HASCONNECTION, Logs.SYNC_DATA_CONNECT_ERROR);
            return false;
        }
    }

    public void showNotConnection() {
        NotConnectionDialog dialog = new NotConnectionDialog();
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    public void showSummary() {
        Intent intent = new Intent(getActivity(), VisitsSummaryActivity.class);
        startActivityForResult(intent, REQUEST_PLAN);
    }

    public void reload() {
        presenter.load();
    }
}