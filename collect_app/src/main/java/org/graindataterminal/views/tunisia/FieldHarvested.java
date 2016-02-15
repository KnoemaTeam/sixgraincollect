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
import org.graindataterminal.controllers.FarmersPager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaCrop;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FieldHarvested extends BaseFragment {
    private final static String QUANTITY_KEY = "head_name";
    private FragmentNotificationListener notificationListener = null;
    private EditText harvestedQuantity = null;

    public static Fragment getInstance (int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, index);

        FieldHarvested fragment = new FieldHarvested();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            this.screenIndex = bundle.getInt(SCREEN_INDEX);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tn_field_harvested, container, false);
        final BaseActivity parentActivity = (BaseActivity) getActivity();
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateHarvestedQuantity(view);
        updateHarvestedUnit(view, inflater);

        if (parentActivity != null) {
            parentActivity.setNotificationListener(screenIndex, new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (harvestedQuantity != null && parentActivity.checkRequiredFieldByKey(screenIndex, QUANTITY_KEY)) {
                        harvestedQuantity.setHint(R.string.title_required_field);
                        harvestedQuantity.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, harvestedQuantity.getTop());
                    }
                }
            });
        }

        return view;
    }

    protected void updateHarvestedQuantity(View parentView) {
        String harvestedQuantityText = ((TunisiaCrop) crop).getHarvestedQuantity();

        harvestedQuantity = (EditText) parentView.findViewById(R.id.harvestedQuantityText);
        harvestedQuantity.setEnabled(!isModeLocked);
        harvestedQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setHarvestedQuantity(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, QUANTITY_KEY, true);
                }
                else {
                    ((TunisiaCrop) crop).setHarvestedQuantity(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, QUANTITY_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(harvestedQuantityText))
            harvestedQuantity.setText(harvestedQuantityText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, QUANTITY_KEY, true);
    }

    protected void updateHarvestedUnit(View parentView, LayoutInflater inflater) {
        String harvestedUnitText = ((TunisiaCrop) crop).getHarvestedUnit();

        RadioGroup harvestedUnitGroup = (RadioGroup) parentView.findViewById(R.id.harvestedUnitGroup);
        harvestedUnitGroup.setEnabled(!isModeLocked);
        harvestedUnitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getText().toString();

                ((TunisiaCrop) crop).setHarvestedUnit(id);
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.tunisia_crop_unit_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, harvestedUnitGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(index * 10);
            rb.setId(index * 10);

            harvestedUnitGroup.addView(rb);

            if (!TextUtils.isEmpty(harvestedUnitText)) {
                String value = harvestedUnitText;

                if (value.compareToIgnoreCase(answer) == 0) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            harvestedUnitGroup.check(10);
    }
}
