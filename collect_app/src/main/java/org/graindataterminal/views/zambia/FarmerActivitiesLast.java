package org.graindataterminal.views.zambia;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerActivitiesLast extends BaseFragment {
    private static String[] answerIdList = {"yes", "no"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerActivitiesLast fragment = new FarmerActivitiesLast();
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
        View view = inflater.inflate(R.layout.za_farmer_activities_last, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCrop(view, inflater);
        updateLivestock(view, inflater);
        updateFishing(view, inflater);

        return view;
    }

    protected void updateCrop(View parentView, LayoutInflater inflater) {
        String cropText = ((ZambiaSurvey) survey).getCropProductionLast();

        RadioGroup cropGroup = (RadioGroup) parentView.findViewById(R.id.cropGroup);
        cropGroup.setEnabled(!isModeLocked);
        cropGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                ((ZambiaSurvey) survey).setCropProductionLast(rb.getTag().toString());
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, cropGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            cropGroup.addView(rb);

            if (!TextUtils.isEmpty(cropText)) {
                if (rb.getTag().equals(cropText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            cropGroup.check(10);
    }

    protected void updateLivestock(View parentView, LayoutInflater inflater) {
        String livestockText = ((ZambiaSurvey) survey).getLivestockProductionLast();

        RadioGroup livestockGroup = (RadioGroup) parentView.findViewById(R.id.livestockGroup);
        livestockGroup.setEnabled(!isModeLocked);
        livestockGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                ((ZambiaSurvey) survey).setLivestockProductionLast(rb.getTag().toString());
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, livestockGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 11);

            livestockGroup.addView(rb);

            if (!TextUtils.isEmpty(livestockText)) {
                if (rb.getTag().equals(livestockText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            livestockGroup.check(22);
    }

    protected void updateFishing(View parentView, LayoutInflater inflater) {
        String fishingText = ((ZambiaSurvey) survey).getFarmFishingLast();

        RadioGroup fishingGroup = (RadioGroup) parentView.findViewById(R.id.fishingGroup);
        fishingGroup.setEnabled(!isModeLocked);
        fishingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                ((ZambiaSurvey) survey).setFarmFishingLast(rb.getTag().toString());
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, fishingGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 12);

            fishingGroup.addView(rb);

            if (!TextUtils.isEmpty(fishingText)) {
                if (rb.getTag().equals(fishingText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            fishingGroup.check(24);
    }
}
