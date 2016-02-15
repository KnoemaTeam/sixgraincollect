package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
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
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerAgrActivity extends BaseFragment {
    private final static String[] answerIdList = {"oui", "non"};

    private TextView hasProOrgGroupTitle = null;
    private RadioGroup hasProOrgGroup = null;

    private TextView organizationNameTitle = null;
    private EditText organizationName = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerAgrActivity fragment = new FarmerAgrActivity();
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
        View view = inflater.inflate(R.layout.sn_farmer_agr_activity, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateOrganizationName(view);
        updateHasProfessionalOrganization(view, inflater);
        updateHasAgrActivity(view, inflater);

        if (SenegalSurvey.answerIdList[1].equals(((SenegalSurvey) survey).getHasAgrActivity())) {
            hasProOrgGroup.check(getResources().getInteger(R.integer.default_answer) * 22);

            ((SenegalSurvey) survey).setHasAgrOrganization(null);
            ((SenegalSurvey) survey).setAgrOrganizationName(null);
        }

        return view;
    }

    protected void updateHasAgrActivity(View parentView, LayoutInflater inflater) {
        String hasAgrActivityText = ((SenegalSurvey) survey).getHasAgrActivity();

        RadioGroup hasAgrActivityGroup = (RadioGroup) parentView.findViewById(R.id.hasAgrActivityGroup);
        hasAgrActivityGroup.setEnabled(!isModeLocked);
        hasAgrActivityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasAgrActivity(id);
                updateHasProfessionalOrganizationVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasAgrActivityGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            hasAgrActivityGroup.addView(rb);

            if (!TextUtils.isEmpty(hasAgrActivityText)) {
                if (rb.getTag().equals(hasAgrActivityText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasAgrActivityGroup.check(getResources().getInteger(R.integer.default_answer) * 10);
        else {
            updateHasProfessionalOrganizationVisibility(hasAgrActivityText, false);
        }
    }

    protected void updateHasProfessionalOrganizationVisibility(String answer, boolean withAnimation) {
        if (answer != null && answer.compareToIgnoreCase(answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(hasProOrgGroupTitle);
                Helper.showView(hasProOrgGroup);
            }
            else {
                hasProOrgGroupTitle.setVisibility(View.VISIBLE);
                hasProOrgGroup.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(hasProOrgGroupTitle);
                Helper.fadeView(hasProOrgGroup);
            }
            else {
                hasProOrgGroupTitle.setVisibility(View.GONE);
                hasProOrgGroup.setVisibility(View.GONE);
            }

            hasProOrgGroup.check(getResources().getInteger(R.integer.default_answer) * 22);
        }
    }

    protected void updateHasProfessionalOrganization(View parentView, LayoutInflater inflater) {
        String hasProfOrgText = ((SenegalSurvey) survey).getHasAgrOrganization();

        hasProOrgGroupTitle = (TextView) parentView.findViewById(R.id.hasProfessionalOrganizationTitle);
        hasProOrgGroup = (RadioGroup) parentView.findViewById(R.id.hasProfessionalOrganizationGroup);
        hasProOrgGroup.setEnabled(!isModeLocked);
        hasProOrgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHasAgrOrganization(id);
                updateOrganizationNameVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasProOrgGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 11);

            hasProOrgGroup.addView(rb);

            if (!TextUtils.isEmpty(hasProfOrgText)) {
                if (rb.getTag().equals(hasProfOrgText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasProOrgGroup.check(getResources().getInteger(R.integer.default_answer) * 11);
        else
            updateOrganizationNameVisibility(hasProfOrgText, false);
    }

    protected void updateOrganizationNameVisibility(String answer, boolean withAnimation) {
        if (answer != null && answer.compareToIgnoreCase(answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(organizationNameTitle);
                Helper.showView(organizationName);
            }
            else {
                organizationNameTitle.setVisibility(View.VISIBLE);
                organizationName.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(organizationNameTitle);
                Helper.fadeView(organizationName);
            }
            else {
                organizationNameTitle.setVisibility(View.GONE);
                organizationName.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOrganizationName(View parentView) {
        String organizationNameText = ((SenegalSurvey) survey).getAgrOrganizationName();

        organizationNameTitle = (TextView) parentView.findViewById(R.id.agrOrganizationTitle);
        organizationName = (EditText) parentView.findViewById(R.id.agrOrganizationText);
        organizationName.setEnabled(!isModeLocked);
        organizationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setAgrOrganizationName(null);
                else
                    ((SenegalSurvey) survey).setAgrOrganizationName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(organizationNameText))
            organizationName.setText(organizationNameText);
    }
}
