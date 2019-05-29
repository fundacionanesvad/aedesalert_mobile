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

package com.gruposca.sapev.presenter;

import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.query.SampleListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.interactor.Callback;
import com.gruposca.sapev.interactor.ElementGetList;
import com.gruposca.sapev.interactor.SamplesSummaryGetList;
import com.gruposca.sapev.tool.Errors;
import com.gruposca.sapev.tool.Logs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SamplesSummaryPresenterImpl implements SamplesSummaryPresenter {

    private SamplesSummaryPresenter.View view;
    private final SamplesSummaryGetList samplesSummaryGetList;
    private final ElementGetList elementGetList;

    private List<Element> states;

    @Inject
    public SamplesSummaryPresenterImpl(ElementGetList elementGetList,SamplesSummaryGetList samplesSummaryGetList) {
        this.samplesSummaryGetList = samplesSummaryGetList;
        this.elementGetList = elementGetList;
    }

    @Override
    public void initialize(SamplesSummaryPresenter.View view) {
        this.view = view;
        this.view.showLoading();
    }

    @Override
    public void load() {
        elementGetList.execute(Element.TABLE_STATES, new Callback<List<Element>>() {
            @Override
            public void onResponse(List<Element> response) {
                states = response;
            }
            @Override
            public void onError(ErrorBundle error) {
                Logs.log(Logs.ERROR, getClass().getSimpleName(), Errors.METHOD_REGISTRY_ELEMENT_GETLIST, error.toString());
            }
        });

        samplesSummaryGetList.execute(new Callback<List<SampleListQuery>>() {
            @Override
            public void onResponse(List<SampleListQuery> response) {
                view.showSampleList(response,states);
            }

            @Override
            public void onError(ErrorBundle error) {
                view.showErrorLoad(this.getClass().toString(), Errors.METHOD_SAMPLESSUMMARY_GETLIST, error.toString());
            }
        });
    }

}