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
import org.graindataterminal.models.senegal.ServiceGroup;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerService extends BaseFragment {
    private TextView servicesTitle = null;
    private LinearLayout servicesLayout1 = null;
    private LinearLayout servicesLayout2 = null;

    private TextView otherServiceTitle = null;
    private EditText otherService = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerService fragment = new FarmerService();
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
        View view = inflater.inflate(R.layout.sn_farmer_service, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateOtherService(view);
        updateService(view, inflater);

        updateHasOperationEquipment(view, inflater);
        updateHasInfrastructure(view, inflater);

        String hasAgrActivity = ((SenegalSurvey) survey).getHasAgrActivity();
        String hasOperatingEquipment = ((SenegalSurvey) survey).getHasOperatingEquipment();

        boolean needHide = false;

        if (hasAgrActivity == null || SenegalSurvey.answerIdList[1].equals(hasAgrActivity))
            needHide = true;
        else if (hasOperatingEquipment != null && SenegalSurvey.answerIdList[0].equals(hasOperatingEquipment))
            needHide = true;

        if (needHide) {
            ((SenegalSurvey) survey).setServiceGroup(null);

            servicesTitle.setVisibility(View.GONE);
            servicesLayout1.setVisibility(View.GONE);
            servicesLayout2.setVisibility(View.GONE);

            updateOtherServiceVisibility(false, false);
        }

        return view;
    }

    protected void updateService(View parentView, LayoutInflater inflater) {
        final List<String> services = ((SenegalSurvey) survey).getServiceGroup().getServiceList();

        servicesTitle = (TextView) parentView.findViewById(R.id.serviceTitle);
        servicesLayout1 = (LinearLayout) parentView.findViewById(R.id.serviceGroup1);
        servicesLayout1.setEnabled(!isModeLocked);

        servicesLayout2 = (LinearLayout) parentView.findViewById(R.id.serviceGroup2);
        servicesLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] servicesList =  resources.getStringArray(R.array.senegal_service_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !services.contains(id))
                        services.add(id);
                }
                else {
                    services.remove(id);
                }

                ((SenegalSurvey) survey).getServiceGroup().setServiceList(services);
                updateOtherServiceVisibility(services.contains(ServiceGroup.serviceNameIdList[ServiceGroup.serviceNameIdList.length - 1]), true);
            }
        };

        int index = 1;
        for (String activity: servicesList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, servicesLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(ServiceGroup.serviceNameIdList[index - 1]);
            checkBox.setText(activity);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < servicesList.length / 2)
                servicesLayout1.addView(checkBox);
            else
                servicesLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && services.contains(id))
                checkBox.setChecked(true);

            if (services.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateOtherServiceVisibility(services.contains(ServiceGroup.serviceNameIdList[ServiceGroup.serviceNameIdList.length - 1]), false);
    }

    protected void updateOtherServiceVisibility(boolean hasOtherReason, boolean withAnimation) {
        if (hasOtherReason) {
            if (withAnimation) {
                Helper.showView(otherServiceTitle);
                Helper.showView(otherService);
            }
            else {
                otherServiceTitle.setVisibility(View.VISIBLE);
                otherService.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(otherServiceTitle);
                Helper.fadeView(otherService);
            }
            else {
                otherServiceTitle.setVisibility(View.GONE);
                otherService.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherService(View parentView) {
        String otherReasonsText = ((SenegalSurvey) survey).getOtherUncultivatedReasons();

        otherServiceTitle = (TextView) parentView.findViewById(R.id.otherServiceTitle);
        otherService = (EditText) parentView.findViewById(R.id.otherServiceText);
        otherService.setEnabled(!isModeLocked);
        otherService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).getServiceGroup().setOtherService(null);
                else
                    ((SenegalSurvey) survey).getServiceGroup().setOtherService(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherReasonsText))
            otherService.setText(otherReasonsText);
    }

    protected void updateHasOperationEquipment(View parentView, LayoutInflater inflater) {
        String hasEquipmentText = ((SenegalSurvey) survey).getHasOperatingEquipment();

        RadioGroup equipmentGroup = (RadioGroup) parentView.findViewById(R.id.hasOperationEquipmentGroup);
        equipmentGroup.setEnabled(!isModeLocked);
        equipmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasOperatingEquipment(id);
            }
        });

        Resources resources = getResources();
        String[] services =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String service: services) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, equipmentGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(service);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 11);

            equipmentGroup.addView(rb);

            if (!TextUtils.isEmpty(hasEquipmentText)) {
                if (rb.getTag().equals(hasEquipmentText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            equipmentGroup.check(11);
    }

    protected void updateHasInfrastructure(View parentView, LayoutInflater inflater) {
        String hasInfrastructureText = ((SenegalSurvey) survey).getHasInfrastructure();

        RadioGroup infrastructureGroup = (RadioGroup) parentView.findViewById(R.id.hasInfrastructureGroup);
        infrastructureGroup.setEnabled(!isModeLocked);
        infrastructureGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasInfrastructure(id);
            }
        });

        Resources resources = getResources();
        String[] services =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String service: services) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, infrastructureGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(service);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 12);

            infrastructureGroup.addView(rb);

            if (!TextUtils.isEmpty(hasInfrastructureText)) {
                if (rb.getTag().equals(hasInfrastructureText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            infrastructureGroup.check(12);
    }
}
