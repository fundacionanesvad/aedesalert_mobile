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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.query.SampleListQuery;
import com.gruposca.sapev.presenter.SamplesSummaryPresenter;
import com.gruposca.sapev.so.adapter.ItemDecorator;
import com.gruposca.sapev.so.adapter.SampleListAdapter;
import com.gruposca.sapev.tool.Logs;

import java.util.List;
import javax.inject.Inject;
import butterknife.InjectView;

public class SamplesSummaryFragment extends AbsFragment implements SamplesSummaryPresenter.View {

    @Inject
    SamplesSummaryPresenter presenter;

    @InjectView(R.id.list) protected RecyclerView list;
    @InjectView(R.id.empty) protected View empty;
    @InjectView(R.id.progress) protected View progress;
    @InjectView(R.id.error_samples_list) protected TextView error;

    private SampleListAdapter adapter;

    @Override
    protected int onFragmentRequestLayoutResourceId() {
        return R.layout.fragment_sample_list;
    }

    @Override
    protected void onFragmentInitializeViews() {
        setHasOptionsMenu(true);
        presenter.initialize(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.addItemDecoration(new ItemDecorator(getActivity()));
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        presenter.load();
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
    public void showSampleList(List<SampleListQuery> items, List<Element> values) {
        progress.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        if (items.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new SampleListAdapter(getActivity(), values);
                list.setAdapter(adapter);
            }
            adapter.animateTo(items);
        }
    }
}