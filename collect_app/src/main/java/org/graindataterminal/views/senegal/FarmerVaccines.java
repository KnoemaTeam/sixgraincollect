package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.senegal.VaccineGroup;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerVaccines extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "porcin", "volaille", "lapin", "autrre_r"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerVaccines fragment = new FarmerVaccines();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            screenIndex = getArguments().getInt(SCREEN_INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_farmer_vaccines, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateDewormingType(view, inflater);
        updateDewormingUsage(view, inflater);
        updateVaccinesType(view, inflater);
        updateVaccinesUsage(view, inflater);

        return view;
    }

    protected void updateVaccinesUsage(View parentView, LayoutInflater inflater) {
        final VaccineGroup vaccineGroup = ((SenegalSurvey) survey).getVaccineGroup();
        String vaccinesUsageText = vaccineGroup.getVaccinesUsage();

        RadioGroup vaccinesUsageGroup = (RadioGroup) parentView.findViewById(R.id.vaccinesUsageGroup);
        vaccinesUsageGroup.setEnabled(!isModeLocked);
        vaccinesUsageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                vaccineGroup.setVaccinesUsage(id);
                ((SenegalSurvey) survey).setVaccineGroup(vaccineGroup);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_animal_vaccination_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, vaccinesUsageGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(VaccineGroup.vaccinationIdList[index - 1]);
            rb.setId(index * 10);

            vaccinesUsageGroup.addView(rb);

            if (!TextUtils.isEmpty(vaccinesUsageText)) {
                if (rb.getTag().equals(vaccinesUsageGroup)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            vaccinesUsageGroup.check(10);
    }

    protected void updateVaccinesType(View parentView, LayoutInflater inflater) {
        final VaccineGroup vaccineGroup = ((SenegalSurvey) survey).getVaccineGroup();
        final List<String> types = vaccineGroup.getVaccinesType();

        LinearLayout vaccinesTypeLayout1 = (LinearLayout) parentView.findViewById(R.id.vaccinesTypeGroup1);
        vaccinesTypeLayout1.setEnabled(!isModeLocked);

        LinearLayout vaccinesTypeLayout2 = (LinearLayout) parentView.findViewById(R.id.vaccinesTypeGroup2);
        vaccinesTypeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_race_4_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !types.contains(id))
                        types.add(id);
                }
                else {
                    types.remove(id);
                }

                vaccineGroup.setVaccinesType(types);
                ((SenegalSurvey) survey).setVaccineGroup(vaccineGroup);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, vaccinesTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesSource.length / 2)
                vaccinesTypeLayout1.addView(checkBox);
            else
                vaccinesTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateDewormingUsage(View parentView, LayoutInflater inflater) {
        final VaccineGroup vaccineGroup = ((SenegalSurvey) survey).getVaccineGroup();
        String dewormingUsageText = vaccineGroup.getDewormingUsage();

        RadioGroup dewormingUsageGroup = (RadioGroup) parentView.findViewById(R.id.dewormingUsageGroup);
        dewormingUsageGroup.setEnabled(!isModeLocked);
        dewormingUsageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                vaccineGroup.setDewormingUsage(id);
                ((SenegalSurvey) survey).setVaccineGroup(vaccineGroup);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_animal_vaccination_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, dewormingUsageGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(VaccineGroup.vaccinationIdList[index - 1]);
            rb.setId(index * 12);

            dewormingUsageGroup.addView(rb);

            if (!TextUtils.isEmpty(dewormingUsageText)) {
                if (rb.getTag().equals(dewormingUsageText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            dewormingUsageGroup.check(12);
    }

    protected void updateDewormingType(View parentView, LayoutInflater inflater) {
        final VaccineGroup vaccineGroup = ((SenegalSurvey) survey).getVaccineGroup();
        final List<String> types = vaccineGroup.getDewormingType();

        LinearLayout dewormingTypeLayout1 = (LinearLayout) parentView.findViewById(R.id.dewormingTypeGroup1);
        dewormingTypeLayout1.setEnabled(!isModeLocked);

        LinearLayout dewormingTypeLayout2 = (LinearLayout) parentView.findViewById(R.id.dewormingTypeGroup2);
        dewormingTypeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_race_4_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !types.contains(id))
                        types.add(id);
                }
                else {
                    types.remove(id);
                }

                vaccineGroup.setDewormingType(types);
                ((SenegalSurvey) survey).setVaccineGroup(vaccineGroup);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, dewormingTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesSource.length / 2)
                dewormingTypeLayout1.addView(checkBox);
            else
                dewormingTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
