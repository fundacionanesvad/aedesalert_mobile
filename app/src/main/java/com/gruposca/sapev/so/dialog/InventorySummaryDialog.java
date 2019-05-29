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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gruposca.sapev.R;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InventorySummaryDialog extends AbsDialog {

    public static final String PARAM_SAMPLE = "PARAM_SAMPLE";
    public static final String PARAM_PACKETS = "PARAM_PACKETS";
    public static final String PARAM_DESTROYED = "PARAM_DESTROYED";
    public static final String PARAM_UNITS = "PARAM_UNITS";

    @InjectView(R.id.sample)
    protected TextView sample;
    @InjectView(R.id.treated)
    protected TextView treated;
    @InjectView(R.id.destroyed)
    protected TextView destroyed;
    @InjectView(R.id.units)
    protected TextView units;
    @InjectView(R.id.alert)
    protected TextView alert;
    @InjectView(R.id.notice)
    protected TextView notice;

    private OnConfirmListener onConfirmListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_inventory_summary, null);
        ButterKnife.inject(this, view);
        builder.setTitle(R.string.summary)
                .setView(view)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onConfirmListener != null)
                            onConfirmListener.onConfirm();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onConfirmListener != null)
                            onConfirmListener.onGoBack();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle.containsKey(PARAM_SAMPLE)) {
            sample.setText(bundle.getString(PARAM_SAMPLE));
        } else {
            sample.setText(R.string.action_no);
        }
        if (bundle.containsKey(PARAM_PACKETS)) {
            treated.setText(bundle.getString(PARAM_PACKETS));
        } else {
            treated.setText(R.string.action_no);
        }
        destroyed.setText(bundle.getBoolean(PARAM_DESTROYED) ? R.string.action_yes : R.string.action_no);
        units.setText(String.format(Locale.getDefault(),"%d", bundle.getInt(PARAM_UNITS)));
        boolean sample = bundle.containsKey(PARAM_SAMPLE);
        boolean action = bundle.containsKey(PARAM_PACKETS) || bundle.getBoolean(PARAM_DESTROYED);
        alert.setVisibility(sample && !action ? View.VISIBLE : View.GONE);
        notice.setVisibility(!sample && action ? View.VISIBLE : View.GONE);
    }

    public void setOnConfirmListener(OnConfirmListener l) {
        onConfirmListener = l;
    }

    public interface OnConfirmListener {
        void onConfirm();
        void onGoBack();
    }
}