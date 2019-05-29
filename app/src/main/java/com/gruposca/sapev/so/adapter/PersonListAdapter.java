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
import android.widget.ImageView;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Person;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PersonListAdapter extends AbsAdapter<Person, PersonListAdapter.PersonViewHolder> {

    private Context mContext;
    protected OnItemDeleteClickListener onItemDeleteClicklistener;

    public PersonListAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getItemLayoutResourceId() {
        return R.layout.item_person;
    }

    @Override
    protected PersonListAdapter.PersonViewHolder createViewHolder(View view) {
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder viewHolder, final int i) {
        Person item = getItem(i);
        viewHolder.name.setText(item.name);
        viewHolder.genre.setImageResource(item.genre.equals("M") ? R.drawable.icon_genre_male : R.drawable.icon_genre_female);
        viewHolder.age.setText(mContext.getResources().getString(R.string.age_format, item.getAge()));
        if (onItemDeleteClicklistener != null) {
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemDeleteClicklistener.onItemDeleteClick(view, i);
                }
            });
        } else {
            viewHolder.delete.setVisibility(View.GONE);
        }
    }

    public void setOnItemDeleteClickListener(final OnItemDeleteClickListener onItemDeleteClicklistener) {
        this.onItemDeleteClicklistener = onItemDeleteClicklistener;
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(View view, int position);
    }

    public static class PersonViewHolder<T> extends RecyclerView.ViewHolder {
        @InjectView(R.id.genre) protected ImageView genre;
        @InjectView(R.id.name) protected TextView name;
        @InjectView(R.id.age) protected TextView age;
        @InjectView(R.id.action_delete_person) protected ImageView delete;

        public PersonViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}