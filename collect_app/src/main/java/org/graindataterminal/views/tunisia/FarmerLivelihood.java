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
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerLivelihood extends BaseFragment {
    private static String[] livelihoodIdList = {"dans_la_ferme", "hors_ferme"};
    private static String[] labourIdList = {"familiale", "main_oeuvre_salariale", "occasionnel"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerLivelihood fragment = new FarmerLivelihood();
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
        View view = inflater.inflate(R.layout.tn_farmer_livelihood, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateLivelihood(view, inflater);
        updateLabour(view, inflater);

        return view;
    }

    protected void updateLivelihood(View parentView, LayoutInflater inflater) {
        String livelihoodText = ((TunisiaSurvey) survey).getSourceLivelihood();

        RadioGroup livelihoodGroup = (RadioGroup) parentView.findViewById(R.id.livelihoodSourceGroup);
        livelihoodGroup.setEnabled(!isModeLocked);
        livelihoodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaSurvey) survey).setSourceLivelihood(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.tunisia_livelihood_source_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, livelihoodGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(livelihoodIdList[index - 1]);
            rb.setId(index * 10);

            livelihoodGroup.addView(rb);

            if (!TextUtils.isEmpty(livelihoodText)) {
                if (rb.getTag().equals(livelihoodText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            livelihoodGroup.check(10);
    }

    protected void updateLabour(View parentView, LayoutInflater inflater) {
        String labourText = ((TunisiaSurvey) survey).getSourceLabour();

        RadioGroup labourGroup = (RadioGroup) parentView.findViewById(R.id.labourSourceGroup);
        labourGroup.setEnabled(!isModeLocked);
        labourGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaSurvey) survey).setSourceLabour(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.tunisia_labour_source_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, labourGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(labourIdList[index - 1]);
            rb.setId(index * 11);

            labourGroup.addView(rb);

            if (!TextUtils.isEmpty(labourText)) {
                if (rb.getTag().equals(labourText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            labourGroup.check(11);
    }
}
