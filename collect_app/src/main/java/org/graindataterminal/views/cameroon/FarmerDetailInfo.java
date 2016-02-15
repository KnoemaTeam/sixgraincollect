package org.graindataterminal.views.cameroon;

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

import org.odk.collect.android.R;
import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerGeneralInfo;
import org.graindataterminal.views.base.BaseFragment;
import org.odk.collect.android.application.Collect;

public class FarmerDetailInfo extends BaseFragment {
    private String mobilePhoneText = null;
    private static String[] extensionOfficerIdList = {"mrs_b"};

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cm_farmer_detail_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFirstName(view);
        updateLastName(view);
        updateBirthDate(view);
        updateExtensionOfficer(view, inflater);
        updateCooperative(view);
        updateMobilePhone(view);

        return view;
    }

    protected void updateFirstName(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String firstNameText = farmerGeneralInfo.getFirstName();

        EditText firstName = (EditText) parentView.findViewById(R.id.firstNameText);
        firstName.setEnabled(!isModeLocked);
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setFirstName(null);
                } else {
                    farmerGeneralInfo.setFirstName(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(firstNameText))
            firstName.setText(firstNameText);
    }

    protected void updateLastName(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String lastNameText = farmerGeneralInfo.getLastName();

        EditText lastName = (EditText) parentView.findViewById(R.id.lastNameText);
        lastName.setEnabled(!isModeLocked);
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setLastName(null);
                } else {
                    farmerGeneralInfo.setLastName(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(lastNameText))
            lastName.setText(lastNameText);
    }

    protected void updateBirthDate(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String birthDateText = farmerGeneralInfo.getBirthDate();

        final EditText birthDate = (EditText) parentView.findViewById(R.id.birthDateText);
        birthDate.setEnabled(!isModeLocked);
        birthDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setBirthDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(birthDate.getText().toString())) {
                                birthDate.setText(correctDate);
                                birthDate.setSelection(correctDate.length());
                            }

                            farmerGeneralInfo.setBirthDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(birthDateText))
            birthDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", birthDateText));
    }

    protected void updateExtensionOfficer(View parentView, LayoutInflater inflater) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String extensionOfficerText = farmerGeneralInfo.getExtensionOfficer();

        EditText officer = (EditText) parentView.findViewById(R.id.extensionOfficerText);
        officer.setEnabled(!isModeLocked);
        officer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setExtensionOfficer(null);
                } else {
                    farmerGeneralInfo.setExtensionOfficer(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(extensionOfficerText))
            officer.setText(extensionOfficerText);

    }

    protected void updateCooperative(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String cooperativeText = farmerGeneralInfo.getCooperative();

        EditText cooperative = (EditText) parentView.findViewById(R.id.cooperativeText);
        cooperative.setEnabled(!isModeLocked);
        cooperative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setCooperative(null);
                } else {
                    farmerGeneralInfo.setCooperative(s.toString());
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cooperativeText))
            cooperative.setText(cooperativeText);
    }

    protected void updateMobilePhone(View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        mobilePhoneText = farmerGeneralInfo.getPhoneNumber();

        final EditText mobilePhone = (EditText) parentView.findViewById(R.id.mobilePhoneText);
        mobilePhone.setEnabled(!isModeLocked);
        mobilePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    farmerGeneralInfo.setPhoneNumber(null);
                } else {
                    if (PhoneNumberUtils.compare(Collect.getContext(), s.toString(), mobilePhoneText))
                        return;

                    mobilePhoneText = PhoneNumberUtils.formatNumber(s.toString());
                    mobilePhone.setText(mobilePhoneText);
                    mobilePhone.setSelection(mobilePhoneText.length());

                    farmerGeneralInfo.setPhoneNumber(mobilePhoneText);
                }

                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(mobilePhoneText))
            mobilePhone.setText(mobilePhoneText);
    }
}
