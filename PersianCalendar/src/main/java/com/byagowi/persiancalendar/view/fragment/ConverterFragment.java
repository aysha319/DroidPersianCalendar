package com.byagowi.persiancalendar.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.byagowi.common.Range;
import com.byagowi.persiancalendar.R;
import com.byagowi.persiancalendar.Utils;
import com.byagowi.persiancalendar.adapter.CalendarTypesSpinnerAdapter;
import com.byagowi.persiancalendar.adapter.ShapedArrayAdapter;
import com.byagowi.persiancalendar.enums.CalendarType;

import java.util.ArrayList;
import java.util.List;

import calendar.AbstractDate;
import calendar.CivilDate;
import calendar.DateConverter;
import calendar.IslamicDate;
import calendar.PersianDate;

/**
 * Program activity for android
 *
 * @author ebraminio
 */
public class ConverterFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private final Utils utils = Utils.getInstance();
    private Spinner calendarTypeSpinner;
    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Spinner daySpinner;
    private int startingYearOnYearSpinner = 0;
    private TextView date0;
    private TextView date1;
    private TextView date2;
    private RelativeLayout moreDate;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        utils.setActivityTitleAndSubtitle(getActivity(), getString(R.string.date_converter), "");

        // fill members
        calendarTypeSpinner = (Spinner) view.findViewById(R.id.calendarTypeSpinner);
        yearSpinner = (Spinner) view.findViewById(R.id.yearSpinner);
        monthSpinner = (Spinner) view.findViewById(R.id.monthSpinner);
        daySpinner = (Spinner) view.findViewById(R.id.daySpinner);

        date0 = (TextView) view.findViewById(R.id.date0);
        date1 = (TextView) view.findViewById(R.id.date1);
        date2 = (TextView) view.findViewById(R.id.date2);

        date0.setOnClickListener(this);
        date1.setOnClickListener(this);
        date2.setOnClickListener(this);

        moreDate = (RelativeLayout) view.findViewById(R.id.more_date);

        // Shape and set font
        Context context = getContext();
        utils.prepareShapeTextView(context, (TextView) view.findViewById(R.id.converterLabelDay));
        utils.prepareShapeTextView(context, (TextView) view.findViewById(R.id.converterLabelMonth));
        utils.prepareShapeTextView(context, (TextView) view.findViewById(R.id.converterLabelYear));
        utils.prepareShapeTextView(context, (TextView) view.findViewById(R.id.calendarTypeTitle));

        utils.prepareTextView(context, date0);
        utils.prepareTextView(context, date1);
        utils.prepareTextView(context, date2);
        //

        // fill views
        calendarTypeSpinner.setAdapter(new CalendarTypesSpinnerAdapter(context,
                android.R.layout.select_dialog_item));
        calendarTypeSpinner.setSelection(0);

        fillYearMonthDaySpinners();

        calendarTypeSpinner.setOnItemSelectedListener(this);

        yearSpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        daySpinner.setOnItemSelectedListener(this);
        //
        return view;
    }

    void fillCalendarInfo() {
        int year = startingYearOnYearSpinner
                + yearSpinner.getSelectedItemPosition();
        int month = monthSpinner.getSelectedItemPosition() + 1;
        int day = daySpinner.getSelectedItemPosition() + 1;
        CalendarType calendarType = (CalendarType) calendarTypeSpinner.getSelectedItem();

        CivilDate civilDate = null;
        PersianDate persianDate;
        IslamicDate islamicDate;

        char[] digits = utils.preferredDigits(getContext());
        StringBuilder sb = new StringBuilder();

        try {
            List<String> calendarsTextList = new ArrayList<>();
            switch (calendarType) {
                case GEORGIAN:
                    civilDate = new CivilDate(year, month, day);
                    islamicDate = DateConverter.civilToIslamic(civilDate, 0);
                    persianDate = DateConverter.civilToPersian(civilDate);

                    calendarsTextList.add(utils.dateToString(civilDate, digits));
                    calendarsTextList.add(utils.dateToString(persianDate, digits));
                    calendarsTextList.add(utils.dateToString(islamicDate, digits));
                    break;

                case ISLAMIC:
                    islamicDate = new IslamicDate(year, month, day);
                    civilDate = DateConverter.islamicToCivil(islamicDate);
                    persianDate = DateConverter.islamicToPersian(islamicDate);

                    calendarsTextList.add(utils.dateToString(islamicDate, digits));
                    calendarsTextList.add(utils.dateToString(civilDate, digits));
                    calendarsTextList.add(utils.dateToString(persianDate, digits));
                    break;

                case SHAMSI:
                    persianDate = new PersianDate(year, month, day);
                    civilDate = DateConverter.persianToCivil(persianDate);
                    islamicDate = DateConverter.persianToIslamic(persianDate);

                    calendarsTextList.add(utils.dateToString(persianDate, digits));
                    calendarsTextList.add(utils.dateToString(civilDate, digits));
                    calendarsTextList.add(utils.dateToString(islamicDate, digits));
                    break;
            }

            sb.append(utils.getWeekDayName(civilDate));
            sb.append(Utils.PERSIAN_COMMA);
            sb.append(" ");
            sb.append(calendarsTextList.get(0));

            date0.setText(Utils.shape(sb.toString()));
            date1.setText(Utils.shape(calendarsTextList.get(1)));
            date2.setText(Utils.shape(calendarsTextList.get(2)));

        } catch (RuntimeException e) {
            moreDate.setVisibility(View.GONE);
            date0.setText(getString(R.string.date_exception));
        }
    }

    private void fillYearMonthDaySpinners() {
        char[] digits = utils.preferredDigits(getContext());

        AbstractDate date = null;
        PersianDate newDatePersian = Utils.getToday();
        CivilDate newDateCivil = DateConverter.persianToCivil(newDatePersian);
        IslamicDate newDateIslamic = DateConverter.persianToIslamic(newDatePersian);

        CalendarType selectedCalendarType = (CalendarType) calendarTypeSpinner.getSelectedItem();
        switch (selectedCalendarType) {
            case GEORGIAN:
                date = newDateCivil;
                break;

            case ISLAMIC:
                date = newDateIslamic;
                break;

            case SHAMSI:
                date = newDatePersian;
                break;
        }

        int dropdownLayout = android.R.layout.simple_spinner_dropdown_item;
        // years spinner init.
        List<String> yearsList = new ArrayList<>();
        int yearDiffRange = 200;
        startingYearOnYearSpinner = date.getYear() - yearDiffRange / 2;
        for (int i : new Range(startingYearOnYearSpinner, yearDiffRange)) {
            yearsList.add(Utils.formatNumber(i, digits));
        }
        yearSpinner.setAdapter(new ShapedArrayAdapter(getContext(), dropdownLayout, yearsList));
        yearSpinner.setSelection(yearDiffRange / 2);
        //

        // month spinner init.
        List<String> monthsList = utils.getMonthNameList(date);
        monthSpinner.setAdapter(new ShapedArrayAdapter(getContext(), dropdownLayout, monthsList));
        monthSpinner.setSelection(date.getMonth() - 1);
        //

        // days spinner init.
        List<String> daysList = new ArrayList<>();
        for (int i : new Range(1, 31)) {
            daysList.add(Utils.formatNumber(i, digits));
        }
        daySpinner.setAdapter(new ShapedArrayAdapter(getContext(), dropdownLayout, daysList));
        daySpinner.setSelection(date.getDayOfMonth() - 1);
        //

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.yearSpinner:
                fillCalendarInfo();
                break;

            case R.id.monthSpinner:
                fillCalendarInfo();
                break;

            case R.id.daySpinner:
                fillCalendarInfo();
                break;

            case R.id.calendarTypeSpinner:
                fillYearMonthDaySpinners();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        Utils.copyToClipboard(getContext(), view);
    }
}
