package org.graindataterminal.views.cameroon;

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

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerFarmPractice extends BaseFragment {
    private TextView fungicidesUsedInSmallSeasonTitle = null;
    private EditText fungicidesUsedInSmallSeasonText = null;

    private TextView fungicidesUsedNumberInSmallSeasonTitle = null;
    private EditText fungicidesUsedNumberInSmallSeasonText = null;

    private TextView fungicidesUsedInBigSeasonTitle = null;
    private EditText fungicidesUsedInBigSeasonText = null;

    private TextView fungicidesUsedNumberInBigSeasonTitle = null;
    private EditText fungicidesUsedNumberInBigSeasonText = null;

    private TextView insecticideUsedTypeTitle = null;
    private EditText insecticideUsedTypeText = null;

    private TextView insecticideUsedNumberTitle = null;
    private EditText insecticideUsedNumberText = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerFarmPractice fragment = new FarmerFarmPractice();
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
        View view = inflater.inflate(R.layout.cm_farmer_farm_practice, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFungicidesUsed(view, inflater);
        updateFungicidesUsedInSmallSeason(view);
        updateFungicidesUsedNumberInSmallSeason(view);
        updateFungicidesUsedInBigSeason(view);
        updateFungicidesUsedNumberInBigSeason(view);

        updateIsInsecticideUsed(view, inflater);
        updateInsecticideUsed(view);
        updateInsecticideUsedNumber(view);
        updateCocoaPlantation(view);

        return view;
    }

    protected void updateFungicidesUsed(View parentView, LayoutInflater inflater) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String isFungicidesUsedText = farmPractice.getIsFungicidesUsed();

        RadioGroup fungicidesUsedGroup = (RadioGroup) parentView.findViewById(R.id.fungicidesUsedGroup);
        fungicidesUsedGroup.setEnabled(!isModeLocked);
        fungicidesUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                farmPractice.setIsFungicidesUsed(id);
                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, fungicidesUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(CameroonSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            fungicidesUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isFungicidesUsedText)) {
                if (rb.getTag().equals(isFungicidesUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            fungicidesUsedGroup.check(10);
    }

    protected void updateFungicidesUsedInSmallSeason(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String fungicidesUsedText = farmPractice.getUsedFungicidesInSmallSeason();

        fungicidesUsedInSmallSeasonTitle = (TextView) parentView.findViewById(R.id.fungicidesUsedInSmallSeasonTitle);
        fungicidesUsedInSmallSeasonText = (EditText) parentView.findViewById(R.id.fungicidesUsedInSmallSeasonText);
        fungicidesUsedInSmallSeasonText.setEnabled(!isModeLocked);
        fungicidesUsedInSmallSeasonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setUsedFungicidesInSmallSeason(null);
                else
                    farmPractice.setUsedFungicidesInSmallSeason(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fungicidesUsedText))
            fungicidesUsedInSmallSeasonText.setText(fungicidesUsedText);
    }

    protected void updateFungicidesUsedNumberInSmallSeason(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String fungicidesUsedText = farmPractice.getFungicidesNumberInSmallSeason();

        fungicidesUsedNumberInSmallSeasonTitle = (TextView) parentView.findViewById(R.id.fungicidesUsedNumberInSmallSeasonTitle);
        fungicidesUsedNumberInSmallSeasonText = (EditText) parentView.findViewById(R.id.fungicidesUsedNumberInSmallSeasonText);
        fungicidesUsedNumberInSmallSeasonText.setEnabled(!isModeLocked);
        fungicidesUsedNumberInSmallSeasonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setFungicidesNumberInSmallSeason(null);
                else
                    farmPractice.setFungicidesNumberInSmallSeason(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fungicidesUsedText))
            fungicidesUsedNumberInSmallSeasonText.setText(fungicidesUsedText);
    }

    protected void updateFungicidesUsedInBigSeason(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String fungicidesUsedText = farmPractice.getUsedFungicidesInBigSeason();

        fungicidesUsedInBigSeasonTitle = (TextView) parentView.findViewById(R.id.fungicidesUsedInBigSeasonTitle);
        fungicidesUsedInBigSeasonText = (EditText) parentView.findViewById(R.id.fungicidesUsedInBigSeasonText);
        fungicidesUsedInBigSeasonText.setEnabled(!isModeLocked);
        fungicidesUsedInBigSeasonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setUsedFungicidesInBigSeason(null);
                else
                    farmPractice.setUsedFungicidesInBigSeason(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fungicidesUsedText))
            fungicidesUsedInBigSeasonText.setText(fungicidesUsedText);
    }

    protected void updateFungicidesUsedNumberInBigSeason(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String fungicidesUsedText = farmPractice.getFungicidesNumberInBigSeason();

        fungicidesUsedNumberInBigSeasonTitle = (TextView) parentView.findViewById(R.id.fungicidesUsedNumberInBigSeasonTitle);
        fungicidesUsedNumberInBigSeasonText = (EditText) parentView.findViewById(R.id.fungicidesUsedNumberInBigSeasonText);
        fungicidesUsedNumberInBigSeasonText.setEnabled(!isModeLocked);
        fungicidesUsedNumberInBigSeasonText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setFungicidesNumberInBigSeason(null);
                else
                    farmPractice.setFungicidesNumberInBigSeason(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fungicidesUsedText))
            fungicidesUsedNumberInBigSeasonText.setText(fungicidesUsedText);
    }

    protected void updateIsInsecticideUsed(View parentView, LayoutInflater inflater) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String isInsecticideUsedText = farmPractice.getIsInsecticideUsed();

        RadioGroup insecticideUsedGroup = (RadioGroup) parentView.findViewById(R.id.insecticideUsedGroup);
        insecticideUsedGroup.setEnabled(!isModeLocked);
        insecticideUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                farmPractice.setIsInsecticideUsed(id);
                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, insecticideUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(CameroonSurvey.answerIdList[index - 1]);
            rb.setId(index * 11);

            insecticideUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isInsecticideUsedText)) {
                if (rb.getTag().equals(isInsecticideUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            insecticideUsedGroup.check(11);
    }

    protected void updateInsecticideUsed(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String insecticideUsedText = farmPractice.getUsedInsecticide();

        insecticideUsedTypeTitle = (TextView) parentView.findViewById(R.id.insecticideUsedTypeTitle);
        insecticideUsedTypeText = (EditText) parentView.findViewById(R.id.insecticideUsedTypeText);
        insecticideUsedTypeText.setEnabled(!isModeLocked);
        insecticideUsedTypeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setUsedInsecticide(null);
                else
                    farmPractice.setUsedInsecticide(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(insecticideUsedText))
            insecticideUsedTypeText.setText(insecticideUsedText);
    }

    protected void updateInsecticideUsedNumber(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String insecticideUsedText = farmPractice.getInsecticideCount();

        insecticideUsedNumberTitle = (TextView) parentView.findViewById(R.id.insecticideUsedNumberTitle);
        insecticideUsedNumberText = (EditText) parentView.findViewById(R.id.insecticideUsedNumberText);
        insecticideUsedNumberText.setEnabled(!isModeLocked);
        insecticideUsedNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setInsecticideCount(null);
                else
                    farmPractice.setInsecticideCount(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(insecticideUsedText))
            insecticideUsedNumberText.setText(insecticideUsedText);
    }

    protected void updateCocoaPlantation(View parentView) {
        final org.graindataterminal.models.cameroon.FarmerFarmPractice farmPractice = ((CameroonSurvey) survey).getFarmerFarmPractice();
        String cocoaIssueText = farmPractice.getMainCocoaIssue();

        EditText cocoaPlantationText = (EditText) parentView.findViewById(R.id.cocoaPlantationText);
        cocoaPlantationText.setEnabled(!isModeLocked);
        cocoaPlantationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmPractice.setMainCocoaIssue(null);
                else
                    farmPractice.setMainCocoaIssue(s.toString());

                ((CameroonSurvey) survey).setFarmerFarmPractice(farmPractice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cocoaIssueText))
            cocoaPlantationText.setText(cocoaIssueText);
    }
}
