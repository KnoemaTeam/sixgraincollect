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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.odk.collect.android.R;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerGeneralInfo;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerEducationLevel extends BaseFragment {
    private static String[] interLanguageIdList = {"inter_english", "inter_french", "none"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerEducationLevel fragment = new FarmerEducationLevel();
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
        View view = inflater.inflate(R.layout.cm_farmer_education, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateInterLanguage(view, inflater);
        updateEducationLevel(view);

        return view;
    }

    protected void updateInterLanguage(View parentView, LayoutInflater inflater) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        final List<String> sources = farmerGeneralInfo.getInterLanguage();

        LinearLayout interLanguageLayout = (LinearLayout) parentView.findViewById(R.id.interLanguageGroup);
        interLanguageLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] languageList = resources.getStringArray(R.array.cameroon_inter_language_list);

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

                farmerGeneralInfo.setInterLanguage(sources);
                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }
        };

        int index = 1;
        for (String language: languageList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, interLanguageLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(interLanguageIdList[index - 1]);
            checkBox.setText(language);
            checkBox.setOnCheckedChangeListener(event);

            interLanguageLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateEducationLevel(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String educationLevelText = farmerGeneralInfo.getEducationLevel();

        EditText educationLevel = (EditText) parentView.findViewById(R.id.educationLevelText);
        educationLevel.setEnabled(!isModeLocked);
        educationLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setEducationLevel(null);
                } else {
                    farmerGeneralInfo.setEducationLevel(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(educationLevelText))
            educationLevel.setText(educationLevelText);
    }
}
