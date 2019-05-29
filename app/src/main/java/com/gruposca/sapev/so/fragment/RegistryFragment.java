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

package com.gruposca.sapev.so.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.IntegerList;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Registry;
import com.gruposca.sapev.presenter.RegistryPresenter;
import com.gruposca.sapev.so.component.MultiElementSpinner;
import com.gruposca.sapev.so.dialog.CubeDimensionDialog;
import com.gruposca.sapev.so.dialog.CylinderDimensionDialog;
import com.gruposca.sapev.so.dialog.InventorySummaryDialog;
import com.gruposca.sapev.so.dialog.UnitsSetToOneDialog;
import com.gruposca.sapev.so.dialog.VolumeLitersDialog;
import com.gruposca.sapev.so.dialog.VolumeTypeDialog;
import com.gruposca.sapev.tool.Params;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegistryFragment extends AbsFragment implements RegistryPresenter.View {

    @Inject
    RegistryPresenter presenter;

    @InjectView(R.id.focus_content) protected View focusContent;
    @InjectView(R.id.treated_content) protected View treatedContent;
    @InjectView(R.id.comment_content) protected View commentContent;
    @InjectView(R.id.comment) protected AutoCompleteTextView comment;
    @InjectView(R.id.states) protected MultiElementSpinner states;
    @InjectView(R.id.sample) protected TextView sample;
    @InjectView(R.id.packets_title) protected TextView packetsTitle;
    @InjectView(R.id.packets) protected EditText packets;
    @InjectView(R.id.packets_calculate) protected Button packetsCalculate;
    @InjectView(R.id.units) protected EditText units;
    @InjectView(R.id.units_less) protected View unitsLess;
    @InjectView(R.id.units_more) protected View unitsMore;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_registry;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.initialize(this, getArguments().getString(Params.VISIT_UUID), getArguments().getInt(Params.CONTAINER_ID));
        presenter.load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                keyboardHide(packets);
                presenter.confirmSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(R.id.comment)
    public void onCommentChanged(CharSequence text) {
        presenter.setComment(text.toString());
    }

    @OnCheckedChanged(R.id.focus_no)
    public void onFocusNoChecked(boolean checked) {
        if (checked) {
            presenter.setFocus(false);
            collapse(focusContent);
            keyboardHide(packets);
        }
    }

    @OnCheckedChanged(R.id.focus_yes)
    public void onFocusYesChecked(boolean checked) {
        if (checked) {
            presenter.setFocus(true);
            expand(focusContent);
            keyboardHide(packets);
        }
    }

    @OnCheckedChanged(R.id.treated_no)
    public void onTreatedNoChecked(boolean checked) {
        if (checked) {
            presenter.setTreated(false);
            collapse(treatedContent);
            keyboardHide(packets);
        }
    }

    @OnCheckedChanged(R.id.treated_yes)
    public void onTreatedYesChecked(boolean checked) {
        if (checked) {
            presenter.setTreated(true);
            expand(treatedContent);
            keyboardShow(packets);
        }
    }

    @OnTextChanged(R.id.packets)
    public void onPacketsChanged(CharSequence text) {
        Integer value = 0;
        if (!TextUtils.isEmpty(text)) {
            value = Integer.parseInt(text.toString());
        }
        presenter.setPackets(value);
    }

    @OnCheckedChanged(R.id.destroyed_no)
    public void onDestroyedNoChecked(boolean checked) {
        if (checked) {
            presenter.setDestroyed(false);
            keyboardHide(packets);
        }
    }

    @OnCheckedChanged(R.id.destroyed_yes)
    public void onDestroyedYesChecked(boolean checked) {
        if (checked) {
            presenter.setDestroyed(true);
            keyboardHide(packets);
        }
    }

    public void expand(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    public void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();
        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                }else{
                    view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    @OnClick(R.id.packets_calculate)
    public void onCalculateClicked() {
        keyboardHide(packets);
        showSelectPaketsCalculate();
        //showVolumeType();
    }

    @OnClick(R.id.units_less)
    public void onUnitsLessClicked() {
        Integer value = 1;
        if (!TextUtils.isEmpty(units.getText())) {
            value = Integer.parseInt(units.getText().toString());
        }
        if (value > 1) {
            value--;
            units.setText(String.format(Locale.getDefault(), "%d", value));
        }
    }

    @OnTextChanged(R.id.units)
    public void onUnitsChanged(CharSequence text) {
        Integer value = 0;
        if (!TextUtils.isEmpty(text)) {
            value = Integer.parseInt(text.toString());
        }
        presenter.setUnits(value);
    }

    @OnClick(R.id.units_more)
    public void onUnitsMoreClicked() {
        Integer value = 0;
        if (!TextUtils.isEmpty(units.getText())) {
            value = Integer.parseInt(units.getText().toString());
        }
        if (value < 999) {
            value++;
            units.setText(String.format(Locale.getDefault(), "%d", value));
        }
    }

    private void showSelectPaketsCalculate() {
        String[] labels = new String[2];
        labels[0] = getResources().getString(R.string.dialog_pakets_calculate_liters);
        labels[1] = getResources().getString(R.string.dialog_pakets_calculate_dimensions);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_pakets_calculate_title));
        builder.setItems(labels,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                switch (which) {
                    case (0) : showVolumeLiters(); break;
                    case (1) : showVolumeType(); break;
                    default : break;
                }
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void showVolumeLiters() {
        VolumeLitersDialog dialog = new VolumeLitersDialog();
        dialog.setOnVolumeLitersSelectedListener(new VolumeLitersDialog.OnVolumeLitersSelectedListener() {
            @Override
            public void onCanceled() {
                showSelectPaketsCalculate();
            }

            @Override
            public void onCalculate(Integer liters) {
                Integer number = presenter.calculatePackets(liters);
                packets.setText(String.format(Locale.getDefault(), "%d", number));
                keyboardHideDelay(packets);

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }


    private void showVolumeType() {
        VolumeTypeDialog dialog = new VolumeTypeDialog();
        dialog.setOnVolumeTypeSelectedListener(new VolumeTypeDialog.OnVolumeTypeSelectedListener() {
            @Override
            public void onCubeSelected() {
                showCubeDimensions();
            }

            @Override
            public void onCylinderSelected() {
                showCylinderDimensions();
            }

            @Override
            public void onCanceled() {
                showSelectPaketsCalculate();
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    private void showCubeDimensions() {
        CubeDimensionDialog dialog = new CubeDimensionDialog();
        dialog.setOnCubeDimensionsSelectedListener(new CubeDimensionDialog.OnCubeDimensionsSelectedListener() {
            @Override
            public void onCubeDimensionsSelected(Integer depth, Integer width, Integer height) {
                Integer number = presenter.calculatePackets(depth, width, height);
                packets.setText(String.format(Locale.getDefault(), "%d", number));
                keyboardHideDelay(packets);
            }

            @Override
            public void onCubeDimensionsCanceled() {
                showVolumeType();
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    private void showCylinderDimensions() {
        CylinderDimensionDialog dialog = new CylinderDimensionDialog();
        dialog.setOnCylinderDimensionsSelectedListener(new CylinderDimensionDialog.OnCylinderDimensionsSelectedListener() {
            @Override
            public void onCylinderDimensionsSelected(Integer diameter, Integer height) {
                Integer number = presenter.calculatePackets(diameter, height);
                packets.setText(String.format(Locale.getDefault(), "%d", number));
                keyboardHideDelay(packets);
            }

            @Override
            public void onCylinderDimensionsCanceled() {
                showVolumeType();
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void onStatesLoaded(List<Element> values) {
        states.setItems(values);
        states.setOnSelectionChangeListener(new MultiElementSpinner.OnSelectionChangedListener() {
            @Override
            public void onSelectedElementsChanged(IntegerList ids) {
                presenter.setStates(ids);
            }
        });
    }

    @Override
    public void onPlanLoaded(Plan plan) {
        packetsTitle.setText(String.format(Locale.getDefault(), "Número de %1$s (de %2$.1f %3$s de %4$s)", plan.larvicideDoseName, plan.larvicideDose, plan.larvicideUnity, plan.larvicideName));
        packetsCalculate.setText(String.format(Locale.getDefault(), "Calcular %1$s", plan.larvicideDoseName));
    }

    @Override
    public void updateView(Registry registry) {
        commentContent.setVisibility(registry.container.id == 4010 ? View.VISIBLE : View.GONE);
        sample.setText(registry.sample);
        packets.setText(registry.packets == 0 ? "" : registry.packets.toString());
        if (registry.focus || registry.treated) {
            units.setEnabled(false);
            unitsLess.setEnabled(false);
            unitsMore.setEnabled(false);
        } else {
            units.setEnabled(true);
            unitsLess.setEnabled(true);
            unitsMore.setEnabled(true);
        }
        units.setText(String.format(Locale.getDefault(), "%d", registry.units));
        states.setSelection(registry.states);
    }

    @OnClick(R.id.action_save)
    public void onSaveClick() {
        keyboardHide(packets);
        presenter.confirmSave();
    }

    @Override
    public void confirmSave(Registry registry, Plan plan) {
        InventorySummaryDialog dialog = new InventorySummaryDialog();
        dialog.setOnConfirmListener(new InventorySummaryDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                presenter.save();
            }

            @Override
            public void onGoBack() {
            }
        });
        Bundle bundle = new Bundle();
        if (registry.focus)
            bundle.putString(InventorySummaryDialog.PARAM_SAMPLE, registry.sample);
        if (registry.treated)
            bundle.putString(InventorySummaryDialog.PARAM_PACKETS, String.format(Locale.getDefault(), "%1$d %2$s", registry.packets, plan.larvicideDoseName));
        bundle.putBoolean(InventorySummaryDialog.PARAM_DESTROYED, registry.destroyed);
        bundle.putInt(InventorySummaryDialog.PARAM_UNITS, registry.units);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void showSnackbar(int resId) {
        if (getView() != null) {
            Snackbar.make(getView(), resId, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void populateCommentAutocomplete() {
        List<String> listComments = Arrays.asList(getResources().getStringArray(R.array.others));
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_comment, listComments);
        comment.setAdapter(adapter);
        comment.setThreshold(1);
    }

    @Override
    public void close() {
        Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Override
    public void showUnitsDialog(Integer number) {
        UnitsSetToOneDialog dialog = new UnitsSetToOneDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(UnitsSetToOneDialog.PARAM_NUMBER, number);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), dialog.getTag());
    }
}