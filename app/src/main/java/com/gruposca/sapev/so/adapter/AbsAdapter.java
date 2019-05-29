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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    protected List<T> items;
    protected LayoutInflater inflater;
    protected OnItemClickListener onItemClicklistener;

    public AbsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    protected abstract int getItemLayoutResourceId();

    protected abstract V createViewHolder(View view);

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) { return items.get(position); }

    public List<T> getItems() { return items; }

    public T removeItem(int position) {
        final T item = items.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, T item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final T item = items.remove(fromPosition);
        items.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<T> items) {
        animateTo(items, false);
    }

    public void animateTo(List<T> items, Boolean forceRefresh) {
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items, forceRefresh);
    }

    private void applyAndAnimateRemovals(List<T> newItems) {
        for (int i = items.size() - 1; i >= 0; i--) {
            final T model = items.get(i);
            if (!newItems.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newItems) {
        for (int i = 0, count = newItems.size(); i < count; i++) {
            final T model = newItems.get(i);
            if (!items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newItems, Boolean forceRefresh) {
        for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newItems.get(toPosition);
            final int fromPosition = items.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            } else if (forceRefresh) {
                items.set(fromPosition, model);
                notifyItemChanged(fromPosition);
            }
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(getItemLayoutResourceId(), parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClicklistener != null) {
                    onItemClicklistener.onItemClick(v);
                }
            }
        });
        return createViewHolder(view);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClicklistener) {
        this.onItemClicklistener = onItemClicklistener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }
}