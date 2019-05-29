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
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.gruposca.sapev.R;

public class NumberPickerDialog extends AbsDialog implements NumberPicker.OnValueChangeListener {

    public static final String PARAM_NUMBER = "PARAM_NUMBER";
    private OnNumberSelectedListener onNumberSelectedListener;
    private Integer number;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        number = getArguments().getInt(PARAM_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(0);
        picker.setMaxValue(150);
        picker.setValue(number);
        picker.setOnValueChangedListener(this);
        builder.setView(picker)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onNumberSelectedListener != null)
                            onNumberSelectedListener.onNumberSelected(number);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        return builder.create();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        number = newVal;
    }

    public void setOnNumberSelectedListener(OnNumberSelectedListener l) {
        onNumberSelectedListener = l;
    }

    public interface OnNumberSelectedListener {
        void onNumberSelected(Integer number);
    }
}