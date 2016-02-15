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

public class FarmerEducation extends BaseFragment {
    private final static String[] educationIdList = {"non_ins", "pri", "sec", "sup", "ins_ara", "alp_lan_nat", "autres"};
    private final static String[] answerIdList = {"oui", "non"};

    private TextView educationTitle = null;
    private RadioGroup educationGroup1 = null;
    private RadioGroup educationGroup2 = null;

    private TextView otherEducationTitle = null;
    private EditText otherEducation = null;

    private TextView nineaNumberTitle = null;
    private EditText nineaNumber = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerEducation fragment = new FarmerEducation();
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
        View view = inflater.inflate(R.layout.sn_farmer_education, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateOtherEducation(view);
        updateEducationLevel(view, inflater);

        updateNineaNumber(view);
        updateNinea(view, inflater);

        if (!((SenegalSurvey) survey).getLegalExpRegime().equals(SenegalSurvey.regimeIdList[0])) {
            educationTitle.setVisibility(View.GONE);
            educationGroup1.setVisibility(View.GONE);
            educationGroup2.setVisibility(View.GONE);

            otherEducationTitle.setVisibility(View.GONE);
            otherEducation.setVisibility(View.GONE);

            if (!isModeLocked) {
                ((SenegalSurvey) survey).setEducationLevel(null);
                ((SenegalSurvey) survey).setOtherEducation(null);
                otherEducation.setText(null);
            }
        }

        return view;
    }

    protected void updateEducationLevel(View parentView, LayoutInflater inflater) {
        String educationLevelText = ((SenegalSurvey) survey).getEducationLevel();

        educationTitle = (TextView) parentView.findViewById(R.id.educationTitle);
        educationGroup1 = (RadioGroup) parentView.findViewById(R.id.educationGroup1);
        educationGroup2 = (RadioGroup) parentView.findViewById(R.id.educationGroup2);

        educationGroup1.setEnabled(!isModeLocked);
        educationGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (educationGroup2 != null && educationGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        educationGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setEducationLevel(id);
                    updateOtherEducationVisibility(id, true);
                }
            }
        });

        educationGroup2.setEnabled(!isModeLocked);
        educationGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (educationGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        educationGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setEducationLevel(id);
                    updateOtherEducationVisibility(id, true);
                }
            }
        });

        Resources resources = getResources();
        String[] educationNames =  resources.getStringArray(R.array.senegal_education_list);

        int index = 1;
        boolean notSet = true;

        for (String educationName: educationNames) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, educationGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(educationName);
            rb.setTag(educationIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= educationNames.length / 2)
                educationGroup1.addView(rb);
            else
                educationGroup2.addView(rb);

            if (!TextUtils.isEmpty(educationLevelText)) {
                if (rb.getTag().equals(educationLevelText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            educationGroup1.check(10);
        else
            updateOtherEducationVisibility(educationLevelText, false);
    }

    protected void updateOtherEducationVisibility(String answer, boolean withAnimation) {
        if (educationIdList[educationIdList.length - 1].compareToIgnoreCase(answer) == 0) {
            if (withAnimation) {
                Helper.showView(otherEducationTitle);
                Helper.showView(otherEducation);
            }
            else {
                otherEducationTitle.setVisibility(View.VISIBLE);
                otherEducation.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherEducation(null);
                otherEducation.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherEducationTitle);
                Helper.fadeView(otherEducation);
            }
            else {
                otherEducationTitle.setVisibility(View.GONE);
                otherEducation.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherEducation(View parentView) {
        String otherEducationText = ((SenegalSurvey) survey).getOtherEducation();

        otherEducationTitle = (TextView) parentView.findViewById(R.id.otherEducationTitle);
        otherEducation = (EditText) parentView.findViewById(R.id.otherEducationText);
        otherEducation.setEnabled(!isModeLocked);
        otherEducation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherEducation(null);
                else
                    ((SenegalSurvey) survey).setOtherEducation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherEducationText))
            otherEducation.setText(otherEducationText);
    }

    protected void updateNinea(View parentView, LayoutInflater inflater) {
        String nineaText = ((SenegalSurvey) survey).getHasNINEA();

        RadioGroup nineaGroup = (RadioGroup) parentView.findViewById(R.id.nineaGroup);
        nineaGroup.setEnabled(!isModeLocked);
        nineaGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasNINEA(id);
                updateNineaVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, nineaGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 11);

            nineaGroup.addView(rb);

            if (!TextUtils.isEmpty(nineaText)) {
                if (rb.getTag().equals(nineaText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            nineaGroup.check(11);
        else
            updateNineaVisibility(nineaText, false);
    }

    protected void updateNineaVisibility(String answer, boolean withAnimation) {
        if (answerIdList[0].compareToIgnoreCase(answer) == 0) {
            if (withAnimation) {
                Helper.showView(nineaNumberTitle);
                Helper.showView(nineaNumber);
            }
            else {
                nineaNumberTitle.setVisibility(View.VISIBLE);
                nineaNumber.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(nineaNumberTitle);
                Helper.fadeView(nineaNumber);
            }
            else {
                nineaNumberTitle.setVisibility(View.GONE);
                nineaNumber.setVisibility(View.GONE);
            }
        }
    }

    protected void updateNineaNumber(View parentView) {
        String nineaNumberText = ((SenegalSurvey) survey).getCodeNINEA();

        nineaNumberTitle = (TextView) parentView.findViewById(R.id.nineaNumberTitle);
        nineaNumber = (EditText) parentView.findViewById(R.id.nineaNumberText);
        nineaNumber.setEnabled(!isModeLocked);
        nineaNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setCodeNINEA(null);
                else
                    ((SenegalSurvey) survey).setCodeNINEA(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(nineaNumberText))
            nineaNumber.setText(nineaNumberText);
    }
}
