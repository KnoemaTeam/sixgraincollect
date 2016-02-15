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
import org.graindataterminal.models.senegal.SupplementationGroup;
import org.graindataterminal.views.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmerDietarySupplement extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "porcin", "volaille", "lapin", "autrre_r"};

    private TextView supplementTypeTitle = null;
    private LinearLayout supplementTypeLayout1 = null;
    private LinearLayout supplementTypeLayout2 = null;

    private TextView supplementProductTitle = null;
    private LinearLayout supplementProductLayout1 = null;
    private LinearLayout supplementProductLayout2 = null;

    private TextView supplementFactoryTitle = null;
    private LinearLayout supplementFactoryLayout1 = null;
    private LinearLayout supplementFactoryLayout2 = null;

    private TextView supplementMineralTitle = null;
    private LinearLayout supplementMineralLayout1 = null;
    private LinearLayout supplementMineralLayout2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerDietarySupplement fragment = new FarmerDietarySupplement();
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
        View view = inflater.inflate(R.layout.sn_farmer_dietary_supplement, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateSupplementMinerals(view, inflater);
        updateSupplementFactory(view, inflater);
        updateSupplementProducts(view, inflater);
        updateSupplementType(view, inflater);
        updateDietarySupplementUsage(view, inflater);

        return view;
    }

    protected void updateDietarySupplementUsage(View parentView, LayoutInflater inflater) {
        final SupplementationGroup supplementationGroup = ((SenegalSurvey) survey).getSupplementationGroup();
        String supplementUsageText = supplementationGroup.getSupplementUsage();

        RadioGroup supplementUsageGroup = (RadioGroup) parentView.findViewById(R.id.supplementUsageGroup);
        supplementUsageGroup.setEnabled(!isModeLocked);
        supplementUsageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                supplementationGroup.setSupplementUsage(id);
                ((SenegalSurvey) survey).setSupplementationGroup(supplementationGroup);

                updateSupplementTypeVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, supplementUsageGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            supplementUsageGroup.addView(rb);

            if (!TextUtils.isEmpty(supplementUsageText)) {
                if (rb.getTag().equals(supplementUsageText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            supplementUsageGroup.check(20);
        else
            updateSupplementTypeVisibility(supplementUsageText, false);
    }

    protected void updateSupplementTypeVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(SenegalSurvey.answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(supplementTypeTitle);
                Helper.showView(supplementTypeLayout1);
                Helper.showView(supplementTypeLayout2);
            }
            else {
                supplementTypeTitle.setVisibility(View.VISIBLE);
                supplementTypeLayout1.setVisibility(View.VISIBLE);
                supplementTypeLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(supplementTypeTitle);
                Helper.fadeView(supplementTypeLayout1);
                Helper.fadeView(supplementTypeLayout2);
            }
            else {
                supplementTypeTitle.setVisibility(View.GONE);
                supplementTypeLayout1.setVisibility(View.GONE);
                supplementTypeLayout2.setVisibility(View.GONE);
            }

            updateСontentVisibility(null, withAnimation);
        }
    }

    protected void updateSupplementType(View parentView, LayoutInflater inflater) {
        final SupplementationGroup supplementationGroup = ((SenegalSurvey) survey).getSupplementationGroup();
        final List<String> types = supplementationGroup.getSupplementType();

        supplementTypeTitle = (TextView) parentView.findViewById(R.id.supplementTypeTitle);
        supplementTypeLayout1 = (LinearLayout) parentView.findViewById(R.id.supplementTypeGroup1);
        supplementTypeLayout1.setEnabled(!isModeLocked);

        supplementTypeLayout2 = (LinearLayout) parentView.findViewById(R.id.supplementTypeGroup2);
        supplementTypeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_complementation_type_list);

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

                supplementationGroup.setSupplementType(types);
                ((SenegalSurvey) survey).setSupplementationGroup(supplementationGroup);

                updateСontentVisibility(types, true);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, supplementTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(SupplementationGroup.compinetationTypeIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesSource.length / 2)
                supplementTypeLayout1.addView(checkBox);
            else
                supplementTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateСontentVisibility(types, false);
    }

    protected void updateСontentVisibility(List<String> answers, boolean withAnimation) {
        if (answers != null && answers.contains(SupplementationGroup.compinetationTypeIdList[1])) {
            if (withAnimation) {
                Helper.showView(supplementProductTitle);
                Helper.showView(supplementProductLayout1);
                Helper.showView(supplementProductLayout2);
            }
            else {
                supplementProductTitle.setVisibility(View.VISIBLE);
                supplementProductLayout1.setVisibility(View.VISIBLE);
                supplementProductLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(supplementProductTitle);
                Helper.fadeView(supplementProductLayout1);
                Helper.fadeView(supplementProductLayout2);
            }
            else {
                supplementProductTitle.setVisibility(View.GONE);
                supplementProductLayout1.setVisibility(View.GONE);
                supplementProductLayout2.setVisibility(View.GONE);
            }
        }

        if (answers != null && answers.contains(SupplementationGroup.compinetationTypeIdList[2])) {
            if (withAnimation) {
                Helper.showView(supplementFactoryTitle);
                Helper.showView(supplementFactoryLayout1);
                Helper.showView(supplementFactoryLayout2);
            }
            else {
                supplementFactoryTitle.setVisibility(View.VISIBLE);
                supplementFactoryLayout1.setVisibility(View.VISIBLE);
                supplementFactoryLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(supplementFactoryTitle);
                Helper.fadeView(supplementFactoryLayout1);
                Helper.fadeView(supplementFactoryLayout2);
            }
            else {
                supplementFactoryTitle.setVisibility(View.GONE);
                supplementFactoryLayout1.setVisibility(View.GONE);
                supplementFactoryLayout2.setVisibility(View.GONE);
            }
        }

        if (answers != null && answers.contains(SupplementationGroup.compinetationTypeIdList[3])) {
            if (withAnimation) {
                Helper.showView(supplementMineralTitle);
                Helper.showView(supplementMineralLayout1);
                Helper.showView(supplementMineralLayout2);
            }
            else {
                supplementMineralTitle.setVisibility(View.VISIBLE);
                supplementMineralLayout1.setVisibility(View.VISIBLE);
                supplementMineralLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(supplementMineralTitle);
                Helper.fadeView(supplementMineralLayout1);
                Helper.fadeView(supplementMineralLayout2);
            }
            else {
                supplementMineralTitle.setVisibility(View.GONE);
                supplementMineralLayout1.setVisibility(View.GONE);
                supplementMineralLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateSupplementProducts(View parentView, LayoutInflater inflater) {
        final SupplementationGroup supplementationGroup = ((SenegalSurvey) survey).getSupplementationGroup();
        final List<String> types = supplementationGroup.getSupplementProduct();

        supplementProductTitle = (TextView) parentView.findViewById(R.id.supplementProductTitle);
        supplementProductLayout1 = (LinearLayout) parentView.findViewById(R.id.supplementProductGroup1);
        supplementProductLayout1.setEnabled(!isModeLocked);

        supplementProductLayout2 = (LinearLayout) parentView.findViewById(R.id.supplementProductGroup2);
        supplementProductLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_4_list);

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

                supplementationGroup.setSupplementProduct(types);
                ((SenegalSurvey) survey).setSupplementationGroup(supplementationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, supplementProductLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                supplementProductLayout1.addView(checkBox);
            else
                supplementProductLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateSupplementFactory(View parentView, LayoutInflater inflater) {
        final SupplementationGroup supplementationGroup = ((SenegalSurvey) survey).getSupplementationGroup();
        final List<String> types = supplementationGroup.getSupplementFactory();

        supplementFactoryTitle = (TextView) parentView.findViewById(R.id.supplementFactoryTitle);
        supplementFactoryLayout1 = (LinearLayout) parentView.findViewById(R.id.supplementFactoryGroup1);
        supplementFactoryLayout1.setEnabled(!isModeLocked);

        supplementFactoryLayout2 = (LinearLayout) parentView.findViewById(R.id.supplementFactoryGroup2);
        supplementFactoryLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_4_list);

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

                supplementationGroup.setSupplementFactory(types);
                ((SenegalSurvey) survey).setSupplementationGroup(supplementationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, supplementFactoryLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                supplementFactoryLayout1.addView(checkBox);
            else
                supplementFactoryLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateSupplementMinerals(View parentView, LayoutInflater inflater) {
        final SupplementationGroup supplementationGroup = ((SenegalSurvey) survey).getSupplementationGroup();
        final List<String> types = supplementationGroup.getSupplementMinerals();

        supplementMineralTitle = (TextView) parentView.findViewById(R.id.supplementMineralsTitle);
        supplementMineralLayout1 = (LinearLayout) parentView.findViewById(R.id.supplementMineralsGroup1);
        supplementMineralLayout1.setEnabled(!isModeLocked);

        supplementMineralLayout2 = (LinearLayout) parentView.findViewById(R.id.supplementMineralsGroup2);
        supplementMineralLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_4_list);

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

                supplementationGroup.setSupplementMinerals(types);
                ((SenegalSurvey) survey).setSupplementationGroup(supplementationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, supplementMineralLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 14);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                supplementMineralLayout1.addView(checkBox);
            else
                supplementMineralLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
