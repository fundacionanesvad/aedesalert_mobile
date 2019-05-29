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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.gruposca.sapev.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

public class VolumeLitersDialog extends AbsDialog {

    private VolumeLitersDialog.OnVolumeLitersSelectedListener onVolumeLitersSelectedListener;

    Integer liters = null;

    @InjectView(R.id.value)
    protected EditText value;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_volume_liters, null);
        ButterKnife.inject(this, view);
        builder.setView(view)
                .setTitle(R.string.volume_liters)
                .setView(view)
                .setPositiveButton(R.string.action_calculate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onVolumeLitersSelectedListener != null)
                            onVolumeLitersSelectedListener.onCalculate(liters);
                    }
                })
                .setNegativeButton(R.string.action_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onVolumeLitersSelectedListener != null)
                            onVolumeLitersSelectedListener.onCanceled();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(value, 0);
            }
        }, 250);
    }

    @OnTextChanged(R.id.value)
    public void onValueChanged(CharSequence text) {
        Integer value = TextUtils.isEmpty(text) ? null : Integer.parseInt(text.toString());
        liters = value;
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(value != null);
    }

    public void setOnVolumeLitersSelectedListener(VolumeLitersDialog.OnVolumeLitersSelectedListener l) {
        onVolumeLitersSelectedListener = l;
    }

    public interface OnVolumeLitersSelectedListener {
        void onCanceled();
        void onCalculate(Integer liters);
    }
}