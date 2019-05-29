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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.presenter.InspectionSummaryPresenter;
import com.gruposca.sapev.so.activity.di.InspectionSummaryActivityModule;
import com.gruposca.sapev.so.dialog.QRNotValidDialog;
import com.gruposca.sapev.tool.Params;
import com.gruposca.sapev.tool.QRs;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class InspectionSummaryActivity extends AbsActivity implements InspectionSummaryPresenter.View {

    private static final int REQUEST_QR_SCANNER = 2;

    @Inject InspectionSummaryPresenter presenter;

    @InjectView(R.id.collapsing_toolbar) protected CollapsingToolbarLayout collapsingToolbarLayout;
    @InjectView(R.id.inspected) protected TextView inspected;
    @InjectView(R.id.focus) protected TextView focus;
    @InjectView(R.id.treated) protected TextView treated;
    @InjectView(R.id.destroyed) protected TextView destroyed;
    @InjectView(R.id.samples) protected TextView samples;
    @InjectView(R.id.feverish) protected TextView feverish;
    @InjectView(R.id.larvicide) protected TextView larvicide;
    @InjectView(R.id.comments) protected EditText comments;

    @Override
    protected Object[] getInjectionModules() {
        return new Object[] {
                new InspectionSummaryActivityModule()
        };
    }

    @Override
    protected int onActivityRequestLayout() {
        return R.layout.activity_inspection_summary;
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        presenter.initialize(this, getIntent().getStringExtra(Params.VISIT_UUID));
        presenter.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inspection_summary, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_camera:
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, REQUEST_QR_SCANNER);
                } catch (Exception e) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android"));
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initToolbar() {
        collapsingToolbarLayout.setTitle(toolbar.getTitle());
    }


    @Override
    public void showSummary(Visit visit, Plan plan) {
        inspected.setText(getString(visit.inspected));
        focus.setText(getString(visit.focus));
        treated.setText(getString(visit.treated));
        destroyed.setText(getString(visit.destroyed));
        samples.setText(getString(visit.samples));
        feverish.setText(getString(visit.feverish));
        larvicide.setText(getString(R.string.weight_grams, visit.larvicide, plan.larvicideUnity));
    }

    private String getString(Integer value) {
        return String.format(Locale.getDefault(), "%d", value);
    }

    @Override
    public void visitSaved() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.action_done)
    public void onNextClick(View view) {
        presenter.save(comments.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QR_SCANNER:
                if (resultCode == Activity.RESULT_OK) {
                    String qr = data.getStringExtra("SCAN_RESULT");
                    if (QRs.validate(qr)) {
                        presenter.saveQrCode(qr);
                    } else {
                        QRNotValidDialog dialog = new QRNotValidDialog();
                        dialog.show(getFragmentManager(), dialog.getTag());
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}