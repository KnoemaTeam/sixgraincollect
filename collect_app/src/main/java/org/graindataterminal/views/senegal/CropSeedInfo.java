package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.odk.collect.android.R;
import org.graindataterminal.models.senegal.SenegalCropGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class CropSeedInfo extends BaseFragment {
    private TextView seedReceivedAmountTitle = null;
    private EditText seedReceivedAmount = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropSeedInfo fragment = new CropSeedInfo();
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
        View view = inflater.inflate(R.layout.sn_crop_seed_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = DataHolder.getInstance().getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updatePlantedDate(view);
        updateSeedCost(view);
        updateIsSeedUsed(view, inflater);
        updateSeedReceivedAmount(view);
        updateIsSeedReceived(view, inflater);

        return view;
    }

    protected void updatePlantedDate(View parentView) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String plantedDateText = cropGroup.getPlantedDate();

        final EditText plantedDate = (EditText) parentView.findViewById(R.id.plantedDateText);
        plantedDate.setEnabled(!isModeLocked);
        plantedDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        plantedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    cropGroup.setPlantedDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(plantedDate.getText().toString())) {
                                plantedDate.setText(correctDate);
                                plantedDate.setSelection(correctDate.length());
                            }

                            cropGroup.setPlantedDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }

                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(plantedDateText))
            plantedDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", plantedDateText));
    }

    protected void updateIsSeedUsed(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String isSeedUsedText = cropGroup.getIsSeedUsed();

        RadioGroup seedUsedGroup = (RadioGroup) parentView.findViewById(R.id.isSeedUsedGroup);
        seedUsedGroup.setEnabled(!isModeLocked);
        seedUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                cropGroup.setIsSeedUsed(id);
                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, seedUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            seedUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isSeedUsedText)) {
                if (rb.getTag().equals(isSeedUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            seedUsedGroup.check(10);
    }

    protected void updateSeedCost(View parentView) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String seedCostText = cropGroup.getSeedCost();

        EditText seedCost = (EditText) parentView.findViewById(R.id.seedCostText);
        seedCost.setEnabled(!isModeLocked);
        seedCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    cropGroup.setSeedCost(null);
                else
                    cropGroup.setSeedCost(s.toString());

                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seedCostText))
            seedCost.setText(seedCostText);
    }

    protected void updateIsSeedReceived(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String isSeedReceivedText = cropGroup.getIsSeedReceived();

        RadioGroup seedReceivedGroup = (RadioGroup) parentView.findViewById(R.id.isSeedReceivedGroup);
        seedReceivedGroup.setEnabled(!isModeLocked);
        seedReceivedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                cropGroup.setIsSeedReceived(id);
                ((SenegalCrop) crop).setCropGroup(cropGroup);

                updateReceivedAmountVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, seedReceivedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 11);

            seedReceivedGroup.addView(rb);

            if (!TextUtils.isEmpty(isSeedReceivedText)) {
                if (rb.getTag().equals(isSeedReceivedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            seedReceivedGroup.check(11);
        else
            updateReceivedAmountVisibility(isSeedReceivedText, false);
    }

    protected void updateReceivedAmountVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(seedReceivedAmountTitle);
                Helper.showView(seedReceivedAmount);
            }
            else {
                seedReceivedAmountTitle.setVisibility(View.VISIBLE);
                seedReceivedAmount.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(seedReceivedAmountTitle);
                Helper.fadeView(seedReceivedAmount);
            }
            else {
                seedReceivedAmountTitle.setVisibility(View.GONE);
                seedReceivedAmount.setVisibility(View.GONE);
            }
        }
    }

    protected void updateSeedReceivedAmount(View parentView) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String seedReceivedAmountText = cropGroup.getSeedReceivedAmount();

        seedReceivedAmountTitle = (TextView) parentView.findViewById(R.id.seedReceivedAmountTitle);
        seedReceivedAmount = (EditText) parentView.findViewById(R.id.seedReceivedAmountText);
        seedReceivedAmount.setEnabled(!isModeLocked);
        seedReceivedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    cropGroup.setSeedReceivedAmount(null);
                else
                    cropGroup.setSeedReceivedAmount(s.toString());

                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seedReceivedAmountText))
            seedReceivedAmount.setText(seedReceivedAmountText);
    }
}
