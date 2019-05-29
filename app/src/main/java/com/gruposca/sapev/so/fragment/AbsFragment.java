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
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import com.gruposca.sapev.R;
import com.gruposca.sapev.so.activity.AbsActivity;
import com.gruposca.sapev.tool.Logs;

import butterknife.ButterKnife;


public abstract class AbsFragment extends Fragment {

    public int getActionBarTitle() { return R.string.app_name; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);

            trace("onCreate");

            this.onFragmentCreate(savedInstanceState);

        } catch (Exception e) {
            manageFatalUnhandledException("onCreate", e);
        }
    }

    protected void onFragmentCreate(Bundle savedInstanceState) { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnedValue = null;
        try {

            trace("onCreateView");
            returnedValue = this.onFragmentCreateView(inflater, container, savedInstanceState);

        } catch (Exception e) {
            manageFatalUnhandledException("onCreateView", e);
        } finally {
            return returnedValue;
        }
    }

    protected View onFragmentCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int fragmentLayoutResourceId = this.onFragmentRequestLayoutResourceId();

        if (fragmentLayoutResourceId != 0)
            return LayoutInflater.from(getActivity()).inflate(fragmentLayoutResourceId, null);
        else
            return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract int onFragmentRequestLayoutResourceId();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {

            trace("onViewCreated");
            this.injectViews();
            this.onFragmentViewCreated(view, savedInstanceState);
            this.onFragmentInitializeViews();

        } catch (Exception e) {
            manageFatalUnhandledException("onViewCreated", e);
        } finally {
            super.onViewCreated(view, savedInstanceState);
        }
    }

    protected void onFragmentViewCreated(View view, Bundle savedInstanceState) { }

    protected void onFragmentInitializeViews() { }

    @Override
    public void onAttach(Activity activity) {
        try {

            inject();
            onFragmentAttach(activity);

        } catch (Exception e) {
            manageFatalUnhandledException("onAttach", e);
        } finally {
            super.onAttach(activity);
        }
    }

    protected void onFragmentAttach(Activity activity) { }

    private void inject() {
        trace("Injecting Dependencies");
        ((AbsActivity)getActivity()).inject(this);
    }

    protected final void injectViews() {
        trace("Injecting Views");
        ButterKnife.inject(this, getView());
    }

    @Override
    public void onStart() {
        try {

            trace("onStart");
            this.onFragmentStart();

        } catch (Exception e) {
            manageFatalUnhandledException("onStart", e);
        } finally {
            super.onStart();
        }
    }

    protected void onFragmentStart() { }

    @Override
    public void onPause() {
        try {

            trace("onPause");
            this.onFragmentPause();

        } catch (Exception e) {
            manageFatalUnhandledException("onPause", e);
        } finally {
            super.onPause();
        }
    }

    protected void onFragmentPause() { }

    @Override
    public void onStop() {
        try {

            trace("onStop");
            this.onFragmentStop();

        } catch (Exception e) {
            manageFatalUnhandledException("onStop", e);
        } finally {
            super.onStop();
        }
    }

    protected void onFragmentStop() { }

    @Override
    public void onDestroyView() {
        try {

            trace("onDestroyView");
            this.onFragmentDestroyView();
            this.releaseViewInjection();

        } catch (Exception e) {
            manageFatalUnhandledException("onDestroyView", e);
        } finally {
            super.onDestroyView();
        }
    }

    protected final void releaseViewInjection() {
        trace("releaseViewInjection");
        ButterKnife.reset(this);
    }

    protected void onFragmentDestroyView() { }

    @Override
    public void onDestroy() {
        try {

            trace("onDestroy");
            this.onFragmentDestroy();

        } catch (Exception e) {
            manageFatalUnhandledException("onDestroy", e);
        } finally {
            super.onDestroy();
        }
    }

    protected void onFragmentDestroy() { }

    public void showSnackbar(int resId) {
        View view = getView();
        if (view != null)
            Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    public final String getFragmentTagForFragmentManager() {
        return this.getClass().getCanonicalName();
    }

    protected final void manageFatalUnhandledException(String method, Throwable error) {
        String module = this.getClass().getSimpleName();
        Logs.log(Logs.ERROR, module, method, error.getMessage(), error);
    }

    protected final void trace(String method) {
        String module = this.getClass().getSimpleName();
        Logs.log(Logs.VERBOSE, module, method);
    }

    protected void keyboardHide(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void keyboardShow(final View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    protected void keyboardShowDelay(final View view) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                keyboardShow(view);
            }
        }, 100);
    }

    protected void keyboardHideDelay(final View view) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                keyboardHide(view);
            }
        }, 100);
    }
}