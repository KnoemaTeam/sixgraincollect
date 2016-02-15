package org.graindataterminal.views.cameroon;

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
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerBASF;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerBasf extends BaseFragment {
    private TextView basfProductsTitle = null;
    private EditText basfProducts = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerBasf fragment = new FarmerBasf();
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
        View view = inflater.inflate(R.layout.cm_farmer_basf, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateBasfProducts(view);
        updateKnowBasf(view, inflater);

        return view;
    }

    protected void updateKnowBasf(View parentView, LayoutInflater inflater) {
        final FarmerBASF farmerBASF = ((CameroonSurvey) survey).getFarmerBASF();
        String isBASFKnownText = farmerBASF.getIsBASFKnown();

        RadioGroup knowBasfGroup = (RadioGroup) parentView.findViewById(R.id.knowBasfGroup);
        knowBasfGroup.setEnabled(!isModeLocked);
        knowBasfGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                updateControlVisibility(id, true);

                if (!CameroonSurvey.answerIdList[0].equals(id))
                    basfProducts.setText(null);

                farmerBASF.setIsBASFKnown(id);
                ((CameroonSurvey) survey).setFarmerBASF(farmerBASF);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, knowBasfGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(CameroonSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            knowBasfGroup.addView(rb);

            if (!TextUtils.isEmpty(isBASFKnownText)) {
                if (rb.getTag().equals(isBASFKnownText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            knowBasfGroup.check(10);
        else
            updateControlVisibility(isBASFKnownText, false);
    }

    protected void updateControlVisibility(String answer, boolean withAnimation) {
        if (CameroonSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(basfProductsTitle);
                Helper.showView(basfProducts);
            }
            else {
                basfProductsTitle.setVisibility(View.VISIBLE);
                basfProducts.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(basfProductsTitle);
                Helper.fadeView(basfProducts);
            }
            else {
                basfProductsTitle.setVisibility(View.GONE);
                basfProducts.setVisibility(View.GONE);
            }
        }
    }

    protected void updateBasfProducts(View parentView) {
        final FarmerBASF farmerBASF = ((CameroonSurvey) survey).getFarmerBASF();
        String basfProductsText = farmerBASF.getBASFproducts();

        basfProductsTitle = (TextView) parentView.findViewById(R.id.basfProductsTitle);
        basfProducts = (EditText) parentView.findViewById(R.id.basfProductsText);
        basfProducts.setEnabled(!isModeLocked);
        basfProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    farmerBASF.setBASFproducts(null);
                else
                    farmerBASF.setBASFproducts(s.toString());

                ((CameroonSurvey) survey).setFarmerBASF(farmerBASF);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(basfProductsText))
            basfProducts.setText(basfProductsText);
    }
}
