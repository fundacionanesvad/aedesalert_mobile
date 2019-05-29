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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;

import java.util.ArrayList;
import java.util.List;

public class StateListAdapter extends ArrayAdapter<Element> {
    private Context context;
    private List<Element> items;

    public StateListAdapter(Context context, int resource, List<Element> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.item_state, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.text);
            holder.check = convertView.findViewById(R.id.checkbox);
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Integer position = (Integer) buttonView.getTag();
                    if (position != null  && position >= 0) {
                        Element item = items.get(position);
                        item.checked = isChecked;
                        StateListAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Element item = items.get(position);
        holder.text.setText(item.name);
        holder.check.setTag(-1);
        holder.check.setChecked(item.checked);
        holder.check.setTag(position);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.item_state_selection, null);
        }
        List<String> selection = new ArrayList<>();
        for(Element item : items) {
            if (item.checked)
                selection.add(item.name);
        }
        TextView view = (TextView) convertView;
        if (selection.size() == 0) {
            view.setText(R.string.inventory_select_state);
        } else {
            view.setText(TextUtils.join(", ", selection));
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView text;
        private CheckBox check;
    }
}