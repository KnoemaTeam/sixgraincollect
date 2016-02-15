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

public class FieldPesticide extends BaseFragment {
    private static String[] answerIdList = {"oui", "non"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldPesticide fragment = new FieldPesticide();
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
        View view = inflater.inflate(R.layout.tn_field_protection, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateIsPesticideUsed(view, inflater);

        return view;
    }

    protected void updateIsPesticideUsed(View parentView, LayoutInflater inflater) {
        String pesticideText = ((TunisiaCrop) crop).getIsPesticideUsed();

        RadioGroup pesticideGroup = (RadioGroup) parentView.findViewById(R.id.isPesticideUsedGroup);
        pesticideGroup.setEnabled(!isModeLocked);
        pesticideGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaCrop) crop).setIsPesticideUsed(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, pesticideGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            pesticideGroup.addView(rb);

            if (!TextUtils.isEmpty(pesticideText)) {
                if (rb.getTag().equals(pesticideText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            pesticideGroup.check(10);
    }
}
