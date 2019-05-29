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
import com.gruposca.sapev.datastore.database.query.InventoryListQuery;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InventoryListAdapter extends AbsAdapter<InventoryListQuery, InventoryListAdapter.InventoryViewHolder> {

    public InventoryListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_inventory;
    }

    @Override
    protected InventoryViewHolder createViewHolder(View view) {
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder viewHolder, int i) {
        InventoryListQuery item = getItem(i);
        viewHolder.inspected.setText(item.inspected.toString());
        viewHolder.focus.setText(item.focus.toString());
        viewHolder.treated.setText(item.treated.toString());
        viewHolder.destroyed.setText(item.destroyed.toString());
        viewHolder.container.setText(item.containerName);
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.inspected) protected TextView inspected;
        @InjectView(R.id.focus) protected TextView focus;
        @InjectView(R.id.treated) protected TextView treated;
        @InjectView(R.id.destroyed) protected TextView destroyed;
        @InjectView(R.id.container) protected TextView container;

        public InventoryViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}