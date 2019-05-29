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

package com.gruposca.sapev.so.component;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.IntegerList;

import java.util.ArrayList;
import java.util.List;


public class MultiElementSpinner extends AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {

    private Context context;
    private List<Element> items;
    private boolean[] selection;
    private ArrayAdapter<String> adapter;
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener{
        void onSelectedElementsChanged(IntegerList ids);
    }

    public MultiElementSpinner(Context context) {
        super(context);
        this.context = context;
        this.adapter = new ArrayAdapter<>(context, R.layout.item_state_selection);
        this.adapter.add(getSelectionString());
        super.setAdapter(adapter);

    }

    public MultiElementSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.adapter = new ArrayAdapter<>(context, R.layout.item_state_selection);
        this.adapter.add(getSelectionString());
        super.setAdapter(adapter);

    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

        throw new RuntimeException("setAdapter is not supported by MultiElementSpinner.");
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

        if (selection != null && position < selection.length) {
            selection[position] = isChecked;
        } else {
            throw new IllegalArgumentException("Argument 'position' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        int size = items.size();
        String[] labels = new String[size];
        selection = new boolean[size];
        for (int i = 0; i < size; i++) {
            labels[i] = items.get(i).name;
            selection[i] = items.get(i).checked;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Estado vector");
        builder.setMultiChoiceItems(labels, selection, this);
        builder.setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (int i = 0; i < selection.length; i++)
                    items.get(i).checked = selection[i];
                listener.onSelectedElementsChanged(getSelection());
                adapter.clear();
                adapter.add(getSelectionString());
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.show();
        return true;
    }

    public void setOnSelectionChangeListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Element> items) {this.items = items;
    }

    public void setSelection(IntegerList ids) {
        for(Element item : items) {
            item.checked = ids.contains(item.id);

        }
        adapter.clear();
        adapter.add(getSelectionString());
    }

    public IntegerList getSelection() {

        IntegerList ids = new IntegerList();
        for (Element item : items) {
            if (item.checked)
                ids.add(item.id);
        }

        return ids;
    }

    public String getSelectionString() {
        List<String> strings = new ArrayList<>();
        if (items != null) {
            for (Element item : items) {
                if (item.checked)
                    strings.add(item.name);
            }
        }
        if (strings.size() == 0)
            return context.getString(R.string.inventory_select_state);
        else
            return TextUtils.join(", ", strings);


    }

}