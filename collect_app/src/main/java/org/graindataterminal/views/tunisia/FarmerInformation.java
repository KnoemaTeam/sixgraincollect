package org.graindataterminal.views.tunisia;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerInformation extends BaseFragment {
    private static String[] educationIdList = {"bac", "diplôme_superieur", "diplôme_premier_cycle", "maitrise", "master", "dea", "doctorat", "non_qualifié"};
    private static String[] informationIdList = {"crda", "multimedia", "journal_publication", "internet"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerInformation fragment = new FarmerInformation();
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
        View view = inflater.inflate(R.layout.tn_farmer_information, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateEducationLevel(view, inflater);
        updateInformationSource(view, inflater);

        return view;
    }

    protected void updateEducationLevel(View parentView, LayoutInflater inflater) {
        String educationLevelText = ((TunisiaSurvey) survey).getLevelSkills();

        RadioGroup educationGroup = (RadioGroup) parentView.findViewById(R.id.educationGroup);
        educationGroup.setEnabled(!isModeLocked);
        educationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaSurvey) survey).setLevelSkills(id);
            }
        });

        Resources resources = getResources();
        String[] educationList =  resources.getStringArray(R.array.tunisia_education_source_list);

        int index = 1;
        boolean notSet = true;

        for (String education: educationList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, educationGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(education);
            rb.setTag(educationIdList[index - 1]);
            rb.setId(index * 10);

            educationGroup.addView(rb);

            if (!TextUtils.isEmpty(educationLevelText)) {
                if (rb.getTag().equals(educationLevelText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            educationGroup.check(10);
    }

    protected void updateInformationSource(View parentView, LayoutInflater inflater) {
        final List<String> sources = ((TunisiaSurvey) survey).getInformationSource();

        LinearLayout informationSourcesLayout = (LinearLayout) parentView.findViewById(R.id.informationGroup);
        informationSourcesLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] informationList =  resources.getStringArray(R.array.tunisia_information_source_list);

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

                ((TunisiaSurvey) survey).setInformationSource(sources);
            }
        };

        int index = 1;
        for (String information: informationList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, informationSourcesLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(informationIdList[index - 1]);
            checkBox.setText(information);
            checkBox.setOnCheckedChangeListener(event);

            informationSourcesLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
