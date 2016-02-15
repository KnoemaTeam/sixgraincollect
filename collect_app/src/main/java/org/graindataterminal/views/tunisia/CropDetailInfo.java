package org.graindataterminal.views.tunisia;

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

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaCrop;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class CropDetailInfo extends BaseFragment {
    private final static String CROP_QUANTITY_KEY = "area";

    private static String[] cropNameIdList = {"ble_dur", "ble_tendre", "orge", "triticale", "autre_cereales", "feve", "feve_rôle", "pois_chiche", "autres", "Orge_en_Vert", "foin", "ensilage", "Tomate_de_saison", "Tomate_arrière_saison", "Pomme_de_terre_de_saison", "Pomme_de_terre_arrière_saison", "artichaut", "oignon", "ail", "melon", "pasteque", "petit_pois", "olive_huile", "olive_table", "agrumes", "amandier", "raisin_de_table", "pommier", "prunier", "jachère"};
    private static String[] cropUnitIdList = {"tonne", "hectare", "mètres_carrés"};

    private ScrollView scrollView = null;
    private EditText cropQuantity = null;

    private FragmentNotificationListener notificationListener = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        CropDetailInfo fragment = new CropDetailInfo();
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
        View view = inflater.inflate(R.layout.tn_crop_info, container, false);
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
                parentActivity.setNotificationListener(screenIndex, new BaseActivity.NotificationListener() {
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
        String cropNameText = ((TunisiaCrop) crop).getCropName();

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

                    ((TunisiaCrop) crop).setCropName(rb.getTag().toString());
                    ((TunisiaCrop) crop).setTitle(rb.getText().toString());
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
                    ((TunisiaCrop) crop).setCropName(id);
                }
            }
        });

        Resources resources = getResources();
        String[] nameList =  resources.getStringArray(R.array.tunisia_crop_name_list);

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
        String cropQuantityText = ((TunisiaCrop) crop).getCropArea();

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
                    ((TunisiaCrop) crop).setCropArea(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, CROP_QUANTITY_KEY, true);
                }
                else {
                    ((TunisiaCrop) crop).setCropArea(s.toString());
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
        String cropUnitText = ((TunisiaCrop) crop).getAreaUnit();

        RadioGroup unitGroup = (RadioGroup) parentView.findViewById(R.id.cropUnitGroup);
        unitGroup.setEnabled(!isModeLocked);
        unitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((TunisiaCrop) crop).setAreaUnit(id);
            }
        });

        Resources resources = getResources();
        String[] unitList = resources.getStringArray(R.array.tunisia_crop_unit_list);

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
