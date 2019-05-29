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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.presenter.SymptomListDialogPresenter;
import com.gruposca.sapev.so.activity.AbsActivity;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.SymptomListAdapter;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;

import java.util.List;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SymptomsListDialog extends AbsDialog implements SymptomListDialogPresenter.View {

    public static final String PARAM_SYMPTOMS_DETAIL = "PARAM_SYMPTOMS_DETAIL";

    @Inject
    protected SymptomListDialogPresenter presenter;

    @InjectView(R.id.progress)
    protected View progress;
    @InjectView(R.id.empty)
    protected View empty;
    @InjectView(R.id.error)
    protected TextView error;
    @InjectView(R.id.list_symptoms)
    protected RecyclerView list;

    private RecyclerView.LayoutManager layoutManager;
    private SymptomListAdapter adapter;
    private OnSymptomListListener onSymptomListListener;
    private Boolean detailFeverish = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String visitUuid = getArguments().getString(Params.VISIT_UUID);
        final String personUuid = getArguments().getString(Params.PERSON_UUID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_feverish_symptoms, null);
        ButterKnife.inject(this, view);
        presenter.setView(this);
        presenter.getSymptomList(visitUuid, personUuid);
        layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);

        if (!personUuid.equals("")) {
            detailFeverish = true;
        }

        builder.setTitle(R.string.symptom_list_title)
                .setView(view)

                .setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        List<Symptom> listSymptoms = adapter.getItems();
                        if (onSymptomListListener != null) {
                            /*if (detailFeverish) {
                                onSymptomListListener.onUpdateSymptoms(personUuid, listSymptoms);
                            } else {
                                onSymptomListListener.onSaveSymptoms(listSymptoms);
                            }*/
                            onSymptomListListener.onSaveSymptoms(listSymptoms);
                        }
                    }
                })

                .setNegativeButton(detailFeverish ? R.string.action_cancel : R.string.action_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (detailFeverish) {
                            dismiss();
                        } else {
                            if (onSymptomListListener != null)
                                onSymptomListListener.onGoBack();
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        AbsActivity absActivity = (AbsActivity) activity;
        absActivity.inject(this);
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public void showErrorLoad(String module, String method, String errorLog) {
        progress.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        Logs.log(Logs.ERROR, module, method, errorLog);
    }

    @Override
    public void showSymptoms(List<Symptom> symptoms) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if (symptoms.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new SymptomListAdapter(getActivity());
                list.setAdapter(adapter);
            }
            adapter.animateTo(symptoms);
        }
    }

    public void setOnSymptomListListener(OnSymptomListListener l) {
        onSymptomListListener = l;
    }

    public interface OnSymptomListListener {
        void onGoBack();

        void onSaveSymptoms(List<Symptom> listSymptoms);

        //void onUpdateSymptoms(String personUuid, List<Symptom> listSymptoms);
    }
}