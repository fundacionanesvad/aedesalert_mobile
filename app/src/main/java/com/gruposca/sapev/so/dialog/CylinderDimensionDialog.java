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
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.so.component.VolumeCylinder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

public class CylinderDimensionDialog extends AbsDialog {

    private static final int STEP_DIAMETER = 1;
    private static final int STEP_HEIGHT = 2;

    private OnCylinderDimensionsSelectedListener onCylinderDimensionsSelectedListener;
    private Integer step;
    private Integer diameter;
    private Integer height;

    @InjectView(R.id.value)
    protected EditText value;
    @InjectView(R.id.cylinder)
    protected VolumeCylinder cylinder;
    @InjectView(R.id.text)
    protected TextView text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        step = STEP_DIAMETER;
        diameter = null;
        height = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cylinder_dimensions, null);
        ButterKnife.inject(this, view);
        builder.setTitle(R.string.volume_dimensions)
                .setView(view)
                .setPositiveButton(R.string.action_next, null)
                .setNegativeButton(R.string.action_back, null);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextStep(dialog);
            }
        });
        Button negativeButton = (Button) dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(dialog);
            }
        });
        showStep();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(value, 0);
            }
        }, 500);
    }

    private void onNextStep(AlertDialog dialog) {
        if (step == STEP_HEIGHT) {
            if (onCylinderDimensionsSelectedListener != null)
                onCylinderDimensionsSelectedListener.onCylinderDimensionsSelected(diameter, height);
            dialog.dismiss();
        } else {
            step++;
            showStep();
        }
    }

    private void onCancel(AlertDialog dialog) {
        if (step == STEP_DIAMETER) {
            if (onCylinderDimensionsSelectedListener != null)
                onCylinderDimensionsSelectedListener.onCylinderDimensionsCanceled();
            dialog.cancel();
        } else {
            step--;
            showStep();
        }
    }

    @OnTextChanged(R.id.value)
    public void onValueChanged(CharSequence text) {
        Integer value = TextUtils.isEmpty(text) ? null : Integer.parseInt(text.toString());
        switch (step) {
            case STEP_DIAMETER:
                diameter = value;
                break;
            case STEP_HEIGHT:
                height = value;
                break;
        }
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(value != null);
    }

    @OnEditorAction(R.id.value)
    public boolean passwordActionDone(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (!TextUtils.isEmpty(value.getText())) {
                onNextStep((AlertDialog) getDialog());
            }
        }
        return true;
    }

    private void showStep() {
        switch (step) {
            case STEP_DIAMETER:
                initViews(R.string.diameter, diameter, VolumeCylinder.EDGE_DIAMETER);
                break;
            case STEP_HEIGHT:
                initViews(R.string.water_height, height, VolumeCylinder.EDGE_HEIGHT);
                break;
        }
    }

    private void initViews(int textResId, Integer value, int edge) {
        this.text.setText(textResId);
        this.value.setText(value == null ? "" : value.toString());
        this.cylinder.setCylinderEdge(edge);
        final Button button = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(value != null);
        button.setText(step == STEP_HEIGHT ? R.string.action_calculate : R.string.action_next);
    }

    public void setOnCylinderDimensionsSelectedListener(OnCylinderDimensionsSelectedListener l) {
        onCylinderDimensionsSelectedListener = l;
    }

    public interface OnCylinderDimensionsSelectedListener {
        void onCylinderDimensionsSelected(Integer diameter, Integer height);
        void onCylinderDimensionsCanceled();
    }
}
