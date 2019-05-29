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

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialog extends AbsDialog implements android.app.DatePickerDialog.OnDateSetListener {

    public static final String PARAM_DATE = "PARAM_DATE";
    private OnDateSelectedListener onDateSelectedListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Long time = getArguments().getLong(PARAM_DATE);
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (onDateSelectedListener != null) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            onDateSelectedListener.onDateSelected(date);
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener l) {
        onDateSelectedListener = l;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }
}