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

public class HouseDeleteDialog extends AbsDialog {

    private OnDeleteHouseConfirmListener onDeleteHouseConfirmListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_confirm)
                .setMessage(R.string.message_house_delete)
                .setPositiveButton(R.string.action_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onDeleteHouseConfirmListener != null) {
                            onDeleteHouseConfirmListener.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onDeleteHouseConfirmListener != null) {
                            onDeleteHouseConfirmListener.confirm();
                        }
                    }
                });
        return builder.create();
    }


    public void setOnDeleteHouseConfirmListener(OnDeleteHouseConfirmListener l) {
        onDeleteHouseConfirmListener = l;
    }

    public interface OnDeleteHouseConfirmListener {
        void confirm();
        void cancel();
    }
}