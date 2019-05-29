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
import android.widget.Button;
import android.widget.EditText;

import com.gruposca.sapev.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

public class UnlockAreaDialog extends AbsDialog {

    private UnlockAreaDialog.OnUnlockConfirmListener onSyncConfirmListener;
    private Integer code;

    @InjectView(R.id.pin)
    protected EditText pin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_unlock_area, null);
        ButterKnife.inject(this, view);
        builder.setTitle(R.string.action_unlock_area)
                .setView(view)
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.action_unlock, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onSyncConfirmListener != null) {
                            onSyncConfirmListener.onConfirm(code);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pin, 0);
            }
        }, 250);
    }

    @OnTextChanged(R.id.pin)
    public void onPinChanged(CharSequence text) {
        code = TextUtils.isEmpty(text) ? null : Integer.parseInt(text.toString());
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(code != null && code >= 1000);
    }

    public void setOnUnlockConfirmListener(UnlockAreaDialog.OnUnlockConfirmListener l) {
        onSyncConfirmListener = l;
    }

    public interface OnUnlockConfirmListener {
        void onConfirm(Integer pin);
    }
}