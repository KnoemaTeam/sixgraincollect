package org.graindataterminal.views.tunisia;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaCrop;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FieldSources extends BaseFragment {
    private static String[] waterSourceIdList = {"pluviale", "irrigation"};
    private static String[] motiveSourceIdList = {"consommation_domestique", "génération_de_revenus"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldSources fragment = new FieldSources();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            screenIndex = bundle.getInt(SCREEN_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tn_field_sources, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateWaterSource(view, inflater);
        updateMotiveSource(view, inflater);

        return view;
    }

    protected void updateWaterSource(View parentView, LayoutInflater inflater) {
        String waterSourceText = ((TunisiaCrop) crop).getWaterSource();

        RadioGroup waterSourceGroup = (RadioGroup) parentView.findViewById(R.id.waterSourceGroup);
        waterSourceGroup.setEnabled(!isModeLocked);
        waterSourceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaCrop) crop).setWaterSource(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.tunisia_water_source_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, waterSourceGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(waterSourceIdList[index - 1]);
            rb.setId(index * 10);

            waterSourceGroup.addView(rb);

            if (!TextUtils.isEmpty(waterSourceText)) {
                if (rb.getTag().equals(waterSourceText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            waterSourceGroup.check(10);
    }

    protected void updateMotiveSource(View parentView, LayoutInflater inflater) {
        String motiveSourceText = ((TunisiaCrop) crop).getMainMotive();

        RadioGroup motiveSourceGroup = (RadioGroup) parentView.findViewById(R.id.motiveGroup);
        motiveSourceGroup.setEnabled(!isModeLocked);
        motiveSourceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaCrop) crop).setMainMotive(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.tunisia_motive_source_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, motiveSourceGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(motiveSourceIdList[index - 1]);
            rb.setId(index * 11);

            motiveSourceGroup.addView(rb);

            if (!TextUtils.isEmpty(motiveSourceText)) {
                if (rb.getTag().equals(motiveSourceText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            motiveSourceGroup.check(11);
    }

}
