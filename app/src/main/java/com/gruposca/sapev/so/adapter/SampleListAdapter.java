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

package com.gruposca.sapev.so.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.query.SampleListQuery;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SampleListAdapter extends AbsAdapter<SampleListQuery, SampleListAdapter.SampleViewHolder> {

    protected String streetFormat;
    protected String codeFormat;
    protected List<Element> valuesElements;

    public SampleListAdapter(Context context, List<Element> values) {
        super(context);
        this.streetFormat = context.getString(R.string.sample_street_format);
        this.codeFormat = context.getString(R.string.sample_code_format);
        this.valuesElements = values;
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_sample;
    }

    @Override
    protected SampleViewHolder createViewHolder(View view) {
        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SampleViewHolder viewHolder, int i) {
        SampleListQuery item = getItem(i);
        viewHolder.house.setText(Html.fromHtml(String.format(streetFormat, item.streetName, item.streetNumber)));
        String label = "";
        for(int m = 0 ; m < item.states.size() ; m++){
            for(int k = 0 ; k < valuesElements.size() ; k++){
                if(item.states.get(m).equals(valuesElements.get(k).id)){
                    label += valuesElements.get(k).name;
                    if(m +1 != item.states.size()){
                        label += ", ";
                    }
                    break;
                }
            }
        }
        viewHolder.code.setText(Html.fromHtml(String.format(codeFormat, item.sample, label)));
    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.code) protected TextView code;
        @InjectView(R.id.house) protected TextView house;
        public SampleViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}