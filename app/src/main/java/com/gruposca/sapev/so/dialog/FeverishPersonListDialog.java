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
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.presenter.PersonListDialogPresenter;
import com.gruposca.sapev.so.activity.AbsActivity;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.PersonListAdapter;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;
import java.util.List;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeverishPersonListDialog extends AbsDialog implements PersonListDialogPresenter.View, AbsAdapter.OnItemClickListener{

    @Inject
    protected PersonListDialogPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.empty_personFeverish) protected TextView empty;
    @InjectView(R.id.error_personFeverish) protected TextView error;
    @InjectView(R.id.list_personFeverish) protected RecyclerView list;

    private PersonListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private OnFeverishPersonListener onFeverishPersonListener;
    private OnNewFeverishListener onNewFeverishListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_feverish_persons, null);
        ButterKnife.inject(this, view);
        presenter.setView(this);
        presenter.initialize(getArguments().getString(Params.HOUSE_UUID), getArguments().getString(Params.VISIT_UUID));
        layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        builder.setTitle(R.string.feverish_list_persons)
                .setView(view)
                .setPositiveButton(R.string.action_new, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onNewFeverishListener != null) {
                            onNewFeverishListener.onConfirm();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onNewFeverishListener != null) {
                            onNewFeverishListener.onCancel();
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        AbsActivity absActivity =(AbsActivity) activity;
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
    public void showError(String module, String method, String errorLog) {
        progress.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        Logs.log(Logs.ERROR, module, method, errorLog);
    }

    @Override
    public void showPersons(List<Person> persons) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if (persons.size() == 0) {

            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new PersonListAdapter(getActivity());
                adapter.setOnItemClickListener(this);
                list.setAdapter(adapter);
            }
            adapter.animateTo(persons);
        }
    }

    @Override
    public void onItemClick(View view) {
        dismiss();
        int position = list.getChildAdapterPosition(view);
        Person person = adapter.getItem(position);
        if (onFeverishPersonListener != null) {
            onFeverishPersonListener.goToSymptoms(person.uuid);
        }
    }

    public interface OnFeverishPersonListener {
        void goToSymptoms(String uuid);
    }

    public void setOnFeverishPersonListener(OnFeverishPersonListener l) {
        onFeverishPersonListener = l;
    }

    public void setOnNewFeverishListener(OnNewFeverishListener l) {
        onNewFeverishListener = l;
    }

    public interface OnNewFeverishListener {
        void onConfirm();
        void onCancel();
    }
}