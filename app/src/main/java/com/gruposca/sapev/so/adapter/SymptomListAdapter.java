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
import android.widget.CheckBox;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Symptom;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SymptomListAdapter extends AbsAdapter<Symptom, SymptomListAdapter.SymptomViewHolder> {

    public SymptomListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_symptom;
    }

    @Override
    protected SymptomViewHolder createViewHolder(View view) {
        return new SymptomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SymptomViewHolder viewHolder, final int position) {
        Symptom item = getItem(position);
        viewHolder.name.setText(item.symptom.name);
        viewHolder.checkBox.setChecked(item.selected);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox c = (CheckBox) v.findViewById(R.id.check_symptom);
                if (c.isChecked()) {
                    c.setChecked(false);
                } else {
                    c.setChecked(true);
                }
                items.get(position).selected = c.isChecked();
            }
        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                items.get(position).selected = cb.isChecked();
            }
        });
    }

    public class SymptomViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.name) protected TextView name;
        @InjectView(R.id.check_symptom) protected CheckBox checkBox;

        public SymptomViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}