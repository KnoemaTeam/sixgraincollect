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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerPolicyInfo extends BaseFragment {
    private final static String[] answerIdList = {"oui", "non"};

    private TextView hasPolicyTitle = null;
    private RadioGroup hasPolicyGroup = null;

    private TextView insuranceCompanyTitle = null;
    private EditText insuranceCompany = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerPolicyInfo fragment = new FarmerPolicyInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_policy_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateInsuranceCompanyName(view);
        updateHasPolicy(view, inflater);
        updateAgriculturalInformation(view, inflater);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            String hasCredit = ((SenegalSurvey) survey).getAgriculturalServicesGroup().getHasCredit();
            String answer = answerIdList[0];

            if (!TextUtils.isEmpty(hasCredit) && SenegalSurvey.answerIdList[0].equals(hasCredit))
                answer = answerIdList[1];

            updateInsuranceCompanyNameVisibility(answer, false);
            updateHasInsurancePolicyVisibility(answer, false);

            ((SenegalSurvey) survey).setHasInsurancePolicy(null);
            ((SenegalSurvey) survey).setInsuranceCompanyName(null);
        }
    }

    protected void updateHasPolicy(View parentView, LayoutInflater inflater) {
        String hasPolicyText = ((SenegalSurvey) survey).getHasInsurancePolicy();

        hasPolicyTitle = (TextView) parentView.findViewById(R.id.hasPolicyTitle);
        hasPolicyGroup = (RadioGroup) parentView.findViewById(R.id.hasPolicyGroup);
        hasPolicyGroup.setEnabled(!isModeLocked);
        hasPolicyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasInsurancePolicy(id);
                updateInsuranceCompanyNameVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasPolicyGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            hasPolicyGroup.addView(rb);

            if (!TextUtils.isEmpty(hasPolicyText)) {
                if (rb.getTag().equals(hasPolicyText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasPolicyGroup.check(10);
        else
            updateInsuranceCompanyNameVisibility(hasPolicyText, false);
    }

    protected void updateHasInsurancePolicyVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(SenegalSurvey.answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(hasPolicyTitle);
                Helper.showView(hasPolicyGroup);
            }
            else {
                hasPolicyTitle.setVisibility(View.VISIBLE);
                hasPolicyGroup.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(hasPolicyTitle);
                Helper.fadeView(hasPolicyGroup);
            }
            else {
                hasPolicyTitle.setVisibility(View.GONE);
                hasPolicyGroup.setVisibility(View.GONE);
            }
        }
    }

    protected void updateInsuranceCompanyNameVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(SenegalSurvey.answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(insuranceCompanyTitle);
                Helper.showView(insuranceCompany);
            }
            else {
                insuranceCompanyTitle.setVisibility(View.VISIBLE);
                insuranceCompany.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherReliefType(null);
                insuranceCompany.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(insuranceCompanyTitle);
                Helper.fadeView(insuranceCompany);
            }
            else {
                insuranceCompanyTitle.setVisibility(View.GONE);
                insuranceCompany.setVisibility(View.GONE);
            }
        }
    }

    protected void updateInsuranceCompanyName(View parentView) {
        String nameText = ((SenegalSurvey) survey).getInsuranceCompanyName();

        insuranceCompanyTitle = (TextView) parentView.findViewById(R.id.insuranceCompanyNameTitle);
        insuranceCompany = (EditText) parentView.findViewById(R.id.insuranceCompanyNameText);
        insuranceCompany.setEnabled(!isModeLocked);
        insuranceCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setInsuranceCompanyName(null);
                else
                    ((SenegalSurvey) survey).setInsuranceCompanyName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(nameText))
            insuranceCompany.setText(nameText);
    }

    protected void updateAgriculturalInformation(View parentView, LayoutInflater inflater) {
        final List<String> informationList = ((SenegalSurvey) survey).getAgrInformation();

        LinearLayout agriculturalInformationLayout = (LinearLayout) parentView.findViewById(R.id.agriculturalInformationGroup);
        agriculturalInformationLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] informationSources = resources.getStringArray(R.array.senegal_agricultural_information_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !informationList.contains(id))
                        informationList.add(id);
                }
                else {
                    informationList.remove(id);
                }

                ((SenegalSurvey) survey).setAgrInformation(informationList);
            }
        };

        int index = 1;
        for (String information: informationSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, agriculturalInformationLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(SenegalSurvey.informationSourceIdList[index - 1]);
            checkBox.setText(information);
            checkBox.setOnCheckedChangeListener(event);

            agriculturalInformationLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && informationList.contains(id))
                checkBox.setChecked(true);

            if (informationList.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
