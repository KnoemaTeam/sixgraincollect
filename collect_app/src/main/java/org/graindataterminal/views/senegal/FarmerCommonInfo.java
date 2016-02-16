package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
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
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;
import org.odk.collect.android.application.Collect;

public class FarmerCommonInfo extends BaseFragment {
    private final static String[] zoneIdList = {"urbain", "rural"};
    private String faxText = null;
    private String phoneText = null;
    private String cellPhoneText = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCommonInfo fragment = new FarmerCommonInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_common_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateZoneCensus(view);
        updateZoneGroup(view, inflater);
        updatePostbox(view);
        updateFax(view);
        updatePhone(view);
        updateCellPhone(view);
        updateMail(view);
        updateWebSite(view);
        updateRegimeExploitation(view, inflater);

        return view;
    }

    protected void updateZoneCensus(View parentView) {
        String zoneCensusText = ((SenegalSurvey) survey).getZoneRecensement();

        EditText zoneCensus = (EditText) parentView.findViewById(R.id.zoneCensusText);
        zoneCensus.setEnabled(!isModeLocked);
        zoneCensus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setZoneRecensement(null);
                else
                    ((SenegalSurvey) survey).setZoneRecensement(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(zoneCensusText))
            zoneCensus.setText(zoneCensusText);
    }

    protected void updateZoneGroup(View parentView, LayoutInflater inflater) {
        String zoneText = ((SenegalSurvey) survey).getZone();

        RadioGroup zoneGroup = (RadioGroup) parentView.findViewById(R.id.zoneGroup);
        zoneGroup.setEnabled(!isModeLocked);
        zoneGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setZone(id);
            }
        });

        Resources resources = getResources();
        String[] zoneNames =  resources.getStringArray(R.array.senegal_zone_list);

        int index = 1;
        boolean notSet = true;

        for (String zoneName: zoneNames) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, zoneGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(zoneName);
            rb.setTag(zoneIdList[index - 1]);
            rb.setId(index * 10);

            zoneGroup.addView(rb);

            if (!TextUtils.isEmpty(zoneText)) {
                if (rb.getTag().equals(zoneText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            zoneGroup.check(10);
    }

    protected void updatePostbox(View parentView) {
        String postBoxText = ((SenegalSurvey) survey).getPostBox();

        EditText postBox = (EditText) parentView.findViewById(R.id.postBoxText);
        postBox.setEnabled(!isModeLocked);
        postBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setPostBox(null);
                else
                    ((SenegalSurvey) survey).setPostBox(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(postBoxText))
            postBox.setText(postBoxText);
    }

    protected void updateFax(View parentView) {
        faxText = ((SenegalSurvey) survey).getFax();

        final EditText fax = (EditText) parentView.findViewById(R.id.faxText);
        fax.setEnabled(!isModeLocked);
        fax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setFax(null);
                else {
                    if (PhoneNumberUtils.compare(Collect.getInstance().getContext(), s.toString(), faxText))
                        return;

                    faxText = PhoneNumberUtils.formatNumber(s.toString());
                    fax.setText(faxText);
                    fax.setSelection(faxText.length());

                    ((SenegalSurvey) survey).setFax(faxText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(faxText))
            fax.setText(faxText);
    }

    protected void updatePhone(View parentView) {
        phoneText = ((SenegalSurvey) survey).getPhone();

        final EditText phone = (EditText) parentView.findViewById(R.id.phoneText);
        phone.setEnabled(!isModeLocked);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setPhone(null);
                else {
                    if (PhoneNumberUtils.compare(Collect.getInstance().getContext(), s.toString(), phoneText))
                        return;

                    phoneText = PhoneNumberUtils.formatNumber(s.toString());
                    phone.setText(phoneText);
                    phone.setSelection(phoneText.length());

                    ((SenegalSurvey) survey).setPhone(phoneText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(phoneText))
            phone.setText(phoneText);
    }

    protected void updateCellPhone(View parentView) {
        cellPhoneText = ((SenegalSurvey) survey).getCellPhone();

        final EditText cellPhone = (EditText) parentView.findViewById(R.id.cellPhoneText);
        cellPhone.setEnabled(!isModeLocked);
        cellPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setCellPhone(null);
                else {
                    if (PhoneNumberUtils.compare(Collect.getInstance().getContext(), s.toString(), cellPhoneText))
                        return;

                    cellPhoneText = PhoneNumberUtils.formatNumber(s.toString());
                    cellPhone.setText(cellPhoneText);
                    cellPhone.setSelection(cellPhoneText.length());

                    ((SenegalSurvey) survey).setCellPhone(cellPhoneText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cellPhoneText))
            cellPhone.setText(cellPhoneText);
    }

    protected void updateMail(View parentView) {
        String mailText = ((SenegalSurvey) survey).getEmail();

        EditText mail = (EditText) parentView.findViewById(R.id.emailText);
        mail.setEnabled(!isModeLocked);
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setEmail(null);
                else
                    ((SenegalSurvey) survey).setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(mailText))
            mail.setText(mailText);
    }

    protected void updateWebSite(View parentView) {
        String webSiteText = ((SenegalSurvey) survey).getWebsite();

        EditText webSite = (EditText) parentView.findViewById(R.id.webSiteText);
        webSite.setEnabled(!isModeLocked);
        webSite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setWebsite(null);
                else
                    ((SenegalSurvey) survey).setWebsite(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(webSiteText))
            webSite.setText(webSiteText);
    }

    protected void updateRegimeExploitation(View parentView, LayoutInflater inflater) {
        String regimeExploitationText = ((SenegalSurvey) survey).getLegalExpRegime();

        final RadioGroup regimeGroup1 = (RadioGroup) parentView.findViewById(R.id.regimeGroup1);
        final RadioGroup regimeGroup2 = (RadioGroup) parentView.findViewById(R.id.regimeGroup2);

        regimeGroup1.setEnabled(!isModeLocked);
        regimeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (regimeGroup2 != null && regimeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        regimeGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setLegalExpRegime(id);
                }
            }
        });

        regimeGroup2.setEnabled(!isModeLocked);
        regimeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (regimeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        regimeGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    ((SenegalSurvey) survey).setLegalExpRegime(id);
                }
            }
        });

        Resources resources = getResources();
        String[] regimes =  resources.getStringArray(R.array.senegal_regime_exploitation_list);

        int index = 1;
        boolean notSet = true;

        for (String regime: regimes) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, regimeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(regime);
            rb.setTag(SenegalSurvey.regimeIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 < regimes.length / 2)
                regimeGroup1.addView(rb);
            else
                regimeGroup2.addView(rb);

            if (!TextUtils.isEmpty(regimeExploitationText)) {
                if (rb.getTag().equals(regimeExploitationText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            regimeGroup1.check(11);
    }
}
