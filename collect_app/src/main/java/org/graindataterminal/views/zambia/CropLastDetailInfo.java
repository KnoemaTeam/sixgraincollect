package org.graindataterminal.views.zambia;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ScrollView;

import org.odk.collect.android.R;
import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.helpers.DictionaryManager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.zambia.ZambiaCrop;
import org.graindataterminal.views.base.BaseFragment;

public class CropLastDetailInfo extends BaseFragment {
    private final static String CROP_GROUP_KEY = "crop";
    private final static String CROP_QUANTITY_KEY = "area_last";

    private ScrollView scrollView = null;
    private EditText cropQuantity = null;

    private FragmentNotificationListener notificationListener = null;

    private static String[] cropNameIdList = {"maize", "sorghum", "rice", "millet", "sunflower", "groundnuts", "soybeans", "seed_cotton", "irish_potatoes", "v_tobacco", "b_tobacco", "mixed_beans", "sweet_potatoes", "cassava", "cabbage", "rape", "spinach", "tomato", "onion", "okra", "eggplant", "pumpkin", "chilies", "cauliflower", "carrots", "impwa", "chinese_cabbage", "sugar_cane"};
    private static String[] cropUnitIdList = {"hectare", "lima", "acre", "sq_metres"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropLastDetailInfo fragment = new CropLastDetailInfo();
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
        View view = inflater.inflate(R.layout.za_crop_last_info, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        final FieldsPager parentActivity = (FieldsPager) getActivity();
        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        if (crop != null) {
            updateCropName(view, inflater);
            updateCropQuantity(view);
            updateCropUnit(view, inflater);

            if (parentActivity != null) {
                parentActivity.setNotificationListener(screenIndex,  new BaseActivity.NotificationListener() {
                    @Override
                    public void refreshFragmentView() {
                        if (cropQuantity != null && parentActivity.checkRequiredFieldByKey(screenIndex, CROP_QUANTITY_KEY)) {
                            cropQuantity.setHint(R.string.title_required_field);
                            cropQuantity.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                            scrollView.smoothScrollTo(0, cropQuantity.getTop());
                        }
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                notificationListener = (FragmentNotificationListener) context;
            }
            catch (ClassCastException exception) {
                throw new ClassCastException(context.toString() + " " + "must implement FarmerNotificationListener");
            }
        }
    }

    protected void updateCropName(View parentView, LayoutInflater inflater) {
        String cropNameText = ((ZambiaCrop) crop).getCropNameLast();

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

                    ((ZambiaCrop) crop).setCropNameLast(rb.getTag().toString());
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

                    ((ZambiaCrop) crop).setCropNameLast(rb.getTag().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] nameList =  resources.getStringArray(R.array.zambia_crop_name_list);

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

    protected void updateCropQuantity(View parentView) {
        String cropQuantityText = ((ZambiaCrop) crop).getCropAreaLast();

        cropQuantity = (EditText) parentView.findViewById(R.id.cropQuantityText);
        cropQuantity.setEnabled(!isModeLocked);
        cropQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((ZambiaCrop) crop).setCropAreaLast(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, CROP_QUANTITY_KEY, true);
                }
                else {
                    ((ZambiaCrop) crop).setCropAreaLast(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, CROP_QUANTITY_KEY, false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cropQuantityText))
            cropQuantity.setText(cropQuantityText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, CROP_QUANTITY_KEY, true);
    }

    protected void updateCropUnit(View parentView, LayoutInflater inflater) {
        String cropUnitText = ((ZambiaCrop) crop).getAreaUnitLast();

        RadioGroup unitGroup = (RadioGroup) parentView.findViewById(R.id.cropUnitGroup);
        unitGroup.setEnabled(!isModeLocked);
        unitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                ((ZambiaCrop) crop).setAreaUnitLast(rb.getTag().toString());
            }
        });

        Resources resources = getResources();
        String[] unitList = resources.getStringArray(R.array.zambia_crop_unit_list);

        int index = 1;
        boolean notSet = true;

        for (String unit : unitList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, unitGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(unit);
            rb.setTag(cropUnitIdList[index - 1]);
            rb.setId(index * 11);

            unitGroup.addView(rb);

            if (!TextUtils.isEmpty(cropUnitText)) {
                if (rb.getTag().equals(cropUnitText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            unitGroup.check(11);
    }
}
