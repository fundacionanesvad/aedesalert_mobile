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

package com.gruposca.sapev.datastore.database.managers;

import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.datastore.database.model.Symptom$Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SymptomManager {

    @Inject
    public SymptomManager() { }

    public void saveSymptoms(List<Symptom> list) {
        for(Symptom symptom : list) {
            symptom.save();
        }
    }

    public List<Symptom> getSymptoms(String visitUuid, String personUuid) {
        return new Select()
                .from(Symptom.class)
                .where(Condition.column(Symptom$Table.PERSON_PERSONUUID).eq(personUuid))
                .and(Condition.column(Symptom$Table.VISIT_VISITUUID).eq(visitUuid))
                .orderBy(Symptom$Table.SYMPTOM_SYMPTOMID)
                .queryList();
    }
}