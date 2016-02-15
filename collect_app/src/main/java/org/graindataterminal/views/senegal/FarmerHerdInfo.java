package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Context;
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

import org.graindataterminal.adapters.BaseDelegate;
import org.graindataterminal.controllers.FarmersPager;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.DistinguishingFeatureGroup;
import org.graindataterminal.models.senegal.FarmingPracticeGroup;
import org.graindataterminal.models.senegal.SafetyStockGroup;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerHerdInfo extends BaseFragment {
    private TextView foragePracticeCropsTitle = null;
    private TextView foragePracticeTitle = null;
    private RadioGroup foragePracticeGroup = null;

    private TextView opeTitle = null;
    private RadioGroup opeGroup = null;

    private TextView organizationNameTitle = null;
    private EditText organizationName = null;

    private TextView securingLivestockTitle = null;
    private TextView insurancePolicyTitle = null;
    private RadioGroup hasInsurancePolicy = null;

    private TextView insuranceCompanyNameTitle = null;
    private EditText insuranceCompanyName = null;

    private TextView microchipsTitle = null;
    private RadioGroup microchipsGroup = null;

    private TextView identificationMethodsTitle = null;
    private LinearLayout identificationMethods = null;

    private TextView otherIdentificationMethodTitle = null;
    private EditText otherIdentificationMethod = null;

    private BaseDelegate delegate = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerHerdInfo fragment = new FarmerHerdInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_herd_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateHasForagePractice(view, inflater);

        updateOrganizationName(view);
        updateHasProfessionOrganization(view, inflater);

        updateInsuranceCompanyName(view);
        updateHasInsurancePolicy(view, inflater);

        updateOtherIdentificationMethod(view);
        updateIdentificationMethods(view, inflater);
        updateHasMicrochipsPractice(view, inflater);

        updateHasLivestockOperation(view, inflater);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            delegate = (FarmersPager) context;
    }

    protected void updateHasLivestockOperation(View parentView, LayoutInflater inflater) {
        final FarmingPracticeGroup practiceGroup =  ((SenegalSurvey) survey).getFarmingPracticeGroup();
        String hasOperationText = practiceGroup.getIsMadeFarming();

        RadioGroup operationGroup = (RadioGroup) parentView.findViewById(R.id.hasLivestockOperationGroup);
        operationGroup.setEnabled(!isModeLocked);
        operationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                practiceGroup.setIsMadeFarming(id);
                ((SenegalSurvey) survey).setFarmingPracticeGroup(practiceGroup);

                if (delegate != null)
                    delegate.onControlStateChanged(SenegalSurvey.answerIdList[1].equals(id));

                updateLivestockOperationVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, operationGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            operationGroup.addView(rb);

            if (!TextUtils.isEmpty(hasOperationText)) {
                if (rb.getTag().equals(hasOperationText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            operationGroup.check(10);
        else if (delegate != null) {
            delegate.onControlStateChanged(SenegalSurvey.answerIdList[1].equals(hasOperationText));
            updateLivestockOperationVisibility(hasOperationText, false);
        }
    }

    protected void updateLivestockOperationVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(foragePracticeCropsTitle);
                Helper.showView(foragePracticeTitle);
                Helper.showView(foragePracticeGroup);
                Helper.showView(opeTitle);
                Helper.showView(opeGroup);
                Helper.showView(organizationNameTitle);
                Helper.showView(organizationName);
                Helper.showView(securingLivestockTitle);
                Helper.showView(insurancePolicyTitle);
                Helper.showView(hasInsurancePolicy);
                Helper.showView(insuranceCompanyNameTitle);
                Helper.showView(insuranceCompanyName);
                Helper.showView(microchipsTitle);
                Helper.showView(microchipsGroup);
                Helper.showView(identificationMethodsTitle);
                Helper.showView(identificationMethods);
                Helper.showView(otherIdentificationMethodTitle);
                Helper.showView(otherIdentificationMethod);
            }
            else {
                foragePracticeCropsTitle.setVisibility(View.VISIBLE);
                foragePracticeTitle.setVisibility(View.VISIBLE);
                foragePracticeGroup.setVisibility(View.VISIBLE);
                opeTitle.setVisibility(View.VISIBLE);
                opeGroup.setVisibility(View.VISIBLE);
                organizationNameTitle.setVisibility(View.VISIBLE);
                organizationName.setVisibility(View.VISIBLE);
                securingLivestockTitle.setVisibility(View.VISIBLE);
                insurancePolicyTitle.setVisibility(View.VISIBLE);
                hasInsurancePolicy.setVisibility(View.VISIBLE);
                insuranceCompanyNameTitle.setVisibility(View.VISIBLE);
                insuranceCompanyName.setVisibility(View.VISIBLE);
                microchipsTitle.setVisibility(View.VISIBLE);
                microchipsGroup.setVisibility(View.VISIBLE);
                identificationMethodsTitle.setVisibility(View.VISIBLE);
                identificationMethods.setVisibility(View.VISIBLE);
                otherIdentificationMethodTitle.setVisibility(View.VISIBLE);
                otherIdentificationMethod.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(foragePracticeCropsTitle);
                Helper.fadeView(foragePracticeTitle);
                Helper.fadeView(foragePracticeGroup);
                Helper.fadeView(opeTitle);
                Helper.fadeView(opeGroup);
                Helper.fadeView(organizationNameTitle);
                Helper.fadeView(organizationName);
                Helper.fadeView(securingLivestockTitle);
                Helper.fadeView(insurancePolicyTitle);
                Helper.fadeView(hasInsurancePolicy);
                Helper.fadeView(insuranceCompanyNameTitle);
                Helper.fadeView(insuranceCompanyName);
                Helper.fadeView(microchipsTitle);
                Helper.fadeView(microchipsGroup);
                Helper.fadeView(identificationMethodsTitle);
                Helper.fadeView(identificationMethods);
                Helper.fadeView(otherIdentificationMethodTitle);
                Helper.fadeView(otherIdentificationMethod);
            }
            else {
                foragePracticeCropsTitle.setVisibility(View.GONE);
                foragePracticeTitle.setVisibility(View.GONE);
                foragePracticeGroup.setVisibility(View.GONE);
                opeTitle.setVisibility(View.GONE);
                opeGroup.setVisibility(View.GONE);
                organizationNameTitle.setVisibility(View.GONE);
                organizationName.setVisibility(View.GONE);
                securingLivestockTitle.setVisibility(View.GONE);
                insurancePolicyTitle.setVisibility(View.GONE);
                hasInsurancePolicy.setVisibility(View.GONE);
                insuranceCompanyNameTitle.setVisibility(View.GONE);
                insuranceCompanyName.setVisibility(View.GONE);
                microchipsTitle.setVisibility(View.GONE);
                microchipsGroup.setVisibility(View.GONE);
                identificationMethodsTitle.setVisibility(View.GONE);
                identificationMethods.setVisibility(View.GONE);
                otherIdentificationMethodTitle.setVisibility(View.GONE);
                otherIdentificationMethod.setVisibility(View.GONE);
            }
        }
    }

    protected void updateHasForagePractice(View parentView, LayoutInflater inflater) {
        final DistinguishingFeatureGroup featureGroup = ((SenegalSurvey) survey).getFeatureGroup();
        String hasForagePracticeText = featureGroup.getIsForagePractice();

        foragePracticeCropsTitle = (TextView) parentView.findViewById(R.id.practiceForageCropsTitle);
        foragePracticeTitle = (TextView) parentView.findViewById(R.id.foragePracticeTitle);
        foragePracticeGroup = (RadioGroup) parentView.findViewById(R.id.foragePracticeGroup);
        foragePracticeGroup.setEnabled(!isModeLocked);
        foragePracticeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                featureGroup.setIsForagePractice(id);
                ((SenegalSurvey) survey).setFeatureGroup(featureGroup);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, foragePracticeGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 11);

            foragePracticeGroup.addView(rb);

            if (!TextUtils.isEmpty(hasForagePracticeText)) {
                if (rb.getTag().equals(hasForagePracticeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            foragePracticeGroup.check(11);
    }

    protected void updateHasProfessionOrganization(View parentView, LayoutInflater inflater) {
        final DistinguishingFeatureGroup featureGroup = ((SenegalSurvey) survey).getFeatureGroup();
        String hasOpeText = featureGroup.getOpe();

        opeTitle = (TextView) parentView.findViewById(R.id.hasProfessionalOrganizationTitle);
        opeGroup = (RadioGroup) parentView.findViewById(R.id.hasProfessionalOrganizationGroup);
        opeGroup.setEnabled(!isModeLocked);
        opeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                featureGroup.setOpe(id);
                ((SenegalSurvey) survey).setFeatureGroup(featureGroup);
                updateOrganizationVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_breeding_organization_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, opeGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(DistinguishingFeatureGroup.opeIdList[index - 1]);
            rb.setId(index * 12);

            opeGroup.addView(rb);

            if (!TextUtils.isEmpty(hasOpeText)) {
                if (rb.getTag().equals(hasOpeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            opeGroup.check(12);
        else
            updateOrganizationVisibility(hasOpeText, false);
    }

    protected void updateOrganizationVisibility(String answer, boolean withAnimation) {
        if (DistinguishingFeatureGroup.opeIdList[DistinguishingFeatureGroup.opeIdList.length - 1].equals(answer)) {
            if (withAnimation) {
                Helper.showView(organizationNameTitle);
                Helper.showView(organizationName);
            }
            else {
                organizationNameTitle.setVisibility(View.VISIBLE);
                organizationName.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(organizationNameTitle);
                Helper.fadeView(organizationName);
            }
            else {
                organizationNameTitle.setVisibility(View.GONE);
                organizationName.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOrganizationName(View parentView) {
        final DistinguishingFeatureGroup featureGroup = ((SenegalSurvey) survey).getFeatureGroup();
        String organizationNameText = featureGroup.getPeasantOrganizationName();

        organizationNameTitle = (TextView) parentView.findViewById(R.id.professionalOrganizationNameTitle);
        organizationName = (EditText) parentView.findViewById(R.id.professionalOrganizationNameText);
        organizationName.setEnabled(!isModeLocked);
        organizationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    featureGroup.setPeasantOrganizationName(null);
                else
                    featureGroup.setPeasantOrganizationName(s.toString());

                ((SenegalSurvey) survey).setFeatureGroup(featureGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(organizationNameText))
            organizationName.setText(organizationNameText);
    }

    protected void updateHasInsurancePolicy(View parentView, LayoutInflater inflater) {
        final SafetyStockGroup safetyStockGroup = ((SenegalSurvey) survey).getSafetyStockGroup();
        String hasInsurancePolicyText = safetyStockGroup.getIsLivestockInsurance();

        securingLivestockTitle = (TextView) parentView.findViewById(R.id.securingLivestockTitle);
        insurancePolicyTitle = (TextView) parentView.findViewById(R.id.hasInsurancePolicyTitle);
        hasInsurancePolicy = (RadioGroup) parentView.findViewById(R.id.hasInsurancePolicyGroup);
        hasInsurancePolicy.setEnabled(!isModeLocked);
        hasInsurancePolicy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                safetyStockGroup.setIsLivestockInsurance(id);
                ((SenegalSurvey) survey).setSafetyStockGroup(safetyStockGroup);
                updateInsuranceCompanyVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasInsurancePolicy, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 13);

            hasInsurancePolicy.addView(rb);

            if (!TextUtils.isEmpty(hasInsurancePolicyText)) {
                if (rb.getTag().equals(hasInsurancePolicyText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasInsurancePolicy.check(13);
        else
            updateInsuranceCompanyVisibility(hasInsurancePolicyText, false);
    }

    protected void updateInsuranceCompanyVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(insuranceCompanyNameTitle);
                Helper.showView(insuranceCompanyName);
            }
            else {
                insuranceCompanyNameTitle.setVisibility(View.VISIBLE);
                insuranceCompanyName.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(insuranceCompanyNameTitle);
                Helper.fadeView(insuranceCompanyName);
            }
            else {
                insuranceCompanyNameTitle.setVisibility(View.GONE);
                insuranceCompanyName.setVisibility(View.GONE);
            }
        }
    }

    protected void updateInsuranceCompanyName(View parentView) {
        final SafetyStockGroup safetyStockGroup = ((SenegalSurvey) survey).getSafetyStockGroup();
        String companyNameText = safetyStockGroup.getLivestockInsuranceCompanyName();

        insuranceCompanyNameTitle = (TextView) parentView.findViewById(R.id.insuranceCompanyNameTitle);
        insuranceCompanyName = (EditText) parentView.findViewById(R.id.insuranceCompanyNameText);
        insuranceCompanyName.setEnabled(!isModeLocked);
        insuranceCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    safetyStockGroup.setLivestockInsuranceCompanyName(null);
                else
                    safetyStockGroup.setLivestockInsuranceCompanyName(s.toString());

                ((SenegalSurvey) survey).setSafetyStockGroup(safetyStockGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(companyNameText))
            insuranceCompanyName.setText(companyNameText);
    }

    protected void updateHasMicrochipsPractice(View parentView, LayoutInflater inflater) {
        final SafetyStockGroup safetyStockGroup = ((SenegalSurvey) survey).getSafetyStockGroup();
        String hasMicrochipPracticeText = safetyStockGroup.getHasLivestockIdentification();

        microchipsTitle = (TextView) parentView.findViewById(R.id.hasPracticeMicrochipsTitle);
        microchipsGroup = (RadioGroup) parentView.findViewById(R.id.hasPracticeMicrochipsGroup);
        microchipsGroup.setEnabled(!isModeLocked);
        microchipsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                safetyStockGroup.setHasLivestockIdentification(id);
                ((SenegalSurvey) survey).setSafetyStockGroup(safetyStockGroup);
                updateIdentificationMethodsVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, microchipsGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 14);

            microchipsGroup.addView(rb);

            if (!TextUtils.isEmpty(hasMicrochipPracticeText)) {
                if (rb.getTag().equals(hasMicrochipPracticeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            microchipsGroup.check(14);
        else
            updateIdentificationMethodsVisibility(hasMicrochipPracticeText, false);
    }

    protected void updateIdentificationMethodsVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(identificationMethodsTitle);
                Helper.showView(identificationMethods);
            }
            else {
                identificationMethodsTitle.setVisibility(View.VISIBLE);
                identificationMethods.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(identificationMethodsTitle);
                Helper.fadeView(identificationMethods);
            }
            else {
                identificationMethodsTitle.setVisibility(View.GONE);
                identificationMethods.setVisibility(View.GONE);
            }

            updateOtherInsuranceCompanyVisibility(false, withAnimation);
        }
    }

    protected void updateIdentificationMethods(View parentView, LayoutInflater inflater) {
        final SafetyStockGroup safetyStockGroup = ((SenegalSurvey) survey).getSafetyStockGroup();
        final List<String> methods = safetyStockGroup.getLivestockIdentificationMethods();

        identificationMethodsTitle = (TextView) parentView.findViewById(R.id.identificationMethodsTitle);

        identificationMethods = (LinearLayout) parentView.findViewById(R.id.identificationMethodsGroup);
        identificationMethods.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] constraintSources = resources.getStringArray(R.array.senegal_identification_method_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !methods.contains(id))
                        methods.add(id);
                }
                else {
                    methods.remove(id);
                }

                safetyStockGroup.setLivestockIdentificationMethods(methods);
                ((SenegalSurvey) survey).setSafetyStockGroup(safetyStockGroup);
                updateOtherInsuranceCompanyVisibility(methods.contains(SenegalSurvey.identificationIdList[SenegalSurvey.identificationIdList.length - 1]), true);
            }
        };

        int index = 1;
        for (String constraint: constraintSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, identificationMethods, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 15);
            checkBox.setTag(SenegalSurvey.identificationIdList[index - 1]);
            checkBox.setText(constraint);
            checkBox.setOnCheckedChangeListener(event);

            identificationMethods.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && methods.contains(id))
                checkBox.setChecked(true);

            if (methods.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateOtherInsuranceCompanyVisibility(methods.contains(SenegalSurvey.identificationIdList[SenegalSurvey.identificationIdList.length - 1]), false);
    }

    protected void updateOtherInsuranceCompanyVisibility(boolean hasOther, boolean withAnimation) {
        if (hasOther) {
            if (withAnimation) {
                Helper.showView(otherIdentificationMethodTitle);
                Helper.showView(otherIdentificationMethod);
            }
            else {
                otherIdentificationMethodTitle.setVisibility(View.VISIBLE);
                otherIdentificationMethod.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(otherIdentificationMethodTitle);
                Helper.fadeView(otherIdentificationMethod);
            }
            else {
                otherIdentificationMethodTitle.setVisibility(View.GONE);
                otherIdentificationMethod.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherIdentificationMethod(View parentView) {
        final SafetyStockGroup safetyStockGroup = ((SenegalSurvey) survey).getSafetyStockGroup();
        String methodText = safetyStockGroup.getOtherLivestockIdentification();

        otherIdentificationMethodTitle = (TextView) parentView.findViewById(R.id.otherIdentificationMethodTitle);
        otherIdentificationMethod = (EditText) parentView.findViewById(R.id.otherIdentificationMethodText);
        otherIdentificationMethod.setEnabled(!isModeLocked);
        otherIdentificationMethod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    safetyStockGroup.setOtherLivestockIdentification(null);
                else
                    safetyStockGroup.setOtherLivestockIdentification(s.toString());

                ((SenegalSurvey) survey).setSafetyStockGroup(safetyStockGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(methodText))
            otherIdentificationMethod.setText(methodText);
    }
}
