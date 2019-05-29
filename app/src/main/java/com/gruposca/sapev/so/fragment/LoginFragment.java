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
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.presenter.LoginPresenter;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class LoginFragment extends AbsFragment implements LoginPresenter.View {

    public final int REQUEST_PERMISSIONS = 1;

    @Inject LoginPresenter presenter;
    @InjectView(R.id.login) protected EditText login;
    @InjectView(R.id.password) protected EditText password;
    @InjectView(R.id.info) protected TextView info;
    @InjectView(R.id.error) protected TextView error;
    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.submit) protected View submit;
    @InjectView(R.id.version) protected TextView version;
    private OnLoggedListener onLoggedListener;

    @Override
    public int getActionBarTitle() { return R.string.empty; }

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_login;
    }

    @Override
    protected View onFragmentCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeLogin);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(onFragmentRequestLayoutResourceId(), container, false);
    }

    @Override
    protected void onFragmentInitializeViews() {
        presenter.initialize(this);
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(getString(R.string.version, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_logs:
                if (hasConnection()){
                    presenter.sendLogs();
                } else {
                    showSnackbar(R.string.message_error_connection);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showPlan() {
        if (onLoggedListener != null)
            onLoggedListener.onLogged();
    }

    @Override
    public void showLogin() {
        showViews(true, false, false, false);
    }

    @Override
    public void showLoading() {
        showViews(false, true, true, false);
    }

    @Override
    public void showInfo(int resId) {
        info.setText(resId);
    }

    @Override
    public void showError(String module, String method, int resId) {
        showViews(true, false, false, true);
        error.setText(resId);
        Logs.log(Logs.ERROR, module, method, getText(resId).toString());
    }

    @Override
    public void showSnackbar(int resId) {
        Snackbar.make(getView(), resId, Snackbar.LENGTH_LONG).show();
    }

    private void showViews(Boolean submit, Boolean progress, Boolean info, Boolean error) {
        this.submit.setVisibility(submit ? View.VISIBLE : View.GONE);
        this.progress.setVisibility(progress ? View.VISIBLE : View.GONE);
        this.info.setVisibility(info ? View.VISIBLE : View.GONE);
        this.error.setVisibility(error ? View.VISIBLE : View.GONE);
    }

    @Override
    public void hideKeyboard() {
        keyboardHide(login);
    }

    public void showKeyboard() {
        keyboardShow(login);
    }

    @Override
    public boolean controlErrors() {
        boolean valid = true;
        login.setError(null);
        password.setError(null);
        if (TextUtils.isEmpty(login.getText())) {
            login.setError(getString(R.string.login_login_required));
            valid = false;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError(getString(R.string.login_password_required));
            valid = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            if (permissions.size() != 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean result = true;
                    for(int grantResult : grantResults) {
                        result &= grantResult == PackageManager.PERMISSION_GRANTED;
                    }
                    if (result) {
                        presenter.login(login.getText().toString(), password.getText().toString());
                    }
                }
                break;
        }
    }

    @OnClick(R.id.submit)
    public void submit(View view) {
        presenter.login(login.getText().toString(), password.getText().toString());
    }

    @OnEditorAction(R.id.password)
    public boolean passwordActionDone(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            presenter.login(login.getText().toString(), password.getText().toString());
        }
        return true;
    }

    public void setOnLoggedListener(OnLoggedListener l) {
        onLoggedListener = l;
    }

    public interface OnLoggedListener {
        void onLogged();
    }

    private boolean hasConnection(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            Logs.log(Logs.INFO, getClass().getSimpleName(), Errors.METHOD_PLAN_HASCONNECTION, Logs.SYNC_DATA_CONNECT_OK);
            return true;
        } else {
            Logs.log(Logs.INFO, getClass().getSimpleName(), Errors.METHOD_PLAN_HASCONNECTION, Logs.SYNC_DATA_CONNECT_ERROR);
            return false;
        }
    }
}