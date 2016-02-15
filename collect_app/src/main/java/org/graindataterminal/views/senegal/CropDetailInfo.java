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
import org.graindataterminal.models.senegal.SenegalCrop;
import org.odk.collect.android.R;
import org.graindataterminal.models.senegal.SenegalCropGroup;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class CropDetailInfo extends BaseFragment {
    private final static String [] irrigationSourceIdList = {"ir_surface", "aspersion", "goutte_a_goutte", "epandage", "ir_mixte"};
    private final static String [] reasonsIdList = {"autoconsommation", "generateur_revenu"};

    private TextView irrigationSourceTitle = null;
    private RadioGroup irrigationSourceGroup1 = null;
    private RadioGroup irrigationSourceGroup2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropDetailInfo fragment = new CropDetailInfo();
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
        View view = inflater.inflate(R.layout.sn_crop_detail_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = DataHolder.getInstance().getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateIrrigationSource(view, inflater);
        updateWaterSource(view, inflater);
        updateHarvestedAmount(view);
        updateProductionReasons(view, inflater);

        return view;
    }

    protected void updateWaterSource(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String waterSourceText = cropGroup.getWaterSource();

        final RadioGroup waterSourceGroup1 = (RadioGroup) parentView.findViewById(R.id.waterSourceGroup1);
        final RadioGroup waterSourceGroup2 = (RadioGroup) parentView.findViewById(R.id.waterSourceGroup2);

        waterSourceGroup1.setEnabled(!isModeLocked);
        waterSourceGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (waterSourceGroup2 != null && waterSourceGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        waterSourceGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    updateIrrigationSourceVisibility(id, true);

                    cropGroup.setWaterSource(id);
                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                }
            }
        });

        waterSourceGroup2.setEnabled(!isModeLocked);
        waterSourceGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (waterSourceGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        waterSourceGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    updateIrrigationSourceVisibility(id, true);

                    cropGroup.setWaterSource(id);
                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                }
            }
        });

        Resources resources = getResources();
        String[] waterSourceList =  resources.getStringArray(R.array.senegal_crop_water_source_list);

        int index = 1;
        boolean notSet = true;

        for (String source: waterSourceList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, waterSourceGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(source);
            rb.setTag(SenegalCropGroup.waterSourceIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < waterSourceList.length / 2)
                waterSourceGroup1.addView(rb);
            else
                waterSourceGroup2.addView(rb);

            if (!TextUtils.isEmpty(waterSourceText)) {
                if (rb.getTag().equals(waterSourceText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            waterSourceGroup1.check(10);
        else
            updateIrrigationSourceVisibility(waterSourceText, false);
    }

    protected void updateIrrigationSourceVisibility(String answer, boolean withAnimation) {
        if (SenegalCropGroup.waterSourceIdList[SenegalCropGroup.waterSourceIdList.length - 1].equals(answer)) {
            if (withAnimation) {
                Helper.showView(irrigationSourceTitle);
                Helper.showView(irrigationSourceGroup1);
                Helper.showView(irrigationSourceGroup2);
            }
            else {
                irrigationSourceTitle.setVisibility(View.VISIBLE);
                irrigationSourceGroup1.setVisibility(View.VISIBLE);
                irrigationSourceGroup2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(irrigationSourceTitle);
                Helper.fadeView(irrigationSourceGroup1);
                Helper.fadeView(irrigationSourceGroup2);
            }
            else {
                irrigationSourceTitle.setVisibility(View.GONE);
                irrigationSourceGroup1.setVisibility(View.GONE);
                irrigationSourceGroup2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateIrrigationSource(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String irrigationSourceText = cropGroup.getIrrigationSource();

        irrigationSourceTitle = (TextView) parentView.findViewById(R.id.irrigationSourceTitle);
        irrigationSourceGroup1 = (RadioGroup) parentView.findViewById(R.id.irrigationSourceGroup1);
        irrigationSourceGroup2 = (RadioGroup) parentView.findViewById(R.id.irrigationSourceGroup2);

        irrigationSourceGroup1.setEnabled(!isModeLocked);
        irrigationSourceGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (irrigationSourceGroup2 != null && irrigationSourceGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        irrigationSourceGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    cropGroup.setIrrigationSource(id);
                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                }
            }
        });

        irrigationSourceGroup2.setEnabled(!isModeLocked);
        irrigationSourceGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (irrigationSourceGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        irrigationSourceGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    cropGroup.setIrrigationSource(id);
                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                }
            }
        });

        Resources resources = getResources();
        String[] irrigationSourceList =  resources.getStringArray(R.array.senegal_crop_irrigation_source_list);

        int index = 1;
        boolean notSet = true;

        for (String source: irrigationSourceList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, irrigationSourceGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(source);
            rb.setTag(irrigationSourceIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 <= irrigationSourceList.length / 2)
                irrigationSourceGroup1.addView(rb);
            else
                irrigationSourceGroup2.addView(rb);

            if (!TextUtils.isEmpty(irrigationSourceText)) {
                if (rb.getTag().equals(irrigationSourceText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            irrigationSourceGroup1.check(11);
    }

    protected void updateHarvestedAmount(View parentView) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String harvestedAmountText = cropGroup.getHarvestedAmount();

        EditText harvestedAmount = (EditText) parentView.findViewById(R.id.harvestedAmountText);
        harvestedAmount.setEnabled(!isModeLocked);
        harvestedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    cropGroup.setHarvestedAmount(null);
                else
                   cropGroup.setHarvestedAmount(s.toString());

                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(harvestedAmountText))
            harvestedAmount.setText(harvestedAmountText);
    }

    protected void updateProductionReasons(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        final List<String> reasons = cropGroup.getMainMotives();

        LinearLayout productionReasonsLayout = (LinearLayout) parentView.findViewById(R.id.productionReasonsGroup);
        productionReasonsLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] reasonsList =  resources.getStringArray(R.array.senegal_animal_main_production_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !reasons.contains(id))
                        reasons.add(id);
                }
                else {
                    reasons.remove(id);
                }

                cropGroup.setMainMotives(reasons);
                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }
        };

        int index = 1;
        for (String reason: reasonsList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, productionReasonsLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(reasonsIdList[index - 1]);
            checkBox.setText(reason);
            checkBox.setOnCheckedChangeListener(event);

            productionReasonsLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && reasons.contains(id))
                checkBox.setChecked(true);

            if (reasons.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
