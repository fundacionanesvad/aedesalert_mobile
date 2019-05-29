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
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.AreaListQuery;


import butterknife.ButterKnife;
import butterknife.InjectView;

public class AreaListAdapter extends AbsAdapter<AreaListQuery, AreaListAdapter.AreaViewHolder> {

    private String inspectionsFormat,inspectionNone,inspectionOne;
    String inspections;
    public AreaListAdapter(Context context) {
        super(context);
        this.inspectionsFormat = context.getString(R.string.inspections_count);
        this.inspectionNone = context.getString(R.string.inspections_none);
        this.inspectionOne = context.getString(R.string.inspections_one);
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_area;
    }

    @Override
    protected AreaViewHolder createViewHolder(View view) {
        return new AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder viewHolder, int i) {
        AreaListQuery item = getItem(i);
        viewHolder.name.setText(item.name);
        String Sector;

        if(item.sector!=null){
            Sector=" - "+item.sector;}
        else{
            Sector=" - Sector";
        }
        if (item.inspections == null || item.inspections == 0){
            viewHolder.inspections.setText(String.format(inspectionNone+Sector));
        }else if (item.inspections == 1)
            viewHolder.inspections.setText(String.format(inspectionOne+Sector));
        else
            viewHolder.inspections.setText(String.format(inspectionsFormat, item.inspections+Sector));
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.name) protected TextView name;

        @InjectView(R.id.inspections) protected TextView inspections;

        public AreaViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}