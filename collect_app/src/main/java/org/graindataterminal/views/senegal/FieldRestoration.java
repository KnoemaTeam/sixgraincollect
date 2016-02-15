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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.List;

public class FieldRestoration extends BaseFragment {
    private final static String[] restorationMethodIdList = {"compost", "parcage", "autre_methode"};

    private TextView restorationMethodsTitle = null;
    private LinearLayout restorationMethodsLayout = null;

    private TextView restorationCostTitle = null;
    private EditText restorationCost = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldRestoration fragment = new FieldRestoration();
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
        View view = inflater.inflate(R.layout.sn_field_restoration, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateSoilRestorationCost(view);
        updateRestorationMethods(view, inflater);
        updateIsRestorationUsed(view, inflater);

        return view;
    }

    protected void updateIsRestorationUsed(View parentView, LayoutInflater inflater) {
        String isRestorationUsedText = ((SenegalField) field).getIsSoilRestorationUsed();

        RadioGroup restorationUsedGroup = (RadioGroup) parentView.findViewById(R.id.isRestorationUsedGroup);
        restorationUsedGroup.setEnabled(!isModeLocked);
        restorationUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalField) field).setIsSoilRestorationUsed(id);
                updateSoilRestorationVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, restorationUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            restorationUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isRestorationUsedText)) {
                if (rb.getTag().equals(isRestorationUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            restorationUsedGroup.check(10);
        else
            updateSoilRestorationVisibility(isRestorationUsedText, false);
    }

    protected void updateSoilRestorationVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(restorationMethodsTitle);
                Helper.showView(restorationMethodsLayout);
                Helper.showView(restorationCostTitle);
                Helper.showView(restorationCost);
            }
            else {
                restorationMethodsTitle.setVisibility(View.VISIBLE);
                restorationMethodsLayout.setVisibility(View.VISIBLE);
                restorationCostTitle.setVisibility(View.VISIBLE);
                restorationCost.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(restorationMethodsTitle);
                Helper.fadeView(restorationMethodsLayout);
                Helper.fadeView(restorationCostTitle);
                Helper.fadeView(restorationCost);
            }
            else {
                restorationMethodsTitle.setVisibility(View.GONE);
                restorationMethodsLayout.setVisibility(View.GONE);
                restorationCostTitle.setVisibility(View.GONE);
                restorationCost.setVisibility(View.GONE);
            }
        }
    }

    protected void updateRestorationMethods(View parentView, LayoutInflater inflater) {
        final List<String> methods = ((SenegalField) field).getSoilRestorationMethods();

        restorationMethodsTitle = (TextView) parentView.findViewById(R.id.restorationMethodsTitle);
        restorationMethodsLayout = (LinearLayout) parentView.findViewById(R.id.restorationMethodsGroup);
        restorationMethodsLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] methodsList =  resources.getStringArray(R.array.senegal_restoration_methods_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !methods.contains(id))
                        methods.add(id);
                }
                else {
                    methods.remove(id);
                }

                ((SenegalField) field).setSoilRestorationMethods(methods);
            }
        };

        int index = 1;
        for (String method: methodsList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, restorationMethodsLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(restorationMethodIdList[index - 1]);
            checkBox.setText(method);
            checkBox.setOnCheckedChangeListener(event);

            restorationMethodsLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && methods.contains(id))
                checkBox.setChecked(true);

            if (methods.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateSoilRestorationCost(View parentView) {
        String restorationCostText = ((SenegalField) field).getSoilRestorationCost();

        restorationCostTitle = (TextView) parentView.findViewById(R.id.restorationCostTitle);
        restorationCost = (EditText) parentView.findViewById(R.id.restorationCostText);
        restorationCost.setEnabled(!isModeLocked);
        restorationCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalField) field).setSoilRestorationCost(null);
                else
                    ((SenegalField) field).setSoilRestorationCost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(restorationCostText))
            restorationCost.setText(restorationCostText);
    }
}
