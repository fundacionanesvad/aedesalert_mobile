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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.query.HouseListQuery;

import java.text.Format;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HouseListAdapter extends AbsAdapter<HouseListQuery, HouseListAdapter.HouseViewHolder> {

    private Format dateFormat;
    private String streetFormat;

    public HouseListAdapter(Context context) {
        super(context);
        this.dateFormat = DateFormat.getDateFormat(context);
        this.streetFormat = context.getString(R.string.street_format);
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_house;
    }

    @Override
    protected HouseViewHolder createViewHolder(View view) {
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HouseViewHolder viewHolder, int i) {
        HouseListQuery item = getItem(i);
        viewHolder.name.setText(String.format(streetFormat, item.streetName, item.streetNumber));
        if (item.lastVisitDate == null) {
            viewHolder.state.setText(R.string.never_visited);
            viewHolder.date.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.state.setText(item.lastVisitResultName);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(dateFormat.format(item.lastVisitDate.getTime()));
        }
        if (item.visited) {
            viewHolder.visited.setVisibility(View.VISIBLE);
            viewHolder.visited.setImageResource(R.drawable.icon_visited);
        } else if (item.visitInProgress) {
            viewHolder.visited.setVisibility(View.VISIBLE);
            viewHolder.visited.setImageResource(R.drawable.icon_visit_in_progress);
        } else {
            viewHolder.visited.setVisibility(View.INVISIBLE);
        }
        viewHolder.feverish.setVisibility(item.feverish ? View.VISIBLE : View.INVISIBLE);
        viewHolder.focus.setVisibility(item.focus ? View.VISIBLE : View.INVISIBLE);
        viewHolder.qr.setVisibility(TextUtils.isEmpty(item.qrCode) ? View.INVISIBLE : View.VISIBLE);
        viewHolder.gps.setVisibility(item.latitude == null ? View.INVISIBLE : View.VISIBLE);
    }

    public class HouseViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.name) public TextView name;
        @InjectView(R.id.state) public TextView state;
        @InjectView(R.id.date) public TextView date;
        @InjectView(R.id.visited) public ImageView visited;
        @InjectView(R.id.feverish) public View feverish;
        @InjectView(R.id.focus) public View focus;
        @InjectView(R.id.gps) public View gps;
        @InjectView(R.id.qr) public View qr;

        public HouseViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}