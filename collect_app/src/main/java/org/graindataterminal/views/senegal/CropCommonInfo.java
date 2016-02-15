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

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.odk.collect.android.R;
import org.graindataterminal.models.senegal.SenegalCropGroup;
import org.graindataterminal.views.base.BaseFragment;

public class CropCommonInfo extends BaseFragment {
    private final static String[] cropNameIdList = {"riz", "mil", "mai", "sorh", "fonio", "arachide", "sesa", "coton", "cann_suc", "niebe", "beref", "manioc", "basap", "tomate_fr", "tomate_ch", "pomme_fr", "pomme_ch", "oign_fr", "oigno_ch", "poivron", "carote", "h_vert", "diaxatu", "choux", "gombo", "piment", "aubergine", "navet", "patate_douce", "bissap", "banane", "mangue", "mandarine", "orange", "citron", "papaye", "pamplemousse", "pasteque", "melon", "anacarde"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropCommonInfo fragment = new CropCommonInfo();
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
        View view = inflater.inflate(R.layout.sn_crop_common_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = DataHolder.getInstance().getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCropName(view, inflater);
        updateCropArea(view);

        return view;
    }

    protected void updateCropName(View parentView, LayoutInflater inflater) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String cropNameText = cropGroup.getCropName();

        final RadioGroup cropNameGroup1 = (RadioGroup) parentView.findViewById(R.id.cropNameGroup1);
        final RadioGroup cropNameGroup2 = (RadioGroup) parentView.findViewById(R.id.cropNameGroup2);

        cropNameGroup1.setEnabled(!isModeLocked);
        cropNameGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cropNameGroup2 != null && cropNameGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        cropNameGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    String title = rb.getText().toString();

                    cropGroup.setCropName(id);

                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                    ((SenegalCrop) crop).setTitle(title);
                }
            }
        });

        cropNameGroup2.setEnabled(!isModeLocked);
        cropNameGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (cropNameGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        cropNameGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    String title = rb.getText().toString();

                    cropGroup.setCropName(id);

                    ((SenegalCrop) crop).setCropGroup(cropGroup);
                    ((SenegalCrop) crop).setTitle(title);
                }
            }
        });

        Resources resources = getResources();
        String[] nameList =  resources.getStringArray(R.array.senegal_crop_name_list);

        int index = 1;
        boolean notSet = true;

        for (String name: nameList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, cropNameGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(cropNameIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < nameList.length / 2)
                cropNameGroup1.addView(rb);
            else
                cropNameGroup2.addView(rb);

            if (!TextUtils.isEmpty(cropNameText)) {
                if (rb.getTag().equals(cropNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            cropNameGroup1.check(10);
    }

    protected void updateCropArea(View parentView) {
        final SenegalCropGroup cropGroup = ((SenegalCrop) crop).getCropGroup();
        String cropAreaText = cropGroup.getCropArea();

        EditText cropArea = (EditText) parentView.findViewById(R.id.cropAreaText);
        cropArea.setEnabled(!isModeLocked);
        cropArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    cropGroup.setCropArea(null);
                else
                    cropGroup.setCropArea(s.toString());

                ((SenegalCrop) crop).setCropGroup(cropGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cropAreaText))
            cropArea.setText(cropAreaText);
    }
}
