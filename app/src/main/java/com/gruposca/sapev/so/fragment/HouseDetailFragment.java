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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.presenter.HouseDetailPresenter;
import com.gruposca.sapev.service.LocationService;
import com.gruposca.sapev.so.activity.InspectionActivity;
import com.gruposca.sapev.so.activity.VisitActivity;
import com.gruposca.sapev.so.dialog.HouseDeleteDialog;
import com.gruposca.sapev.so.dialog.QRNotValidDialog;
import com.gruposca.sapev.so.dialog.SaveChangesDialog;
import com.gruposca.sapev.so.dialog.VisitConfirmDialog;
import com.gruposca.sapev.so.dialog.VisitConvertConfirmDialog;
import com.gruposca.sapev.tool.Params;
import com.gruposca.sapev.tool.QRs;

import java.text.Format;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class HouseDetailFragment extends AbsFragment implements HouseDetailPresenter.View, FloatingActionMenu.OnMenuToggleListener {

    private static final int REQUEST_QR_SCANNER = 1;
    private static final int REQUEST_INSPECTION = 2;
    private static final int REQUEST_VISIT = 3;

    @Inject
    HouseDetailPresenter presenter;

    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.content) protected View content;
    @InjectView(R.id.last_visit) protected CardView lastVisit;
    @InjectView(R.id.qr_code) protected TextView qrCode;
    @InjectView(R.id.area_code) protected TextView areaCode;
    @InjectView(R.id.street_name) protected AutoCompleteTextView streetName;
    @InjectView(R.id.street_number) protected EditText streetNumber;
    @InjectView(R.id.visit_title) protected TextView visitTitle;
    @InjectView(R.id.visit_last_date) protected TextView visitLastDate;
    @InjectView(R.id.visit_last_result) protected TextView visitLastResult;
    @InjectView(R.id.button_add) protected FloatingActionMenu buttonAdd;
    @InjectView(R.id.button_convert) protected FloatingActionMenu buttonConvert;
    @InjectView(R.id.edit) protected View edit;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_house_detail;
    }

    @Override
    protected void onFragmentViewCreated(View view, Bundle savedInstanceState) {


        setHasOptionsMenu(true);
        presenter.initialize(this, getArguments().getString(Params.HOUSE_UUID), getArguments().getInt(Params.AREA_ID));
        presenter.load(savedInstanceState);
        buttonAdd.setClosedOnTouchOutside(true);
        buttonAdd.setOnMenuToggleListener(this);
        buttonAdd.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu(buttonAdd);
            }
        });
        buttonConvert.setClosedOnTouchOutside(true);
        buttonConvert.setOnMenuToggleListener(this);
        buttonConvert.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu(buttonConvert);
            }
        });



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_house_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.action_save);
        saveItem.setVisible(presenter.isValid() && presenter.hasChanges());
        MenuItem qrItem = menu.findItem(R.id.action_camera);
        qrItem.setIcon(presenter.hasQr() ? R.drawable.action_qr_ok : R.drawable.action_qr);
        MenuItem editVisitItem = menu.findItem(R.id.action_edit_visit);
        editVisitItem.setVisible(presenter.hasVisit());
        MenuItem deleteItem =  menu.findItem(R.id.action_delete_house);
        deleteItem.setVisible(presenter.isNew());
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                keyboardHide(streetName);
                getActivity().setResult(Activity.RESULT_OK);
                presenter.saveHouse(false);
                break;

            case R.id.action_edit_visit:
                presenter.editVisit();
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

            case R.id.action_delete_house:
                deleteHouse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QR_SCANNER:
                if (resultCode == Activity.RESULT_OK) {
                    String qr = data.getStringExtra("SCAN_RESULT");
                    if (QRs.validate(qr)) {
                        qrCode.setText(qr);
                        presenter.setQrCode(qr);
                    } else {
                        QRNotValidDialog dialog = new QRNotValidDialog();
                        dialog.show(getFragmentManager(), dialog.getTag());
                    }
                }
                break;

            case REQUEST_VISIT:
            case REQUEST_INSPECTION:
                if (resultCode == Activity.RESULT_OK) {
                    onHouseSaved();
                } else {
                    presenter.load(null);
                    streetNumber.requestFocus();
                }
                break;
        }
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
    }

    @Override
    public void populateStreetAutocomplete(List<String> streets) {
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_street, streets);
        streetName.setAdapter(adapter);
        streetName.setThreshold(1);
        streetName.clearFocus();
        content.requestFocus();
    }

    @Override
    public void showHouse(House house) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        areaCode.setText(house.getUbigeo());
        if (house.qrCode == null)
            qrCode.setText(R.string.action_no);
        else
            qrCode.setText(house.qrCode);
        streetName.setText(house.streetName);
        streetNumber.setText(house.streetNumber);
        if (house.latitude == null) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getActivity(), LocationService.class);
                intent.putExtra(LocationService.PARAM_HOUSE_UUID, house.uuid);
                getActivity().startService(intent);
            }
        }
        getActivity().invalidateOptionsMenu();
        streetName.clearFocus();
        content.requestFocus();
    }

    @Override
    public void showVisit(House house) {
        if (house.lastVisitDate == null) {
            lastVisit.setVisibility(View.GONE);
        } else {
            lastVisit.setVisibility(View.VISIBLE);
            Format dateFormat = DateFormat.getDateFormat(getActivity());
            visitLastDate.setText(dateFormat.format(house.lastVisitDate.getTime()));
            visitLastResult.setText(house.lastVisitResult.name);
            edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void showVisit(Visit visit) {
        Format dateFormat = DateFormat.getDateFormat(getActivity());
        visitLastDate.setText(dateFormat.format(visit.date.getTime()));
        visitLastResult.setText(visit.result.name);
        edit.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVisitButton(Boolean visible, Boolean visited) {
        if (visited) {
            buttonAdd.setVisibility(View.GONE);
            buttonConvert.setVisibility(visible ? View.GONE : View.GONE);
        } else {
            buttonAdd.setVisibility(visible ? View.VISIBLE : View.GONE);
            buttonConvert.setVisibility(View.GONE);
        }
    }

    @Override
    public void showButtons(Boolean reconversion,String planTypeName) {

        if(planTypeName.compareTo("Vigilancia")==1) {
            getView().findViewById(R.id.action_deserted).setVisibility(View.GONE);
            getView().findViewById(R.id.action_closed).setVisibility(View.GONE);
            getView().findViewById(R.id.action_reluctant).setVisibility(View.GONE);
        }else{
            getView().findViewById(R.id.action_inspection).setVisibility(reconversion ? View.GONE : View.VISIBLE);
            getView().findViewById(R.id.action_deserted).setVisibility(reconversion ? View.GONE : View.VISIBLE);
            getView().findViewById(R.id.action_closed).setVisibility(reconversion ? View.GONE : View.VISIBLE);
            getView().findViewById(R.id.action_reluctant).setVisibility(reconversion ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void invalidateOptionMenu() {
        getActivity().invalidateOptionsMenu();
    }

    @OnTextChanged(R.id.street_name)
    public void onStreetNameChanged(CharSequence text) {
        presenter.setStreetName(text.toString().trim());
    }

    @OnTextChanged(R.id.street_number)
    public void onStreetNumberChanged(CharSequence text) {
        presenter.setStreetNumber(text.toString().trim());
    }

    @OnClick(R.id.action_deserted)
    public void onActionDeserted() {
        createVisit(Visit.RESULT_DESERTED);
    }

    @OnClick(R.id.action_closed)
    public void onClosedClick() {
        createVisit(Visit.RESULT_CLOSED);
    }

    @OnClick(R.id.action_reluctant)
    public void onReluctantClick() {
        createVisit(Visit.RESULT_RELUCTANT);
    }

    @OnClick(R.id.action_inspection)
    public void onInspectionClick() {
        createVisit(Visit.RESULT_INSPECTION);
    }

    private void createVisit(int result) {

        buttonAdd.close(false);
        getActivity().setResult(Activity.RESULT_OK);
        presenter.confirmVisit(result);
    }

    @OnClick(R.id.action_convert_deserted)
    public void onActionConvertDeserted() {
        convertVisit(Visit.RESULT_DESERTED);
    }

    @OnClick(R.id.action_convert_closed)
    public void onClosedConvertClick() {
        convertVisit(Visit.RESULT_CLOSED);
    }

    @OnClick(R.id.action_convert_reluctant)
    public void onReluctantConvertClick() {
        convertVisit(Visit.RESULT_RELUCTANT);
    }

    @OnClick(R.id.action_convert_inspection)
    public void onInspectionConvertClick() {
        convertVisit(Visit.RESULT_INSPECTION);
    }

    private void convertVisit(final int result) {
        buttonConvert.close(false);
        getActivity().setResult(Activity.RESULT_OK);
        VisitConvertConfirmDialog dialog = new VisitConvertConfirmDialog();
        dialog.setOnVisitConvertConfirmListener(new VisitConvertConfirmDialog.OnVisitConvertConfirmListener() {
            @Override
            public void onConfirm() {
                presenter.convertVisit(result);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void onCreateVisit(String houseUuid, int result) {
        if (result == Visit.RESULT_INSPECTION) {
            Intent intent = new Intent(getActivity(), InspectionActivity.class);
            intent.putExtra(Params.HOUSE_UUID, houseUuid);
            startActivityForResult(intent, REQUEST_INSPECTION);
        } else {
            Intent intent = new Intent(getActivity(), VisitActivity.class);
            intent.putExtra(Params.HOUSE_UUID, houseUuid);
            intent.putExtra(Params.VISIT_RESULT_ID, result);
            startActivityForResult(intent, REQUEST_VISIT);
        }
    }

    @Override
    public void confirmVisit(final int result) {
        VisitConfirmDialog dialog = new VisitConfirmDialog();
        dialog.setOnVisitConfirmListener(new VisitConfirmDialog.OnVisitConfirmListener() {
            @Override
            public void onConfirm() {
                presenter.createVisit(result);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void onHouseSaved() {
        Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Override
    public void confirmChanges() {

        trace("confirmChanges");
        if (buttonAdd.isOpened()) {
            toggleMenu(buttonAdd);
        } else if (buttonConvert.isOpened()) {
            toggleMenu(buttonConvert);
        } else if (presenter.hasChanges()) {
            SaveChangesDialog dialog = new SaveChangesDialog();
            dialog.setOnSaveChangesListener(new SaveChangesDialog.OnSaveChangesListener() {
                @Override
                public void onSave() {
                    presenter.saveHouse(true);
                }

                @Override
                public void onNo() {
                    getActivity().finish();
                }

                @Override
                public void onCancel() {

                }
            });
            dialog.show(getFragmentManager(), dialog.getTag());
        } else {
            getActivity().finish();
        }
    }

    private void toggleMenu(FloatingActionMenu menu) {
        Boolean opened = menu.isOpened();
        if (!opened) {
            keyboardHide(streetName);
        }
        menu.toggle(!opened);
    }

    @Override
    public void onMenuToggle(boolean opened) {

        getView().clearFocus();

    }

    @Override
    public void validate() {
        streetName.setError(null);
        streetNumber.setError(null);
        if (TextUtils.isEmpty(streetNumber.getText())) {
            streetNumber.setError(getString(R.string.value_required));
            keyboardShow(streetNumber);
        }
        if (TextUtils.isEmpty(streetName.getText())) {
            streetName.setError(getString(R.string.value_required));
            keyboardShow(streetName);
        }
    }

    @OnClick(R.id.edit)
    public void onEditClick() {
        presenter.editVisit();
    }

    private void deleteHouse (){
        HouseDeleteDialog dialog = new HouseDeleteDialog();
        dialog.setOnDeleteHouseConfirmListener(new HouseDeleteDialog.OnDeleteHouseConfirmListener() {
            @Override
            public void confirm() {
                presenter.deleteHouse();
            }
            @Override
            public void cancel() {

            }
        });
        dialog.show(getFragmentManager(), dialog.getTag());
    }

    @Override
    public void onDeleteHouse() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}