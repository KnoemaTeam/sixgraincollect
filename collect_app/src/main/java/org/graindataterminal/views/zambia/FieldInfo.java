package org.graindataterminal.views.zambia;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.adapters.BaseDelegate;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.zambia.ZambiaCrop;
import org.graindataterminal.models.zambia.ZambiaField;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FieldInfo extends BaseFragment {
    private static String[] answerIdList = {"yes", "no"};
    private static String[] fieldTypeIdList = {"croplands", "grasslands", "shrubland_open", "shrubland_dense", "forest_open", "forest_moderate", "forest_dense", "permanent_wetland", "urban_continuous", "urban_discontinuous", "barren", "water", "other_nonfield"};

    public BaseDelegate delegate = null;

    private TextView fieldTypeTitle = null;
    private RadioGroup fieldTypeGroup = null;

    public static FieldInfo getInstance (BaseDelegate delegate) {
        FieldInfo fragment = new FieldInfo();
        fragment.setDelegate(delegate);

        return fragment;
    }

    public void setDelegate (BaseDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.za_field_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFieldType(view, inflater);
        updateIsField(view, inflater);

        return view;
    }

    protected void updateIsField(View parentView, LayoutInflater inflater) {
        String isFieldText = ((ZambiaField) field).getIsField();

        RadioGroup fieldGroup = (RadioGroup) parentView.findViewById(R.id.isFieldGroup);
        fieldGroup.setEnabled(!isModeLocked);
        fieldGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((ZambiaField) field).setIsField(id);
                updateFieldTypeVisibility(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, fieldGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            fieldGroup.addView(rb);

            if (!TextUtils.isEmpty(isFieldText)) {
                if (rb.getTag().equals(isFieldText)) {
                    updateFieldTypeVisibility(isFieldText);

                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet) {
            fieldGroup.check(20);
            updateFieldTypeVisibility(answerIdList[1]);
        }
    }

    protected void updateFieldTypeVisibility(String answer) {
        int visibility;

        if (answerIdList[1].equals(answer)) {
            if (!isModeLocked && ((ZambiaField) field).getCrop() == null) {
                if (answerIdList[1].equals(survey.getCropProduction()))
                    ((ZambiaField) field).setCrop(null);
                else
                    ((ZambiaField) field).setCrop(new ZambiaCrop());

                ((ZambiaField) field).setFieldType(null);
                ((ZambiaField) field).setTitle(null);
            }

            visibility = View.GONE;
        }
        else {
            if (!isModeLocked && ((ZambiaField) field).getFieldType() == null) {
                ((ZambiaField) field).setCrop(null);
                ((ZambiaField) field).setFieldType(fieldTypeIdList[0]);
            }

            visibility = View.VISIBLE;
        }

        if (delegate != null)
            delegate.onControlStateChanged(visibility == View.VISIBLE);

        fieldTypeTitle.setVisibility(visibility);
        fieldTypeGroup.setVisibility(visibility);
    }

    protected void updateFieldType(View parentView, LayoutInflater inflater) {
        String fieldTypeText = ((ZambiaField) field).getFieldType();

        fieldTypeTitle = (TextView) parentView.findViewById(R.id.fieldTypeTitle);
        fieldTypeGroup = (RadioGroup) parentView.findViewById(R.id.fieldTypeGroup);
        fieldTypeGroup.setEnabled(!isModeLocked);
        fieldTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);

                ((ZambiaField) field).setFieldType(rb.getTag().toString());
                ((ZambiaField) field).setTitle(rb.getText().toString());
            }
        });

        Resources resources = getResources();
        String[] typeList =  resources.getStringArray(R.array.zambia_field_type_list);

        int index = 1;
        boolean notSet = true;

        for (String type: typeList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, fieldTypeGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(fieldTypeIdList[index - 1]);
            rb.setId(index * 11);

            fieldTypeGroup.addView(rb);

            if (!TextUtils.isEmpty(fieldTypeText)) {
                if (rb.getTag().equals(fieldTypeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            fieldTypeGroup.check(11);
    }
}
