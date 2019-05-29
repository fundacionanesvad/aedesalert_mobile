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

package com.gruposca.sapev.repository;

import com.gruposca.sapev.datastore.database.managers.ElementManager;
import com.gruposca.sapev.datastore.database.managers.PersonManager;
import com.gruposca.sapev.datastore.database.managers.SymptomManager;
import com.gruposca.sapev.datastore.database.managers.VisitManager;
import com.gruposca.sapev.datastore.database.model.Element;
import com.gruposca.sapev.datastore.database.model.Symptom;
import com.gruposca.sapev.datastore.database.model.Visit;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SymptomRepository {

    private final SymptomManager symptomManager;
    private final ElementManager elementManager;
    private final VisitManager visitManager;
    private final PersonManager personManager;

    @Inject
    public SymptomRepository(SymptomManager symptomManager, ElementManager elementManager, VisitManager visitManager, PersonManager personManager) {
        this.symptomManager = symptomManager;
        this.elementManager = elementManager;
        this.visitManager = visitManager;
        this.personManager = personManager;
    }

    public Boolean saveSymptoms(List<Symptom> list, String visitUuid, String personUuid) {
        symptomManager.saveSymptoms(list);
        Visit visit = visitManager.getVisit(visitUuid);
        visit.feverish = personManager.getPersonsFeverish(visitUuid).size();
        visitManager.saveVisit(visit);
        return true;
    }

    public List<Symptom> getSymptoms(String visitUuid, String personUuid) {
        List<Symptom> symptoms = symptomManager.getSymptoms(visitUuid, personUuid);
        if (symptoms.size() == 0) {
            List<Element> elements = elementManager.getElements(Element.TABLE_SYMPTOMS);
            for(Element element : elements) {
                Symptom symptom = new Symptom();
                symptom.symptom = element;
                symptom.person.uuid = personUuid;
                symptom.visit.uuid = visitUuid;
                symptoms.add(symptom);
            }
            symptomManager.saveSymptoms(symptoms);
        }
        return symptoms;
    }
}