package org.graindataterminal.views.cameroon;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerSustainability;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerCppSustainability extends BaseFragment {
    private final static String[] cppMainReasonIdList = {"not_aware", "not_available", "not_comfortable", "too_expensive", "other"};

    private TextView cppMainReasonTitle = null;
    private RadioGroup cppMainReasonGroup1 = null;
    private RadioGroup cppMainReasonGroup2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCppSustainability fragment = new FarmerCppSustainability();
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
        View view = inflater.inflate(R.layout.cm_farmer_cpp_sustainability, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCppMainReason(view, inflater);
        updateCppProtectiveGear(view, inflater);

        return view;
    }

    protected void updateControlVisibility(String answer, boolean withAnimation) {
        if (FarmerSustainability.cppProtectiveGearIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(cppMainReasonTitle);
                Helper.showView(cppMainReasonGroup1);
                Helper.showView(cppMainReasonGroup2);
            }
            else {
                cppMainReasonTitle.setVisibility(View.VISIBLE);
                cppMainReasonGroup1.setVisibility(View.VISIBLE);
                cppMainReasonGroup2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(cppMainReasonTitle);
                Helper.fadeView(cppMainReasonGroup1);
                Helper.fadeView(cppMainReasonGroup2);
            }
            else {
                cppMainReasonTitle.setVisibility(View.GONE);
                cppMainReasonGroup1.setVisibility(View.GONE);
                cppMainReasonGroup2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateCppProtectiveGear(View parentView, LayoutInflater inflater) {
        final FarmerSustainability cppSustainability = ((CameroonSurvey) survey).getFarmerSustainability();
        String protectiveGearText = cppSustainability.getProtectiveGear();

        final RadioGroup cppProtectiveGearGroup1 = (RadioGroup) parentView.findViewById(R.id.cppProtectiveGearGroup1);
        final RadioGroup cppProtectiveGearGroup2 = (RadioGroup) parentView.findViewById(R.id.cppProtectiveGearGroup2);

        cppProtectiveGearGroup1.setEnabled(!isModeLocked);
        cppProtectiveGearGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cppProtectiveGearGroup2 != null && cppProtectiveGearGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        cppProtectiveGearGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    updateControlVisibility(id, true);

                    if (!FarmerSustainability.cppProtectiveGearIdList[0].equals(id))
                        cppMainReasonGroup1.check(11);

                    cppSustainability.setProtectiveGear(id);
                    ((CameroonSurvey) survey).setFarmerSustainability(cppSustainability);
                }
            }
        });

        cppProtectiveGearGroup2.setEnabled(!isModeLocked);
        cppProtectiveGearGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cppProtectiveGearGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        cppProtectiveGearGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    updateControlVisibility(id, true);

                    if (!FarmerSustainability.cppProtectiveGearIdList[0].equals(id))
                        cppMainReasonGroup1.check(11);

                    cppSustainability.setProtectiveGear(id);
                    ((CameroonSurvey) survey).setFarmerSustainability(cppSustainability);
                }
            }
        });

        Resources resources = getResources();
        String[] sourceList =  resources.getStringArray(R.array.cameroon_cpp_order_usage_list);

        int index = 1;
        boolean notSet = true;

        for (String source: sourceList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, cppProtectiveGearGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(source);
            rb.setTag(FarmerSustainability.cppProtectiveGearIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= sourceList.length / 2)
                cppProtectiveGearGroup1.addView(rb);
            else
                cppProtectiveGearGroup2.addView(rb);

            if (!TextUtils.isEmpty(protectiveGearText)) {
                if (rb.getTag().equals(protectiveGearText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            cppProtectiveGearGroup1.check(10);
        else
            updateControlVisibility(protectiveGearText, false);

    }

    protected void updateCppMainReason(View parentView, LayoutInflater inflater) {
        final FarmerSustainability cppSustainability = ((CameroonSurvey) survey).getFarmerSustainability();
        String mainReasonText = cppSustainability.getMainReason();

        cppMainReasonTitle = (TextView) parentView.findViewById(R.id.cppMainReasonTitle);
        cppMainReasonGroup1 = (RadioGroup) parentView.findViewById(R.id.cppMainReasonGroup1);
        cppMainReasonGroup2 = (RadioGroup) parentView.findViewById(R.id.cppMainReasonGroup2);

        cppMainReasonGroup1.setEnabled(!isModeLocked);
        cppMainReasonGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cppMainReasonGroup2 != null && cppMainReasonGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        cppMainReasonGroup2.clearCheck();

                    cppSustainability.setMainReason(rb.getTag().toString());
                    ((CameroonSurvey) survey).setFarmerSustainability(cppSustainability);
                }
            }
        });

        cppMainReasonGroup2.setEnabled(!isModeLocked);
        cppMainReasonGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cppMainReasonGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        cppMainReasonGroup1.clearCheck();

                    cppSustainability.setMainReason(rb.getTag().toString());
                    ((CameroonSurvey) survey).setFarmerSustainability(cppSustainability);
                }
            }
        });

        Resources resources = getResources();
        String[] sourceList =  resources.getStringArray(R.array.cameroon_cocoa_main_reason_list);

        int index = 1;
        boolean notSet = true;

        for (String source: sourceList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, cppMainReasonGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(source);
            rb.setTag(cppMainReasonIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 <= sourceList.length / 2)
                cppMainReasonGroup1.addView(rb);
            else
                cppMainReasonGroup2.addView(rb);

            if (!TextUtils.isEmpty(mainReasonText)) {
                if (rb.getTag().equals(mainReasonText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            cppMainReasonGroup1.check(11);
    }
}
