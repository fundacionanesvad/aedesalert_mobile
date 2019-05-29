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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.presenter.PersonListPresenter;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.PersonListAdapter;
import com.gruposca.sapev.so.dialog.DeletePersonConfirmDialog;
import com.gruposca.sapev.so.dialog.PersonDetailDialog;
import com.gruposca.sapev.so.dialog.PersonNotDeleteDialog;
import com.gruposca.sapev.so.dialog.VisitDeleteDialog;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;
import java.util.List;
import javax.inject.Inject;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PersonListFragment extends AbsFragment implements PersonListPresenter.View, AbsAdapter.OnItemClickListener, PersonListAdapter.OnItemDeleteClickListener {

    @Inject
    protected PersonListPresenter presenter;

    @InjectView(R.id.progress)
    protected View progress;
    @InjectView(R.id.empty)
    protected View empty;
    @InjectView(R.id.error)
    protected TextView error;
    @InjectView(R.id.list)
    protected RecyclerView list;
    @InjectView(R.id.personsNumber)
    protected EditText personsNumber;
    @InjectView(R.id.persons_less)
    protected View persons_less;
    @InjectView(R.id.persons_more)
    protected View persons_more;

    private PersonListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_inspection_person_list;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.person_list_title;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.initialize(this, getArguments().getString(Params.HOUSE_UUID));
        layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        presenter.load();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_person_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_person_add:
                showDetail("");
                break;
            case R.id.action_delete_visit:
                deleteVisit();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                adapter.setOnItemDeleteClickListener(this);
                list.setAdapter(adapter);
            }
            adapter.animateTo(persons, true);
        }
    }

    @Override
    public void showPersonsNumber(Integer number) {
        personsNumber.setText(number.toString());
    }

    @Override
    public void showNotDeletePersonDialog() {
        PersonNotDeleteDialog dialog = new PersonNotDeleteDialog();
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void showDeletePersonConfirmDialog(final String personUuid) {
        DeletePersonConfirmDialog dialog = new DeletePersonConfirmDialog();
        dialog.setOnDeletePersonConfirmListener(new DeletePersonConfirmDialog.OnDeletePersonConfirmListener() {
            @Override
            public void onConfirm() {
                presenter.update(personUuid);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void onDeleteVisit() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildAdapterPosition(view);
        Person person = adapter.getItem(position);
        showDetail(person.uuid);
    }

    private void showDetail(String personUuid) {
        PersonDetailDialog dialog = new PersonDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Params.PERSON_UUID, personUuid);
        bundle.putString(Params.HOUSE_UUID, getArguments().getString(Params.HOUSE_UUID));
        dialog.setArguments(bundle);
        dialog.setOnPersonDetailListener(new PersonDetailDialog.OnPersonDetailListener() {
            @Override
            public void onSaved() {
                presenter.load();
            }

            @Override
            public void onCanceled() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @OnClick(R.id.persons_less)
    public void onUnitsLessClicked() {
        Integer value = 1;
        if (!TextUtils.isEmpty(personsNumber.getText())) {
            value = Integer.parseInt(personsNumber.getText().toString());
        }
        if (value > 1) {
            value--;
            personsNumber.setText(value.toString());
        }
    }

    @OnTextChanged(R.id.personsNumber)
    public void onUnitsChanged(CharSequence text) {
        Integer value = 0;
        if (!TextUtils.isEmpty(text)) {
            value = Integer.parseInt(text.toString());
        }
        presenter.setPersonsNumber(value);
    }

    @OnClick(R.id.persons_more)
    public void onUnitsMoreClicked() {
        Integer value = 0;
        if (!TextUtils.isEmpty(personsNumber.getText())) {
            value = Integer.parseInt(personsNumber.getText().toString());
        }
        if (value < 999) {
            value++;
            personsNumber.setText(value.toString());
        }
    }

    @Override
    public void onItemDeleteClick(View view, int position) {
        Person person = adapter.getItem(position);
        presenter.checkPersonFeverish(person.uuid, getArguments().getString(Params.VISIT_UUID));
    }

    private void deleteVisit (){
        VisitDeleteDialog dialog = new VisitDeleteDialog();
        dialog.setOnDeleteVisitConfirmListener(new VisitDeleteDialog.OnDeleteVisitConfirmListener() {
            @Override
            public void confirm() {
                presenter.deleteVisit(getArguments().getString(Params.VISIT_UUID));
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }
}