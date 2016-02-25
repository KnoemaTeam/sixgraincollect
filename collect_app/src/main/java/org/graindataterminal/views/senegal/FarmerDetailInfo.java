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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.SimpleFormatter;

public class FarmerDetailInfo extends BaseFragment {
    private final static String[] genderIdList = {"masculin", "feminin"};
    private final static String[] statusIdList = {"cel", "marie", "divorce", "veuf", "libre", "sep"};
    private final static String[] nationalityIdList = {"sen", "bur", "ivo", "gui", "mal", "ben", "nig", "tog", "bis", "aut_nat_afr", "atr_nat_afr", "fra", "atr_nat_eur", "ame", "can", "aut_nat_amr", "asi", "atr_nat"};

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
        View view = inflater.inflate(R.layout.sn_farmer_detail_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        if (((SenegalSurvey) survey).getLegalExpRegime().equals(SenegalSurvey.regimeIdList[0])) {
            updateManagerName(view);
            updateOperatingOfficerName(view);
            updateDateOfBirth(view);
            updatePlaceOfBirth(view);
            updatePassportNumber(view);
            updatePassportIssueDate(view);
            updateGender(view, inflater);
            updateMaritalStatus(view, inflater);
            updateNationality(view);
        }

        return view;
    }

    protected void updateManagerName(View parentView) {
        String managerNameText = ((SenegalSurvey) survey).getHeadName();

        EditText managerName = (EditText) parentView.findViewById(R.id.managerNameText);
        managerName.setEnabled(!isModeLocked);
        managerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setHeadName(null);
                else
                    ((SenegalSurvey) survey).setHeadName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(managerNameText))
            managerName.setText(managerNameText);
    }

    protected void updateOperatingOfficerName(View parentView) {
        String operatingOfficerNameText = ((SenegalSurvey) survey).getOperatingOfficerName();

        EditText operatingOfficerName = (EditText) parentView.findViewById(R.id.operatingOfficerNameText);
        operatingOfficerName.setEnabled(!isModeLocked);
        operatingOfficerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOperatingOfficerName(null);
                else
                    ((SenegalSurvey) survey).setOperatingOfficerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(operatingOfficerNameText))
            operatingOfficerName.setText(operatingOfficerNameText);
    }

    protected void updateDateOfBirth(View parentView) {
        final String dateOfBirthText = ((SenegalSurvey) survey).getBirthDate();

        final EditText dateOfBirth = (EditText) parentView.findViewById(R.id.dateOfBirthText);
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
                    ((SenegalSurvey) survey).setBirthDate(null);
                }
                else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(dateOfBirth.getText().toString())){
                                dateOfBirth.setText(correctDate);
                                dateOfBirth.setSelection(correctDate.length());
                            }

                            ((SenegalSurvey) survey).setBirthDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
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

    protected void updatePlaceOfBirth(View parentView) {
        String placeOfBirthText = ((SenegalSurvey) survey).getBirthPlace();

        EditText placeOfBirth = (EditText) parentView.findViewById(R.id.placeOfBirthText);
        placeOfBirth.setEnabled(!isModeLocked);
        placeOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setBirthPlace(null);
                else
                    ((SenegalSurvey) survey).setBirthPlace(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(placeOfBirthText))
            placeOfBirth.setText(placeOfBirthText);
    }

    protected void updatePassportNumber(View parentView) {
        String passportNumberText = ((SenegalSurvey) survey).getIdentityCardNumber();

        EditText passportNumber = (EditText) parentView.findViewById(R.id.passportNumberText);
        passportNumber.setEnabled(!isModeLocked);
        passportNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setIdentityCardNumber(null);
                else
                    ((SenegalSurvey) survey).setIdentityCardNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(passportNumberText))
            passportNumber.setText(passportNumberText);
    }

    protected void updatePassportIssueDate(View parentView) {
        final String passportIssueDateText = ((SenegalSurvey) survey).getIdentityCardDate();

        final EditText passportIssueDate = (EditText) parentView.findViewById(R.id.dateOfIssueText);
        passportIssueDate.setEnabled(!isModeLocked);
        passportIssueDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        passportIssueDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((SenegalSurvey) survey).setIdentityCardDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(passportIssueDate.getText().toString())) {
                                passportIssueDate.setText(correctDate);
                                passportIssueDate.setSelection(correctDate.length());
                            }

                            ((SenegalSurvey) survey).setIdentityCardDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(passportIssueDateText))
            passportIssueDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", passportIssueDateText));
    }

    protected void updateGender(View parentView, LayoutInflater inflater) {
        String genderText = ((SenegalSurvey) survey).getGender();

        RadioGroup genderGroup = (RadioGroup) parentView.findViewById(R.id.genderGroup);
        genderGroup.setEnabled(!isModeLocked);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setGender(id);
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

    protected void updateMaritalStatus(View parentView, LayoutInflater inflater) {
        String maritalStatusText = ((SenegalSurvey) survey).getMaritalStatus();

        final RadioGroup maritalStatusGroup1 = (RadioGroup) parentView.findViewById(R.id.maritalStatusGroup1);
        final RadioGroup maritalStatusGroup2 = (RadioGroup) parentView.findViewById(R.id.maritalStatusGroup2);

        maritalStatusGroup1.setEnabled(!isModeLocked);
        maritalStatusGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (maritalStatusGroup2 != null && maritalStatusGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        maritalStatusGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setMaritalStatus(id);
                }
            }
        });

        maritalStatusGroup2.setEnabled(!isModeLocked);
        maritalStatusGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (maritalStatusGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        maritalStatusGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setMaritalStatus(id);
                }
            }
        });

        Resources resources = getResources();
        String[] statuses =  resources.getStringArray(R.array.senegal_marital_status_list);

        int index = 1;
        boolean notSet = true;

        for (String status: statuses) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, maritalStatusGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(status);
            rb.setTag(statusIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 < statuses.length / 2)
                maritalStatusGroup1.addView(rb);
            else
                maritalStatusGroup2.addView(rb);

            if (!TextUtils.isEmpty(maritalStatusText)) {
                if (rb.getTag().equals(maritalStatusText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            maritalStatusGroup1.check(11);
    }

    protected void updateNationality(View parentView) {
        String nationalityText = ((SenegalSurvey) survey).getNationality();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.senegal_nationality_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner nationalitySpinner = (Spinner) parentView.findViewById(R.id.nationalitySpinner);
        nationalitySpinner.setEnabled(!isModeLocked);
        nationalitySpinner.setAdapter(adapter);
        nationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                ((SenegalSurvey) survey).setNationality(nationalityIdList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(nationalityText)) {
            for (int i = 0; i < nationalityIdList.length; i++) {
                if (nationalityIdList[i].equals(nationalityText)) {
                    nationalitySpinner.setSelection(i);
                    break;
                }
            }
        }
    }
}
