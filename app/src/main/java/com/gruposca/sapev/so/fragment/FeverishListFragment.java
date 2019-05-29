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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.presenter.FeverishListPresenter;
import com.gruposca.sapev.presenter.PersonDetailPresenter;
import com.gruposca.sapev.presenter.PersonListPresenter;
import com.gruposca.sapev.presenter.VisitPresenter;
import com.gruposca.sapev.so.adapter.AbsAdapter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.PersonListAdapter;
import com.gruposca.sapev.so.dialog.FeverishPersonListDialog;
import com.gruposca.sapev.so.dialog.FeverishRecommendationsDialog;
import com.gruposca.sapev.so.dialog.HouseDeleteDialog;
import com.gruposca.sapev.so.dialog.PersonDetailDialog;
import com.gruposca.sapev.so.dialog.SymptomsListDialog;
import com.gruposca.sapev.so.dialog.VisitDeleteDialog;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;
import java.util.List;
import javax.inject.Inject;
import butterknife.InjectView;

public class FeverishListFragment extends AbsFragment implements FeverishListPresenter.View, AbsAdapter.OnItemClickListener {

    @Inject
    protected FeverishListPresenter presenter;
    @Inject
    protected PersonListPresenter personListPresenter;
    @Inject
    protected PersonDetailPresenter personDetailPresenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.empty_feverish_list) protected View empty;
    @InjectView(R.id.error_feverish_list) protected TextView error;
    @InjectView(R.id.list_feverish) protected RecyclerView list;

    private PersonListAdapter adapter;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_inspection_feverish_list;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.feverish_list_title;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.initialize(this, getArguments().getString(Params.VISIT_UUID));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        presenter.load();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feverish_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feverish_add:
                showFeverishPersons();
                break;
            case R.id.action_delete_visit:
                deleteVisit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view) {
        int position = list.getChildAdapterPosition(view);
        Person feverish = adapter.getItem(position);
        showSymptoms(feverish.uuid);
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
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public void showFeverish(List<Person> feverishPersons) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if (feverishPersons.size() == 0) {
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
            adapter.animateTo(feverishPersons);
        }
    }

    @Override
    public void showSymptoms(String personUuid) {
        SymptomsListDialog dialog = new SymptomsListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Params.PERSON_UUID, personUuid);
        bundle.putString(Params.VISIT_UUID, getArguments().getString(Params.VISIT_UUID));
        dialog.setArguments(bundle);
        dialog.setOnSymptomListListener(new SymptomsListDialog.OnSymptomListListener() {

            @Override
            public void onGoBack() {
                presenter.showFeverish();
            }

            @Override
            public void onSaveSymptoms(List<Symptom> listSymptoms) {
                presenter.saveSymptoms(getArguments().getString(Params.VISIT_UUID), listSymptoms);
            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void showRecommendations() {
        FeverishRecommendationsDialog dialog = new FeverishRecommendationsDialog();
        dialog.show(getFragmentManager(), dialog.getTag());
        presenter.load();
    }

    @Override
    public void showFeverishList() {
        presenter.load();
    }

    @Override
    public void onDeleteVisit() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showFeverishPersons() {
        FeverishPersonListDialog dialog = new FeverishPersonListDialog();
        dialog.setOnFeverishPersonListener(new FeverishPersonListDialog.OnFeverishPersonListener() {
            @Override
            public void goToSymptoms(String uuid) {
                presenter.setPerson(uuid);
            }
        });
        dialog.setOnNewFeverishListener(new FeverishPersonListDialog.OnNewFeverishListener() {
            @Override
            public void onConfirm() {
                showNewPersonFeverish();
            }

            @Override
            public void onCancel() {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(Params.HOUSE_UUID, getArguments().getString(Params.HOUSE_UUID));
        bundle.putString(Params.VISIT_UUID, getArguments().getString(Params.VISIT_UUID));
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    private void showNewPersonFeverish() {
        PersonDetailDialog dialog = new PersonDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Params.PERSON_UUID, "");
        bundle.putString(Params.HOUSE_UUID, getArguments().getString(Params.HOUSE_UUID));
        dialog.setArguments(bundle);
        dialog.setOnPersonDetailListener(new PersonDetailDialog.OnPersonDetailListener() {
            @Override
            public void onSaved() {
                personListPresenter.load();
                showSymptoms(personDetailPresenter.getPersonUuid());
            }

            @Override
            public void onCanceled() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
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