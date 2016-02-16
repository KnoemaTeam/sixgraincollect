package org.graindataterminal.views.zambia;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
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
import android.widget.ScrollView;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FarmersPager;
import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.odk.collect.android.application.Collect;

public class FarmerDetailInfo extends BaseFragment {
    private final static String HEAD_NAME_KEY = "head_name";
    private final static String FARMER_NAME_KEY = "farmer_name";
    private final static String FARMER_PHONE_KEY = "contact_phone";

    private FragmentNotificationListener notificationListener = null;

    private ScrollView scrollView = null;
    private EditText headName = null;
    private EditText farmerName = null;
    private EditText phone = null;
    private String phoneText = null;

    private String rawDate = null;
    private static String[] genderIdList = {"male", "female"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerDetailInfo fragment = new FarmerDetailInfo();
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
        View view = inflater.inflate(R.layout.za_farmer_detail_info, container, false);

        final FarmersPager parentActivity = (FarmersPager) getActivity();

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateHeadName(view);
        updateFarmerName(view);
        updateGender(view, inflater);
        updateDateOfBirth(view);
        updatePhone(view);

        if (parentActivity != null) {
            parentActivity.setNotificationListener(screenIndex,  new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (headName != null && parentActivity.checkRequiredFieldByKey(screenIndex, HEAD_NAME_KEY)) {
                        headName.setHint(R.string.title_required_field);
                        headName.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, headName.getTop());
                    }

                    if (farmerName != null && parentActivity.checkRequiredFieldByKey(screenIndex, FARMER_NAME_KEY)) {
                        farmerName.setHint(R.string.title_required_field);
                        farmerName.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, farmerName.getTop());
                    }

                    if (phone != null && parentActivity.checkRequiredFieldByKey(screenIndex, FARMER_PHONE_KEY)) {
                        phone.setHint(R.string.title_required_field);
                        phone.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, phone.getTop());
                    }
                }
            });
        }

        return view;
    }

    protected void updateHeadName(View parentView) {
        String headNameText = ((ZambiaSurvey) survey).getHeadName();

        headName = (EditText) parentView.findViewById(R.id.headNameText);
        headName.setEnabled(!isModeLocked);
        headName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((ZambiaSurvey) survey).setHeadName(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, HEAD_NAME_KEY, true);
                } else {
                    ((ZambiaSurvey) survey).setHeadName(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, HEAD_NAME_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(headNameText))
            headName.setText(headNameText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, HEAD_NAME_KEY, true);
    }

    protected void updateFarmerName(View parentView) {
        String farmerNameText = ((ZambiaSurvey) survey).getFarmerName();

        farmerName = (EditText) parentView.findViewById(R.id.farmerNameText);
        farmerName.setEnabled(!isModeLocked);
        farmerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((ZambiaSurvey) survey).setFarmerName(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_NAME_KEY, true);
                } else {
                    ((ZambiaSurvey) survey).setFarmerName(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_NAME_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(farmerNameText))
            farmerName.setText(farmerNameText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, FARMER_NAME_KEY, true);
    }

    protected void updateGender(View parentView, LayoutInflater inflater) {
        String genderText = ((ZambiaSurvey) survey).getFarmerGender();

        RadioGroup genderGroup = (RadioGroup) parentView.findViewById(R.id.genderGroup);
        genderGroup.setEnabled(!isModeLocked);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                ((ZambiaSurvey) survey).setFarmerGender(rb.getTag().toString());
            }
        });

        Resources resources = getResources();
        String[] genderNames =  resources.getStringArray(R.array.senegal_gender_list);

        int index = 1;
        boolean notSet = true;

        for (String genderName: genderNames) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, genderGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(genderName);
            rb.setTag(genderIdList[index - 1]);
            rb.setId(index * 10);

            genderGroup.addView(rb);

            if (!TextUtils.isEmpty(genderText)) {
                if (rb.getTag().equals(genderText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            genderGroup.check(10);
    }

    protected void updateDateOfBirth(View parentView) {
        String dateOfBirthText = ((ZambiaSurvey) survey).getFarmerBirthdate();

        final EditText dateOfBirth = (EditText) parentView.findViewById(R.id.farmerDateText);
        dateOfBirth.setEnabled(!isModeLocked);
        dateOfBirth.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        dateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((ZambiaSurvey) survey).setFarmerBirthdate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(dateOfBirth.getText().toString())) {
                                dateOfBirth.setText(correctDate);
                                dateOfBirth.setSelection(correctDate.length());
                            }

                            ((ZambiaSurvey) survey).setFarmerBirthdate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(dateOfBirthText))
            dateOfBirth.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", dateOfBirthText));
    }

    protected void updatePhone(View parentView) {
        phoneText = ((ZambiaSurvey) survey).getContactPhone();

        phone = (EditText) parentView.findViewById(R.id.farmerPhoneText);
        phone.setEnabled(!isModeLocked);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((ZambiaSurvey) survey).setContactPhone(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_PHONE_KEY, true);
                } else {
                    if (PhoneNumberUtils.compare(Collect.getInstance().getContext(), s.toString(), phoneText))
                        return;

                    phoneText = PhoneNumberUtils.formatNumber(s.toString());
                    phone.setText(phoneText);
                    phone.setSelection(phoneText.length());

                    ((ZambiaSurvey) survey).setContactPhone(phoneText);
                    notificationListener.onRequiredFieldChanged(screenIndex, FARMER_PHONE_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(phoneText))
            phone.setText(phoneText);
        else
            notificationListener.onRequiredFieldChanged(screenIndex, FARMER_PHONE_KEY, true);
    }
}
