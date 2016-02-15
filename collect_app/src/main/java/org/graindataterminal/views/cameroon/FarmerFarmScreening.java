package org.graindataterminal.views.cameroon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerFarmScreening extends BaseFragment {

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerFarmScreening fragment = new FarmerFarmScreening();
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
        View view = inflater.inflate(R.layout.cm_farmer_farm_screening, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCocoa(view);
        updateYield(view);
        updateOtherCrops(view);

        return view;
    }

    protected void updateCocoa(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmScreening farmScreening = ((CameroonSurvey) survey).getFarmerFarmScreening();
        String cocoaText = farmScreening.getCocoa();

        EditText cocoa = (EditText) parentView.findViewById(R.id.cocoaText);
        cocoa.setEnabled(!isModeLocked);
        cocoa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmScreening.setCocoa(null);
                } else {
                    farmScreening.setCocoa(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerFarmScreening(farmScreening);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cocoaText))
            cocoa.setText(cocoaText);
    }

    protected void updateYield(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmScreening farmScreening = ((CameroonSurvey) survey).getFarmerFarmScreening();
        String yieldText = farmScreening.getYield();

        EditText yield = (EditText) parentView.findViewById(R.id.yieldText);
        yield.setEnabled(!isModeLocked);
        yield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmScreening.setYield(null);
                } else {
                    farmScreening.setYield(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerFarmScreening(farmScreening);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(yieldText))
            yield.setText(yieldText);
    }

    protected void updateOtherCrops(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmScreening farmScreening = ((CameroonSurvey) survey).getFarmerFarmScreening();
        String otherCropsText = farmScreening.getOtherCrops();

        EditText otherCrops = (EditText) parentView.findViewById(R.id.otherCropsText);
        otherCrops.setEnabled(!isModeLocked);
        otherCrops.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmScreening.setOtherCrops(null);
                } else {
                    farmScreening.setOtherCrops(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerFarmScreening(farmScreening);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherCropsText))
            otherCrops.setText(otherCropsText);
    }
}
