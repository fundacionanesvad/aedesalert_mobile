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

import com.gruposca.sapev.R;

public class UnitsSetToOneDialog extends AbsDialog {

    public static final String PARAM_NUMBER = "PARAM_NUMBER";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int number = getArguments().getInt(PARAM_NUMBER);
        String message;
        if (number == 1)
            message = getString(R.string.message_units_set_to_one);
        else
            message = getString(R.string.message_units_set_to_one_multiple, number);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.units_set_to_one)
                .setMessage(message)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}