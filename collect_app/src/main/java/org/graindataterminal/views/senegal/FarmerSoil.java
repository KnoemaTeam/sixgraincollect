package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerSoil extends BaseFragment {
    private final static String[] soilTypeIdList = {"dior", "deck_dior", "deck_dior", "limoneux", "autre_type_sol"};
    private final static String[] reliefTypeIdList = {"plaine", "bas_fonds", "cuvette", "autre_relief"};
    private final static String[] landTypeIdList = {"mode_propriete", "mode_location", "mode_emprunt"};

    private TextView otherSoilTypeTitle = null;
    private EditText otherSoilType = null;

    private TextView otherReliefTypeTitle = null;
    private EditText otherReliefType = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerSoil fragment = new FarmerSoil();
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
        View view = inflater.inflate(R.layout.sn_farmer_soil, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateOtherSoilType(view);
        updateSoilType(view, inflater);

        updateOtherReliefType(view);
        updateReliefType(view, inflater);

        updateLandTenure(view, inflater);

        if (SenegalSurvey.answerIdList[1].equals(((SenegalSurvey) survey).getHasAgrActivity())) {
            ((SenegalSurvey) survey).setOtherSoilType(null);
            ((SenegalSurvey) survey).setSoilType(null);
            ((SenegalSurvey) survey).setOtherReliefType(null);
            ((SenegalSurvey) survey).setReliefType(null);
            ((SenegalSurvey) survey).setLandTenureType(null);
        }

        return view;
    }

    protected void updateSoilType(View parentView, LayoutInflater inflater) {
        String soilTypeText = ((SenegalSurvey) survey).getSoilType();

        final RadioGroup soilTypeGroup1 = (RadioGroup) parentView.findViewById(R.id.soilTypeGroup1);
        final RadioGroup soilTypeGroup2 = (RadioGroup) parentView.findViewById(R.id.soilTypeGroup2);

        soilTypeGroup1.setEnabled(!isModeLocked);
        soilTypeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (soilTypeGroup2 != null && soilTypeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        soilTypeGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setSoilType(id);
                    updateSoilTypeVisibility(id, true);
                }
            }
        });

        soilTypeGroup2.setEnabled(!isModeLocked);
        soilTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (soilTypeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        soilTypeGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setSoilType(id);
                    updateSoilTypeVisibility(id, true);
                }
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_soil_type_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, soilTypeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(soilTypeIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= types.length / 2)
                soilTypeGroup1.addView(rb);
            else
                soilTypeGroup2.addView(rb);

            if (!TextUtils.isEmpty(soilTypeText)) {
                if (rb.getTag().equals(soilTypeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            soilTypeGroup1.check(10);
        else
            updateSoilTypeVisibility(soilTypeText, false);
    }

    protected void updateSoilTypeVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(soilTypeIdList[soilTypeIdList.length - 1]) == 0) {
            if (withAnimation) {
                Helper.showView(otherSoilTypeTitle);
                Helper.showView(otherSoilType);
            }
            else {
                otherSoilTypeTitle.setVisibility(View.VISIBLE);
                otherSoilType.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherSoilType(null);
                otherSoilType.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherSoilTypeTitle);
                Helper.fadeView(otherSoilType);
            }
            else {
                otherSoilTypeTitle.setVisibility(View.GONE);
                otherSoilType.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherSoilType(View parentView) {
        String otherSoilTypeText = ((SenegalSurvey) survey).getOtherSoilType();

        otherSoilTypeTitle = (TextView) parentView.findViewById(R.id.otherSoilTypeTitle);
        otherSoilType = (EditText) parentView.findViewById(R.id.otherSoilTypeText);
        otherSoilType.setEnabled(!isModeLocked);
        otherSoilType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherSoilType(null);
                else
                    ((SenegalSurvey) survey).setOtherSoilType(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherSoilTypeText))
            otherSoilType.setText(otherSoilTypeText);
    }

    protected void updateReliefType(View parentView, LayoutInflater inflater) {
        String reliefTypeText = ((SenegalSurvey) survey).getReliefType();

        final RadioGroup reliefTypeGroup1 = (RadioGroup) parentView.findViewById(R.id.reliefTypeGroup1);
        final RadioGroup reliefTypeGroup2 = (RadioGroup) parentView.findViewById(R.id.reliefTypeGroup2);

        reliefTypeGroup1.setEnabled(!isModeLocked);
        reliefTypeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (reliefTypeGroup2 != null && reliefTypeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        reliefTypeGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setReliefType(id);
                    updateReliefTypeVisibility(id, true);
                }
            }
        });

        reliefTypeGroup2.setEnabled(!isModeLocked);
        reliefTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (reliefTypeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        reliefTypeGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setReliefType(id);
                    updateReliefTypeVisibility(id, true);
                }
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_relief_type_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, reliefTypeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(reliefTypeIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 < types.length / 2)
                reliefTypeGroup1.addView(rb);
            else
                reliefTypeGroup2.addView(rb);

            if (!TextUtils.isEmpty(reliefTypeText)) {
                if (rb.getTag().equals(reliefTypeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            reliefTypeGroup1.check(11);
        else {
            updateReliefTypeVisibility(reliefTypeText, false);
        }
    }

    protected void updateReliefTypeVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(reliefTypeIdList[reliefTypeIdList.length - 1]) == 0) {
            if (withAnimation) {
                Helper.showView(otherReliefTypeTitle);
                Helper.showView(otherReliefType);
            }
            else {
                otherReliefTypeTitle.setVisibility(View.VISIBLE);
                otherReliefType.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherReliefType(null);
                otherReliefType.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherReliefTypeTitle);
                Helper.fadeView(otherReliefType);
            }
            else {
                otherReliefTypeTitle.setVisibility(View.GONE);
                otherReliefType.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherReliefType(View parentView) {
        String otherReliefTypeText = ((SenegalSurvey) survey).getOtherReliefType();

        otherReliefTypeTitle = (TextView) parentView.findViewById(R.id.otherReliefTypeTitle);
        otherReliefType = (EditText) parentView.findViewById(R.id.otherReliefTypeText);
        otherReliefType.setEnabled(!isModeLocked);
        otherReliefType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherReliefType(null);
                else
                    ((SenegalSurvey) survey).setOtherReliefType(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherReliefTypeText))
            otherReliefType.setText(otherReliefTypeText);
    }

    protected void updateLandTenure(View parentView, LayoutInflater inflater) {
        String landTenureText = ((SenegalSurvey) survey).getLandTenureType();

        RadioGroup landTenureGroup = (RadioGroup) parentView.findViewById(R.id.landTenureGroup);
        landTenureGroup.setEnabled(!isModeLocked);
        landTenureGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setLandTenureType(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_land_tenure_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, landTenureGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(landTypeIdList[index - 1]);
            rb.setId(index * 12);

            landTenureGroup.addView(rb);

            if (!TextUtils.isEmpty(landTenureText)) {
                if (rb.getTag().equals(landTenureText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            landTenureGroup.check(12);
    }
}
