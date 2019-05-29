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
import android.view.LayoutInflater;
import android.view.View;

import com.gruposca.sapev.R;
import com.gruposca.sapev.so.component.VolumeCube;
import com.gruposca.sapev.so.component.VolumeCylinder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VolumeTypeDialog extends AbsDialog {

    private OnVolumeTypeSelectedListener onVolumeTypeSelectedListener;

    @InjectView(R.id.cylinder)
    protected VolumeCylinder cylinder;
    @InjectView(R.id.cube)
    protected VolumeCube cube;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_volume_type, null);
        ButterKnife.inject(this, view);
        builder.setView(view)
                .setTitle(R.string.volume_type)
                .setNegativeButton(R.string.action_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onVolumeTypeSelectedListener != null)
                            onVolumeTypeSelectedListener.onCanceled();
                    }
                });
        return builder.create();
    }

    @OnClick(R.id.cube)
    public void onCubeClicked(View view) {
        if (onVolumeTypeSelectedListener != null)
            onVolumeTypeSelectedListener.onCubeSelected();
        dismiss();
    }

    @OnClick(R.id.cylinder)
    public void onCylinderClicked(View view) {
        if (onVolumeTypeSelectedListener != null)
            onVolumeTypeSelectedListener.onCylinderSelected();
        dismiss();
    }

    public void setOnVolumeTypeSelectedListener(OnVolumeTypeSelectedListener l) {
        onVolumeTypeSelectedListener = l;
    }

    public interface OnVolumeTypeSelectedListener {
        void onCubeSelected();
        void onCylinderSelected();
        void onCanceled();
    }
}