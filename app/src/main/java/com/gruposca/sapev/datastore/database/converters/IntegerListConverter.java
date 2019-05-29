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

package com.gruposca.sapev.datastore.database.converters;

import android.text.TextUtils;

import com.gruposca.sapev.datastore.database.model.IntegerList;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.ArrayList;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class IntegerListConverter extends TypeConverter<String, IntegerList> {

    @Override
    public String getDBValue(IntegerList model) {
        ArrayList<String> strings = new ArrayList<>();
        for(Integer value : model)
            strings.add(value.toString());
        return TextUtils.join(";", strings);
    }

    @Override
    public IntegerList getModelValue(String data) {
        IntegerList list = new IntegerList();
        String[] values = data.split(";");
        for(String value : values) {
            try {
                list.add(Integer.parseInt(value));
            } catch (Exception exception) {

            }
        }
        return list;
    }
}