package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
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

import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerCompanyInfo extends BaseFragment {
    private final static String[] facilitiesIdList = {"prop", "co_pro", "loc", "autre_prop"};
    private final static String[] waterSourceIdList = {"sde", "forage", "puits", "eau_surface", "autre_source"};
    private final static String[] powerSourceIdList = {"aucune_energie", "electricite", "solaire", "group_elec", "energie_autre"};

    private TextView otherFacilitiesTitle = null;
    private EditText otherFacilities = null;

    private TextView otherWaterSourceTitle = null;
    private EditText otherWaterSource = null;

    private TextView otherPowerSourceTitle = null;
    private EditText otherPowerSource = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCompanyInfo fragment = new FarmerCompanyInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_company_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCompanyCreatedDate(view);

        updateOtherFacilities(view);
        updateFacilitiesType(view, inflater);

        updateOtherWaterSource(view);
        updateWaterSource(view, inflater);

        updateOtherPowerSource(view);
        updatePowerSource(view, inflater);

        return view;
    }

    protected void updateCompanyCreatedDate(View parentView) {
        String createdDateText = ((SenegalSurvey) survey).getCompanyCreatedDate();

        final EditText createdDate = (EditText) parentView.findViewById(R.id.createdDateText);
        createdDate.setEnabled(!isModeLocked);
        createdDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        createdDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((SenegalSurvey) survey).setCompanyCreatedDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(createdDate.getText().toString())) {
                                createdDate.setText(correctDate);
                                createdDate.setSelection(correctDate.length());
                            }

                            ((SenegalSurvey) survey).setCompanyCreatedDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(createdDateText))
            createdDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", createdDateText));
    }

    protected void updateFacilitiesType(View parentView, LayoutInflater inflater) {
        String facilitiesTypeText = ((SenegalSurvey) survey).getFarmType();

        final RadioGroup facilitiesTypeGroup1 = (RadioGroup) parentView.findViewById(R.id.facilitiesTypeGroup1);
        final RadioGroup facilitiesTypeGroup2 = (RadioGroup) parentView.findViewById(R.id.facilitiesTypeGroup2);

        facilitiesTypeGroup1.setEnabled(!isModeLocked);
        facilitiesTypeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (facilitiesTypeGroup2 != null && facilitiesTypeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        facilitiesTypeGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setFarmType(id);
                    updateOtherFacilitiesVisibility(id, true);
                }
            }
        });

        facilitiesTypeGroup2.setEnabled(!isModeLocked);
        facilitiesTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (facilitiesTypeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        facilitiesTypeGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setFarmType(id);
                    updateOtherFacilitiesVisibility(id, true);
                }
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_facilities_types_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, facilitiesTypeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(facilitiesIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < types.length / 2)
                facilitiesTypeGroup1.addView(rb);
            else
                facilitiesTypeGroup2.addView(rb);

            if (!TextUtils.isEmpty(facilitiesTypeText)) {
                if (rb.getTag().equals(facilitiesTypeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            facilitiesTypeGroup1.check(10);
        else {
            updateOtherFacilitiesVisibility(facilitiesTypeText, false);
        }
    }

    protected void updateOtherFacilitiesVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(facilitiesIdList[facilitiesIdList.length - 1]) == 0) {
            if (withAnimation) {
                Helper.showView(otherFacilitiesTitle);
                Helper.showView(otherFacilities);
            }
            else {
                otherFacilitiesTitle.setVisibility(View.VISIBLE);
                otherFacilities.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherFarmType(null);
                otherFacilities.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherFacilitiesTitle);
                Helper.fadeView(otherFacilities);
            }
            else {
                otherFacilitiesTitle.setVisibility(View.GONE);
                otherFacilities.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherFacilities(View parentView) {
        String otherFacilitiesText = ((SenegalSurvey) survey).getOtherFarmType();

        otherFacilitiesTitle = (TextView) parentView.findViewById(R.id.otherFacilitiesTitle);
        otherFacilities = (EditText) parentView.findViewById(R.id.otherFacilitiesText);
        otherFacilities.setEnabled(!isModeLocked);
        otherFacilities.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherFarmType(null);
                else
                    ((SenegalSurvey) survey).setOtherFarmType(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherFacilitiesText))
            otherFacilities.setText(otherFacilitiesText);
    }

    protected void updateWaterSource(View parentView, LayoutInflater inflater) {
        final List<String> sources = ((SenegalSurvey) survey).getExplotationSource();

        LinearLayout waterSourcesLayout1 = (LinearLayout) parentView.findViewById(R.id.waterSourceGroup1);
        waterSourcesLayout1.setEnabled(!isModeLocked);

        LinearLayout waterSourcesLayout2 = (LinearLayout) parentView.findViewById(R.id.waterSourceGroup2);
        waterSourcesLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] waterSources =  resources.getStringArray(R.array.senegal_water_source_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isModeLocked)
                   return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                 updateOtherWaterSourceVisibility(sources.contains(waterSourceIdList[waterSourceIdList.length - 1]), true);
                ((SenegalSurvey) survey).setExplotationSource(sources);
            }
        };

        int index = 1;
        for (String source: waterSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, waterSourcesLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(waterSourceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= waterSources.length / 2)
                waterSourcesLayout1.addView(checkBox);
            else
                waterSourcesLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateOtherWaterSourceVisibility(sources.contains(waterSourceIdList[waterSourceIdList.length - 1]), false);
    }

    protected void updateOtherWaterSourceVisibility(boolean hasOther, boolean withAnimation) {
        if (hasOther) {
            if (withAnimation) {
                Helper.showView(otherWaterSourceTitle);
                Helper.showView(otherWaterSource);
            }
            else {
                otherWaterSourceTitle.setVisibility(View.VISIBLE);
                otherWaterSource.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherExplotationSource(null);
                otherWaterSource.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherWaterSourceTitle);
                Helper.fadeView(otherWaterSource);
            }
            else {
                otherWaterSourceTitle.setVisibility(View.GONE);
                otherWaterSource.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherWaterSource(View parentView) {
        String otherWaterSourceText = ((SenegalSurvey) survey).getOtherExplotationSource();

        otherWaterSourceTitle = (TextView) parentView.findViewById(R.id.otherWaterSourceTitle);
        otherWaterSource = (EditText) parentView.findViewById(R.id.otherWaterSourceText);
        otherWaterSource.setEnabled(!isModeLocked);
        otherWaterSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherExplotationSource(null);
                else
                    ((SenegalSurvey) survey).setOtherExplotationSource(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherWaterSourceText))
            otherWaterSource.setText(otherWaterSourceText);
    }

    protected void updatePowerSource(View parentView, LayoutInflater inflater) {
        final List<String> sources = ((SenegalSurvey) survey).getPowerSource();

        LinearLayout powerSourcesLayout1 = (LinearLayout) parentView.findViewById(R.id.powerSourceGroup1);
        powerSourcesLayout1.setEnabled(!isModeLocked);

        LinearLayout powerSourcesLayout2 = (LinearLayout) parentView.findViewById(R.id.powerSourceGroup2);
        powerSourcesLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] powerSources =  resources.getStringArray(R.array.senegal_power_source_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                updateOtherPowerSourceVisibility(sources.contains(powerSourceIdList[powerSourceIdList.length - 1]), true);
                ((SenegalSurvey) survey).setPowerSource(sources);
            }
        };

        int index = 1;
        for (String source: powerSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, powerSourcesLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(powerSourceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= powerSources.length / 2)
                powerSourcesLayout1.addView(checkBox);
            else
                powerSourcesLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateOtherPowerSourceVisibility(sources.contains(powerSourceIdList[powerSourceIdList.length - 1]), false);
    }

    protected void updateOtherPowerSourceVisibility(boolean hasOther, boolean withAnimation) {
        if (hasOther) {
            if (withAnimation) {
                Helper.showView(otherPowerSourceTitle);
                Helper.showView(otherPowerSource);
            }
            else {
                otherPowerSourceTitle.setVisibility(View.VISIBLE);
                otherPowerSource.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherPowerSource(null);
                otherPowerSource.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherPowerSourceTitle);
                Helper.fadeView(otherPowerSource);
            }
            else {
                otherPowerSourceTitle.setVisibility(View.GONE);
                otherPowerSource.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherPowerSource(View parentView) {
        String otherPowerSourceText = ((SenegalSurvey) survey).getOtherPowerSource();

        otherPowerSourceTitle = (TextView) parentView.findViewById(R.id.otherPowerSourceTitle);
        otherPowerSource = (EditText) parentView.findViewById(R.id.otherPowerSourceText);
        otherPowerSource.setEnabled(!isModeLocked);
        otherPowerSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherPowerSource(null);
                else
                    ((SenegalSurvey) survey).setOtherPowerSource(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherPowerSourceText))
            otherPowerSource.setText(otherPowerSourceText);
    }
}
