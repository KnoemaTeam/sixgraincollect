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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalField;
import org.odk.collect.android.R;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class FieldPesticide extends BaseFragment {
    private TextView pesticideCostTitle = null;
    private EditText pesticideCost = null;

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

        if (getArguments() != null)
            screenIndex = getArguments().getInt(SCREEN_INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sn_field_pesticide, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updatePesticideCost(view);
        updateIsPesticideUsed(view, inflater);

        return view;
    }

    protected void updateIsPesticideUsed(View parentView, LayoutInflater inflater) {
        String isPesticideUsedText = ((SenegalField) field).getIsPesticideUsed();

        RadioGroup pesticideUsedGroup = (RadioGroup) parentView.findViewById(R.id.isPesticideUsedGroup);
        pesticideUsedGroup.setEnabled(!isModeLocked);
        pesticideUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalField) field).setIsPesticideUsed(id);
                updatePesticideVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, pesticideUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            pesticideUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isPesticideUsedText)) {
                if (rb.getTag().equals(isPesticideUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            pesticideUsedGroup.check(10);
        else
            updatePesticideVisibility(isPesticideUsedText, false);
    }

    protected void updatePesticideVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(pesticideCostTitle);
                Helper.showView(pesticideCost);
            }
            else {
                pesticideCostTitle.setVisibility(View.VISIBLE);
                pesticideCost.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(pesticideCostTitle);
                Helper.fadeView(pesticideCost);
            }
            else {
                pesticideCostTitle.setVisibility(View.GONE);
                pesticideCost.setVisibility(View.GONE);
            }
        }
    }

    protected void updatePesticideCost(View parentView) {
        String pesticideCostText = ((SenegalField) field).getPesticideCost();

        pesticideCostTitle = (TextView) parentView.findViewById(R.id.pesticideCostTitle);
        pesticideCost = (EditText) parentView.findViewById(R.id.pesticideCostText);
        pesticideCost.setEnabled(!isModeLocked);
        pesticideCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalField) field).setPesticideCost(null);
                else
                    ((SenegalField) field).setPesticideCost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(pesticideCostText))
            pesticideCost.setText(pesticideCostText);
    }
}
