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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gruposca.sapev.R;
import com.gruposca.sapev.datastore.database.model.Person;
import com.gruposca.sapev.presenter.PersonDetailPresenter;
import com.gruposca.sapev.so.activity.AbsActivity;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Params;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

public class PersonDetailDialog extends AbsDialog  implements PersonDetailPresenter.View {

    @Inject
    protected PersonDetailPresenter presenter;

    @InjectView(R.id.name) protected EditText name;
    @InjectView(R.id.card_id) protected EditText cardId;
    @InjectView(R.id.male) protected RadioButton male;
    @InjectView(R.id.female) protected RadioButton female;
    @InjectView(R.id.age) protected RadioButton age;
    @InjectView(R.id.birthday) protected RadioButton birthday;
    @InjectView(R.id.age_content) protected View ageContent;
    @InjectView(R.id.birthday_content) protected View birthdayContent;
    @InjectView(R.id.age_value) protected TextView ageValue;
    @InjectView(R.id.date_day) protected TextView dateDay;
    @InjectView(R.id.date_month) protected TextView dateMonth;
    @InjectView(R.id.date_year) protected TextView dateYear;

    private OnPersonDetailListener onPersonDetailListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_person_detail, null);
        ButterKnife.inject(this, view);
        builder.setTitle(R.string.person_detail_title)
                .setView(view)
                .setPositiveButton(R.string.action_save, null)
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.setView(this);
        presenter.initialize(getArguments().getString(Params.PERSON_UUID), getArguments().getString(Params.HOUSE_UUID));
        ((AlertDialog) getDialog()).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.save();
            }
        });

        name.requestFocus();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AbsActivity)getActivity()).inject(this);
    }

    private void enableNextButton() {
        boolean valid = true;
        if (TextUtils.isEmpty(name.getText()))
            valid = false;
        if (getBirthday() == null)
            valid = false;
        if (!validateDocumentPeru(cardId.getText().toString()))
            valid = false;
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(valid);
    }

    private Calendar getBirthday() {
        Calendar calendar = null;
        if (age.isChecked()) {
            try {
                Integer value = Integer.parseInt(ageValue.getText().toString());
                calendar = presenter.setAge(value);
            } catch (Exception e) {
                calendar = null;
            }
        } else {
            try {
                Integer year = Integer.parseInt(dateYear.getText().toString());
                Integer month = Integer.parseInt(dateMonth.getText().toString()) - 1;
                Integer day = Integer.parseInt(dateDay.getText().toString());
                if (month <= Calendar.DECEMBER) {
                    calendar = Calendar.getInstance();
                    calendar.set(year, month, 1);
                    if (day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                    } else {
                        calendar = null;
                    }
                } else {
                    calendar = null;
                }
                presenter.setBirthday(calendar);
            } catch (Exception e) {
                calendar = null;
            }
        }
        return calendar;
    }

    @OnTextChanged(R.id.name)
    public void onNameChanged(CharSequence text) {
        enableNextButton();
        presenter.setName(text.toString());
    }

    @OnTextChanged(R.id.card_id)
    public void onIdChanged(CharSequence text) {
        presenter.setCardId(text.toString());
    }

    @OnCheckedChanged(R.id.male)
    public void onMaleChecked(boolean checked) {
        if (checked) {
            presenter.setGenre(Person.GENRE_MALE);
        }
    }

    @OnCheckedChanged(R.id.female)
    public void onFemaleChecked(boolean checked) {
        if (checked) {
            presenter.setGenre(Person.GENRE_FEMALE);
        }
    }

    @OnCheckedChanged(R.id.age)
    public void onAgeChecked(boolean checked) {
        if (checked) {
            presenter.setAge();
            if (TextUtils.isEmpty(ageValue.getText())) {
                ageValue.requestFocus();
                keyboardShow(ageValue);
            }
        }
        ageContent.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @OnCheckedChanged(R.id.birthday)
    public void onBirthdayChecked(boolean checked) {
        if (checked) {
            presenter.setBirthday();
            if (TextUtils.isEmpty(dateDay.getText())) {
                dateDay.requestFocus();
                keyboardShow(dateDay);
            }
        }
        birthdayContent.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @OnTextChanged(R.id.date_day)
    public void onDateDayChanged(CharSequence text) {
        enableNextButton();
        if (text.length() == 2 && TextUtils.isEmpty(dateMonth.getText())) {
            dateMonth.requestFocus();
        }
    }

    @OnTextChanged(R.id.date_month)
    public void onDateMonthChanged(CharSequence text) {
        enableNextButton();
        if (text.length() == 2 && TextUtils.isEmpty(dateYear.getText())) {
            dateYear.requestFocus();
        }
    }

    @OnTextChanged(R.id.date_year)
    public void onDateYearChanged(CharSequence text) {
        enableNextButton();
        if (text.length() == 4) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(dateYear.getWindowToken(), 0);
        }
    }

    @OnTextChanged(R.id.age_value)
    public void onAgeValueChanged() {
        enableNextButton();
    }

    @OnTextChanged(R.id.card_id)
    public void onCardValueChanged() {
        enableNextButton();
    }

    @Override
    public void showPerson(Person person) {
        if (person.genre.equals(Person.GENRE_MALE)) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }
        name.setText(person.name);
        cardId.setText(person.cardId);
        enableNextButton();
    }

    @Override
    public void showBirthday(Person person) {
        if (person.birthdayExact) {
            age.setChecked(false);
            birthday.setChecked(true);
            if (person.birthday == null) {
                dateYear.setText("");
                dateMonth.setText("");
                dateDay.setText("");
            } else {
                dateYear.setText(String.format("%04d", person.birthday.get(Calendar.YEAR)));
                dateMonth.setText(String.format("%02d", person.birthday.get(Calendar.MONTH)));
                dateDay.setText(String.format("%02d", person.birthday.get(Calendar.DAY_OF_MONTH)));
            }
        } else {
            birthday.setChecked(false);
            age.setChecked(true);
            if (person.birthday == null) {
                ageValue.setText("");
            } else {
                ageValue.setText(person.getAge().toString());
            }
        }
        keyboardHide(name);
    }

    @Override
    public void onCanceled() {
        if (onPersonDetailListener != null)
            onPersonDetailListener.onCanceled();
        dismiss();
    }

    @Override
    public void onSaved() {
        if (onPersonDetailListener != null)
            onPersonDetailListener.onSaved();
        dismiss();
    }

    public void setOnPersonDetailListener(OnPersonDetailListener l) {
        onPersonDetailListener = l;
    }

    public interface OnPersonDetailListener {
        void onSaved();
        void onCanceled();
    }

    private Boolean validateDocumentPeru(String identificationDocument) {
        if (identificationDocument != null && !TextUtils.isEmpty(identificationDocument)) {
            identificationDocument = identificationDocument.trim();
            if (identificationDocument.length() == 8 && TextUtils.isDigitsOnly((identificationDocument)))
                return true;
            return false;
            /*identificationDocument = identificationDocument.toUpperCase();
            int addition = 0;
            int[] hash = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
            int identificationDocumentLength = identificationDocument.length();
            String identificationComponent = identificationDocument.substring(0, identificationDocumentLength - 1);
            int identificationComponentLength = identificationComponent.length();
            int diff = hash.length - identificationComponentLength;
            for (int i = identificationComponentLength - 1; i >= 0; i--)
                addition += (identificationComponent.charAt(i) - '0') * hash[i + diff];
            addition = addition % 11;
            if (addition == 11)
                addition = 0;
            else if (addition == 10)
                addition = 1;
            char last = identificationDocument.charAt(identificationDocumentLength - 1);
            if (identificationDocumentLength == 11) {
                // The identification document corresponds to a RUC.
                return addition == last - '0';
            } else if (Character.isDigit(last)) {
                // The identification document corresponds to a DNI with a number as verification digit.
                char[] hashNumbers = {'6', '7', '8', '9', '0', '1', '1', '2', '3', '4', '5'};
                return last == hashNumbers[addition];
            } else if (Character.isLetter(last)) {
                // The identification document corresponds to a DNI with a letter as verification digit.
                char[] hashLetters = {'K', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
                return last == hashLetters[addition];
            }*/
        }
        return true;
    }
}