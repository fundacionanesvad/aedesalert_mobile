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
import android.view.View;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.RegistryListQuery;

import java.text.DateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegistryListAdapter extends AbsAdapter<RegistryListQuery, RegistryListAdapter.RegistryViewHolder> {

    protected DateFormat timeFormat;
    protected String samplePacketsFormat;
    protected String sampleFormat;
    protected String packetsFormat;
    protected String doseName;

    public RegistryListAdapter(Context context, String doseName) {
        super(context);
        this.timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.samplePacketsFormat = context.getString(R.string.inventory_sample_packets);
        this.sampleFormat = context.getString(R.string.inventory_sample);
        this.packetsFormat = context.getString(R.string.inventory_packets);
        this.doseName = doseName;
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_registry;
    }

    @Override
    protected RegistryViewHolder createViewHolder(View view) {
        return new RegistryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegistryViewHolder viewHolder, int i) {
        RegistryListQuery item = getItem(i);
        String units = item.units.toString();
        viewHolder.container.setText(item.containerName);
        viewHolder.inspected.setText(units);
        viewHolder.focus.setText(item.focus ? units : "0");
        viewHolder.treated.setText(item.treated ? units : "0");
        viewHolder.destroyed.setText(item.destroyed ? units : "0");
        String description = "";
        if (item.focus && item.treated) {
            description += String.format(samplePacketsFormat, item.sample, item.packets, doseName);
        } else if (item.focus) {
            description += String.format(sampleFormat, item.sample);
        } else if (item.treated) {
            description += String.format(packetsFormat, item.packets, doseName);
        }
        viewHolder.description.setText(description);
        viewHolder.time.setText(timeFormat.format(item.date.getTime()));
        viewHolder.comment.setText(item.comment);
    }

    public class RegistryViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.container) protected TextView container;
        @InjectView(R.id.inspected) protected TextView inspected;
        @InjectView(R.id.focus) protected TextView focus;
        @InjectView(R.id.treated) protected TextView treated;
        @InjectView(R.id.destroyed) protected TextView destroyed;
        @InjectView(R.id.description) protected TextView description;
        @InjectView(R.id.time) protected TextView time;
        @InjectView(R.id.comment) protected TextView comment;

        public RegistryViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}