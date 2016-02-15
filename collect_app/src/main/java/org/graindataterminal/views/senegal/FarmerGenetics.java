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

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.GeneticsGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerGenetics extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "porcin", "volaille", "lapin", "autrre_r"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerGenetics fragment = new FarmerGenetics();
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
        View view = inflater.inflate(R.layout.sn_farmer_genetics, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateSanitaryControlType(view, inflater);
        updateSanitaryControlUsage(view, inflater);
        updateGeneticType(view, inflater);
        updateGeneticUsage(view, inflater);

        return view;
    }

    protected void updateGeneticUsage(View parentView, LayoutInflater inflater) {
        final GeneticsGroup geneticsGroup = ((SenegalSurvey) survey).getGeneticsGroup();
        String geneticUsageText = geneticsGroup.getGeneticsUsage();

        final RadioGroup geneticUsageGroup1 = (RadioGroup) parentView.findViewById(R.id.geneticUsageGroup1);
        final RadioGroup geneticUsageGroup2 = (RadioGroup) parentView.findViewById(R.id.geneticUsageGroup2);

        geneticUsageGroup1.setEnabled(!isModeLocked);
        geneticUsageGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (geneticUsageGroup2 != null && geneticUsageGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        geneticUsageGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    geneticsGroup.setGeneticsUsage(id);
                    ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
                }
            }
        });

        geneticUsageGroup2.setEnabled(!isModeLocked);
        geneticUsageGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (geneticUsageGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        geneticUsageGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    geneticsGroup.setGeneticsUsage(id);
                    ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
                }
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_animal_genetic_improvement_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, geneticUsageGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(GeneticsGroup.geneticIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < answers.length / 2)
                geneticUsageGroup1.addView(rb);
            else
                geneticUsageGroup2.addView(rb);

            if (!TextUtils.isEmpty(geneticUsageText)) {
                if (rb.getTag().equals(geneticUsageText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            geneticUsageGroup1.check(10);
    }

    protected void updateGeneticType(View parentView, LayoutInflater inflater) {
        final GeneticsGroup geneticsGroup = ((SenegalSurvey) survey).getGeneticsGroup();
        final List<String> types = geneticsGroup.getGeneticsType();

        LinearLayout geneticTypeLayout1 = (LinearLayout) parentView.findViewById(R.id.geneticTypeGroup1);
        geneticTypeLayout1.setEnabled(!isModeLocked);

        LinearLayout geneticTypeLayout2 = (LinearLayout) parentView.findViewById(R.id.geneticTypeGroup2);
        geneticTypeLayout2.setEnabled(!isModeLocked);

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

                geneticsGroup.setGeneticsType(types);
                ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, geneticTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesSource.length / 2)
                geneticTypeLayout1.addView(checkBox);
            else
                geneticTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateSanitaryControlUsage(View parentView, LayoutInflater inflater) {
        final GeneticsGroup geneticsGroup = ((SenegalSurvey) survey).getGeneticsGroup();
        String sanitaryControlText = geneticsGroup.getSanitarControlUsage();

        final RadioGroup sanitaryControlGroup1 = (RadioGroup) parentView.findViewById(R.id.sanitaryControlUsageGroup1);
        final RadioGroup sanitaryControlGroup2 = (RadioGroup) parentView.findViewById(R.id.sanitaryControlUsageGroup2);

        sanitaryControlGroup1.setEnabled(!isModeLocked);
        sanitaryControlGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (sanitaryControlGroup2 != null && sanitaryControlGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        sanitaryControlGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    geneticsGroup.setSanitarControlUsage(id);
                    ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
                }
            }
        });

        sanitaryControlGroup2.setEnabled(!isModeLocked);
        sanitaryControlGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (sanitaryControlGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        sanitaryControlGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    geneticsGroup.setSanitarControlUsage(id);
                    ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
                }
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_animal_sanitary_control_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, sanitaryControlGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(GeneticsGroup.controlIdList[index - 1]);
            rb.setId(index * 12);

            if (index - 1 <= answers.length / 2)
                sanitaryControlGroup1.addView(rb);
            else
                sanitaryControlGroup2.addView(rb);

            if (!TextUtils.isEmpty(sanitaryControlText)) {
                if (rb.getTag().equals(sanitaryControlText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            sanitaryControlGroup1.check(12);
    }

    protected void updateSanitaryControlType(View parentView, LayoutInflater inflater) {
        final GeneticsGroup geneticsGroup = ((SenegalSurvey) survey).getGeneticsGroup();
        final List<String> types = geneticsGroup.getSanitarControlType();

        LinearLayout sanitaryControlTypeLayout1 = (LinearLayout) parentView.findViewById(R.id.sanitaryControlTypeGroup1);
        sanitaryControlTypeLayout1.setEnabled(!isModeLocked);

        LinearLayout sanitaryControlTypeLayout2 = (LinearLayout) parentView.findViewById(R.id.sanitaryControlTypeGroup2);
        sanitaryControlTypeLayout2.setEnabled(!isModeLocked);

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

                geneticsGroup.setSanitarControlType(types);
                ((SenegalSurvey) survey).setGeneticsGroup(geneticsGroup);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, sanitaryControlTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesSource.length / 2)
                sanitaryControlTypeLayout1.addView(checkBox);
            else
                sanitaryControlTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
