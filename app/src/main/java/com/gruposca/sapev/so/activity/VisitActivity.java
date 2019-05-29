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

package com.gruposca.sapev.so.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.presenter.VisitPresenter;
import com.gruposca.sapev.so.activity.di.VisitActivityModule;
import com.gruposca.sapev.so.dialog.VisitDeleteDialog;
import com.gruposca.sapev.tool.Params;
import javax.inject.Inject;
import butterknife.InjectView;
import butterknife.OnClick;

public class VisitActivity extends AbsActivity implements VisitPresenter.View {

    @Inject VisitPresenter presenter;
    @InjectView(R.id.action_done) protected FloatingActionButton actionDone;
    @InjectView(R.id.collapsing_toolbar) protected CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_visit;
    }

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new VisitActivityModule()
        };
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
       presenter.initialize(this, getIntent().getStringExtra(Params.HOUSE_UUID), getIntent().getIntExtra(Params.VISIT_RESULT_ID, 0));
        presenter.load(savedInstanceState);
    }

    @Override
    protected void onActivitySaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visit_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete_visit:
                deleteVisit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.action_done)
    public void onDoneClick(View view) {
        presenter.saveVisit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            presenter.cancelVisit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initToolbar(Visit visit) {
        collapsingToolbarLayout.setTitle(visit.result.name);
    }

    @Override
    public void visitSaved() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void visitCanceled() {
        finish();
    }

    @Override
    public void onDeleteVisit() {
        setResult(Activity.RESULT_OK);
        finish();
    }


    private void deleteVisit (){
        VisitDeleteDialog dialog = new VisitDeleteDialog();
        dialog.setOnDeleteVisitConfirmListener(new VisitDeleteDialog.OnDeleteVisitConfirmListener() {
            @Override
            public void confirm() {
                presenter.deleteVisit();
            }

            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }
}