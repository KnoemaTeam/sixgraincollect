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

public class FieldFertilizer extends BaseFragment {
    private final static String[] fertilizerTypeIdList = {"npk", "dap", "uree", "autre_engrais"};
    private final static String[] acquisitionModeIdList = {"achat", "achat_partiel", "don", "autre_acq_engrais"};

    private TextView fertilizerTypeTitle = null;
    private LinearLayout fertilizerTypeLayout = null;

    private TextView acquisitionModeTitle = null;
    private RadioGroup acquisitionModeGroup = null;

    private TextView fertilizerAmountTitle = null;
    private EditText fertilizerAmount = null;

    private TextView fertilizerCostTitle = null;
    private EditText fertilizerCost = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldFertilizer fragment = new FieldFertilizer();
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
        View view = inflater.inflate(R.layout.sn_field_fertilizer, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFertilizerType(view, inflater);
        updateFertilizerAcquisitionMode(view, inflater);
        updateFertilizerAmount(view);
        updateFertilizerCost(view);
        updateIsFertilizerUsed(view, inflater);

        return view;
    }

    protected void updateIsFertilizerUsed(View parentView, LayoutInflater inflater) {
        String isFertilizerUsedText = ((SenegalField) field).getIsFertilizerUsed();

        RadioGroup fertilizerUsedGroup = (RadioGroup) parentView.findViewById(R.id.isFertilizerUsedGroup);
        fertilizerUsedGroup.setEnabled(!isModeLocked);
        fertilizerUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalField) field).setIsFertilizerUsed(id);
                updateFertilizerVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, fertilizerUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            fertilizerUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isFertilizerUsedText)) {
                if (rb.getTag().equals(isFertilizerUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            fertilizerUsedGroup.check(10);
        else {
            updateFertilizerVisibility(isFertilizerUsedText, false);
        }
    }

    protected void updateFertilizerVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(fertilizerTypeTitle);
                Helper.showView(fertilizerTypeLayout);
                Helper.showView(acquisitionModeTitle);
                Helper.showView(acquisitionModeGroup);
                Helper.showView(fertilizerAmountTitle);
                Helper.showView(fertilizerAmount);
                Helper.showView(fertilizerCostTitle);
                Helper.showView(fertilizerCost);
            }
            else {
                fertilizerTypeTitle.setVisibility(View.VISIBLE);
                fertilizerTypeLayout.setVisibility(View.VISIBLE);
                acquisitionModeTitle.setVisibility(View.VISIBLE);
                acquisitionModeGroup.setVisibility(View.VISIBLE);
                fertilizerAmountTitle.setVisibility(View.VISIBLE);
                fertilizerAmount.setVisibility(View.VISIBLE);
                fertilizerCostTitle.setVisibility(View.VISIBLE);
                fertilizerCost.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(fertilizerTypeTitle);
                Helper.fadeView(fertilizerTypeLayout);
                Helper.fadeView(acquisitionModeTitle);
                Helper.fadeView(acquisitionModeGroup);
                Helper.fadeView(fertilizerAmountTitle);
                Helper.fadeView(fertilizerAmount);
                Helper.fadeView(fertilizerCostTitle);
                Helper.fadeView(fertilizerCost);
            }
            else {
                fertilizerTypeTitle.setVisibility(View.GONE);
                fertilizerTypeLayout.setVisibility(View.GONE);
                acquisitionModeTitle.setVisibility(View.GONE);
                acquisitionModeGroup.setVisibility(View.GONE);
                fertilizerAmountTitle.setVisibility(View.GONE);
                fertilizerAmount.setVisibility(View.GONE);
                fertilizerCostTitle.setVisibility(View.GONE);
                fertilizerCost.setVisibility(View.GONE);
            }
        }
    }

    protected void updateFertilizerType(View parentView, LayoutInflater inflater) {
        final List<String> types = ((SenegalField) field).getFertilizerType();

        fertilizerTypeTitle = (TextView) parentView.findViewById(R.id.fertilizerTypeTitle);
        fertilizerTypeLayout = (LinearLayout) parentView.findViewById(R.id.fertilizerTypeGroup);
        fertilizerTypeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typeSources =  resources.getStringArray(R.array.senegal_fertilizer_type_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !types.contains(id))
                        types.add(id);
                }
                else {
                    types.remove(id);
                }

                ((SenegalField) field).setFertilizerType(types);
            }
        };

        int index = 1;
        for (String type: typeSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, fertilizerTypeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(fertilizerTypeIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            fertilizerTypeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateFertilizerAcquisitionMode(View parentView, LayoutInflater inflater) {
        String acquisitionModeText = ((SenegalField) field).getFertilizerMode();

        acquisitionModeTitle = (TextView) parentView.findViewById(R.id.fertilizerAcquisitionModeTitle);
        acquisitionModeGroup = (RadioGroup) parentView.findViewById(R.id.fertilizerAcquisitionModeGroup);
        acquisitionModeGroup.setEnabled(!isModeLocked);
        acquisitionModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalField) field).setFertilizerMode(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_fertilizer_acquisition_mode_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, acquisitionModeGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(acquisitionModeIdList[index - 1]);
            rb.setId(index * 12);

            acquisitionModeGroup.addView(rb);

            if (!TextUtils.isEmpty(acquisitionModeText)) {
                if (rb.getTag().equals(acquisitionModeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            acquisitionModeGroup.check(12);
    }

    protected void updateFertilizerAmount(View parentView) {
        String fertilizerAmountText = ((SenegalField) field).getFertilizerAmount();

        fertilizerAmountTitle = (TextView) parentView.findViewById(R.id.fertilizerAmountTitle);
        fertilizerAmount = (EditText) parentView.findViewById(R.id.fertilizerAmountText);
        fertilizerAmount.setEnabled(!isModeLocked);
        fertilizerAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalField) field).setFertilizerAmount(null);
                else
                    ((SenegalField) field).setFertilizerAmount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fertilizerAmountText))
            fertilizerAmount.setText(fertilizerAmountText);
    }

    protected void updateFertilizerCost(View parentView) {
        String fertilizerCostText = ((SenegalField) field).getFertilizerCost();

        fertilizerCostTitle = (TextView) parentView.findViewById(R.id.fertilizerCostTitle);
        fertilizerCost = (EditText) parentView.findViewById(R.id.fertilizerCostText);
        fertilizerCost.setEnabled(!isModeLocked);
        fertilizerCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalField) field).setFertilizerCost(null);
                else
                    ((SenegalField) field).setFertilizerCost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fertilizerCostText))
            fertilizerCost.setText(fertilizerCostText);
    }
}
