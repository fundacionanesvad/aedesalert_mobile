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

package com.gruposca.sapev.so.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.presenter.PlanSyncPresenter;
import com.gruposca.sapev.so.activity.AbsActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SyncPlanDialog extends AbsDialog implements PlanSyncPresenter.View  {

    @Inject
    protected PlanSyncPresenter presenter;

    @InjectView(R.id.progress) protected ProgressBar progress;
    @InjectView(R.id.state) protected TextView state;
    
    private OnSyncPlanListener onSyncPlanListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sync_plan, null);
        ButterKnife.inject(this, view);
        presenter.initialize(this);
        presenter.syncPlan();
        builder.setTitle(R.string.action_sync)
                .setView(view)
                .setPositiveButton(R.string.action_terminate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onSyncPlanListener != null)
                            onSyncPlanListener.onFinish();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        AbsActivity absActivity =(AbsActivity) activity;
        absActivity.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        enablePositiveButton(false);
    }

    @Override
    public void setProgressValue(int value) {
        if (value == 0 || value == 100) {
            progress.setIndeterminate(true);
        } else {
            progress.setProgress(value);
            progress.setIndeterminate(false);
        }
    }

    @Override
    public void setProgressState(int resId) {
        state.setText(resId);
    }

    @Override
    public void onFinish() {
        state.setText(R.string.plan_synchronized);
        progress.setIndeterminate(false);
        progress.setProgress(100);
        enablePositiveButton(true);
    }

    @Override
    public void onError(int message, ErrorBundle error) {

        if (onSyncPlanListener != null)
            onSyncPlanListener.onError(message, error);
    }

    private void enablePositiveButton(boolean enable) {
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enable);
    }

    public void setOnSyncPlanListener(OnSyncPlanListener l) {
        onSyncPlanListener = l;
    }

    public interface OnSyncPlanListener {
        void onFinish();
        void onError(int message, ErrorBundle error);
    }
}