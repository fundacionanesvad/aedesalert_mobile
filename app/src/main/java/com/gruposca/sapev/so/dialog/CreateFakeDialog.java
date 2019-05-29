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

package com.gruposca.sapev.so.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.House;
import com.gruposca.sapev.datastore.database.model.Plan;
import com.gruposca.sapev.datastore.database.model.Visit;
import com.gruposca.sapev.repository.AreaRepository;
import com.gruposca.sapev.repository.HouseRepository;
import com.gruposca.sapev.repository.PlanRepository;
import com.gruposca.sapev.repository.VisitRepository;
import com.gruposca.sapev.so.activity.AbsActivity;

import java.util.Calendar;

import javax.inject.Inject;

public class CreateFakeDialog extends AbsDialog {

    @Inject
    protected AreaRepository areaRepository;

    @Inject
    protected HouseRepository houseRepository;

    @Inject
    protected VisitRepository visitRepository;

    @Inject
    protected PlanRepository planRepository;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crear datos falsos (40)")
                .setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createFakes();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    private void createFakes() {
        ((AbsActivity)getActivity()).inject(this);
        int area = areaRepository.getAreas().get(0).id;
        Plan plan = planRepository.getPlan();
        for (Integer i = 1; i <= 40; i++) {
            House house = new House();
            house.area.id = area;
            house.streetName = "Calle falsa";
            house.streetNumber = i.toString();
            house.number = i;
            house.code = "CODE_" + i.toString();
            house.temporary = false;
            house.latitude = 40.3;
            house.longitude = 40.3;
            house.visited = true;
            house.visitInProgress = false;
            house.lastVisitDate = Calendar.getInstance();
            house.lastVisitResult.id = Visit.RESULT_CLOSED;
            houseRepository.saveHouse(house);

            Visit visit = new Visit();
            visit.result = house.lastVisitResult;
            visit.house = house;
            visitRepository.saveVisit(visit);

            plan.progress++;
        }
        planRepository.savePlan(plan);
    }
}