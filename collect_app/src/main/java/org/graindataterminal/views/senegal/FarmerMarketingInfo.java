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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.CommercialGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerMarketingInfo extends BaseFragment {
    private TextView marketingMethodTitle = null;
    private LinearLayout marketingMethodLayout1 = null;
    private LinearLayout marketingMethodLayout2 = null;

    private TextView otherMarketingMethodTitle = null;
    private EditText otherMarketingMethod = null;

    private TextView marketingConstraintTitle = null;
    private LinearLayout marketingConstraintLayout1 = null;
    private LinearLayout marketingConstraintLayout2 = null;

    private TextView otherMarketingConstraintTitle = null;
    private EditText otherMarketingConstraint = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerMarketingInfo fragment = new FarmerMarketingInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_market_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateOtherMarketingMethod(view);
        updateMarketingMethod(view, inflater);

        updateOtherMarketingConstraint(view);
        updateMarketingConstraints(view, inflater);

        if (SenegalSurvey.answerIdList[1].equals(((SenegalSurvey) survey).getHasAgrActivity()))
            ((SenegalSurvey) survey).setCommercialGroup(null);

        return view;
    }

    protected void updateMarketingMethod(View parentView, LayoutInflater inflater) {
        final CommercialGroup commercialGroup = ((SenegalSurvey) survey).getCommercialGroup();
        final List<String> methods = commercialGroup.getTradeProduction();

        marketingMethodTitle = (TextView) parentView.findViewById(R.id.marketingMethodTitle);

        marketingMethodLayout1 = (LinearLayout) parentView.findViewById(R.id.marketingMethodGroup1);
        marketingMethodLayout1.setEnabled(!isModeLocked);

        marketingMethodLayout2 = (LinearLayout) parentView.findViewById(R.id.marketingMethodGroup2);
        marketingMethodLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] methodsSources = resources.getStringArray(R.array.senegal_marketing_method_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !methods.contains(id))
                        methods.add(id);
                }
                else {
                    methods.remove(id);
                }

                commercialGroup.setTradeProduction(methods);
                ((SenegalSurvey) survey).setCommercialGroup(commercialGroup);

                int index = CommercialGroup.marketingMethodIdList.length - 1;
                String[] sourceIdList = CommercialGroup.marketingMethodIdList;

                updateOtherMarketingMethodVisibility(methods.contains(sourceIdList[index]), true);
            }
        };

        int index = 1;
        for (String method: methodsSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, marketingMethodLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(CommercialGroup.marketingMethodIdList[index - 1]);
            checkBox.setText(method);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= methodsSources.length / 2)
                marketingMethodLayout1.addView(checkBox);
            else
                marketingMethodLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();

            if (!TextUtils.isEmpty(id) && methods.contains(id))
                checkBox.setChecked(true);

            if (methods.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        index = CommercialGroup.marketingMethodIdList.length - 1;
        String[] sourceIdList = CommercialGroup.marketingMethodIdList;

        updateOtherMarketingMethodVisibility(methods.contains(sourceIdList[index]), false);
    }

    protected void updateMarketingMethodVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(SenegalSurvey.answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(marketingMethodTitle);
                Helper.showView(marketingMethodLayout1);
                Helper.showView(marketingMethodLayout2);
            }
            else {
                marketingMethodTitle.setVisibility(View.VISIBLE);
                marketingMethodLayout1.setVisibility(View.VISIBLE);
                marketingMethodLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(marketingMethodTitle);
                Helper.fadeView(marketingMethodLayout1);
                Helper.fadeView(marketingMethodLayout2);
            }
            else {
                marketingMethodTitle.setVisibility(View.GONE);
                marketingMethodLayout1.setVisibility(View.GONE);
                marketingMethodLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherMarketingMethod(View parentView) {
        final CommercialGroup commercialGroup = ((SenegalSurvey) survey).getCommercialGroup();
        String otherMarketingMethodText = commercialGroup.getOtherTradeProduction();

        otherMarketingMethodTitle = (TextView) parentView.findViewById(R.id.otherMarketingMethodTitle);
        otherMarketingMethod = (EditText) parentView.findViewById(R.id.otherMarketingMethodText);
        otherMarketingMethod.setEnabled(!isModeLocked);
        otherMarketingMethod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    commercialGroup.setOtherTradeProduction(null);
                else
                    commercialGroup.setOtherTradeProduction(s.toString());

                ((SenegalSurvey) survey).setCommercialGroup(commercialGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherMarketingMethodText))
            otherMarketingMethod.setText(otherMarketingMethodText);
    }

    protected void updateOtherMarketingMethodVisibility(boolean hasOther, boolean withAnimation) {
        if (hasOther) {
            if (withAnimation) {
                Helper.showView(otherMarketingMethodTitle);
                Helper.showView(otherMarketingMethod);
            }
            else {
                otherMarketingMethodTitle.setVisibility(View.VISIBLE);
                otherMarketingMethod.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(otherMarketingMethodTitle);
                Helper.fadeView(otherMarketingMethod);
            }
            else {
                otherMarketingMethodTitle.setVisibility(View.GONE);
                otherMarketingMethod.setVisibility(View.GONE);
            }
        }
    }

    protected void updateMarketingConstraints(View parentView, LayoutInflater inflater) {
        final CommercialGroup commercialGroup = ((SenegalSurvey) survey).getCommercialGroup();
        final List<String> constraints = commercialGroup.getCommercialConstraints();

        marketingConstraintTitle = (TextView) parentView.findViewById(R.id.marketingConstraintTitle);

        marketingConstraintLayout1 = (LinearLayout) parentView.findViewById(R.id.marketingConstraintGroup1);
        marketingConstraintLayout1.setEnabled(!isModeLocked);

        marketingConstraintLayout2 = (LinearLayout) parentView.findViewById(R.id.marketingConstraintGroup2);
        marketingConstraintLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] constraintSources = resources.getStringArray(R.array.senegal_marketing_constraints_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !constraints.contains(id))
                        constraints.add(id);
                }
                else {
                    constraints.remove(id);
                }

                commercialGroup.setCommercialConstraints(constraints);
                ((SenegalSurvey) survey).setCommercialGroup(commercialGroup);

                int index = CommercialGroup.marketingConstraintIdList.length - 1;
                String[] sourceIdList = CommercialGroup.marketingConstraintIdList;

                updateOtherMarketingConstraintVisibility(constraints.contains(sourceIdList[index]), true);
            }
        };

        int index = 1;
        for (String constraint: constraintSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, marketingConstraintLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(CommercialGroup.marketingConstraintIdList[index - 1]);
            checkBox.setText(constraint);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= constraintSources.length / 2)
                marketingConstraintLayout1.addView(checkBox);
            else
                marketingConstraintLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();

            if (!TextUtils.isEmpty(id) && constraints.contains(id))
                checkBox.setChecked(true);

            if (constraints.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        index = CommercialGroup.marketingConstraintIdList.length - 1;
        String[] sourceIdList = CommercialGroup.marketingConstraintIdList;

        updateOtherMarketingConstraintVisibility(constraints.contains(sourceIdList[index]), false);
    }

    protected void updateMarketingConstraintsVisibility(String answer, boolean withAnimation) {
        if (answer.compareToIgnoreCase(SenegalSurvey.answerIdList[0]) == 0) {
            if (withAnimation) {
                Helper.showView(marketingConstraintTitle);
                Helper.showView(marketingConstraintLayout1);
                Helper.showView(marketingConstraintLayout2);
            }
            else {
                marketingConstraintTitle.setVisibility(View.VISIBLE);
                marketingConstraintLayout1.setVisibility(View.VISIBLE);
                marketingConstraintLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(marketingConstraintTitle);
                Helper.fadeView(marketingConstraintLayout1);
                Helper.fadeView(marketingConstraintLayout2);
            }
            else {
                marketingConstraintTitle.setVisibility(View.GONE);
                marketingConstraintLayout1.setVisibility(View.GONE);
                marketingConstraintLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherMarketingConstraint(View parentView) {
        final CommercialGroup commercialGroup = ((SenegalSurvey) survey).getCommercialGroup();
        String otherMarketingConstraintText = commercialGroup.getOtherCommercialConstraints();

        otherMarketingConstraintTitle = (TextView) parentView.findViewById(R.id.otherMarketingConstraintTitle);
        otherMarketingConstraint = (EditText) parentView.findViewById(R.id.otherMarketingConstraintText);
        otherMarketingConstraint.setEnabled(!isModeLocked);
        otherMarketingConstraint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    commercialGroup.setOtherCommercialConstraints(null);
                else
                    commercialGroup.setOtherCommercialConstraints(s.toString());

                ((SenegalSurvey) survey).setCommercialGroup(commercialGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherMarketingConstraintText))
            otherMarketingConstraint.setText(otherMarketingConstraintText);
    }

    protected void updateOtherMarketingConstraintVisibility(boolean hasOther, boolean withAnimation) {
        if (hasOther) {
            if (withAnimation) {
                Helper.showView(otherMarketingConstraintTitle);
                Helper.showView(otherMarketingConstraint);
            }
            else {
                otherMarketingConstraintTitle.setVisibility(View.VISIBLE);
                otherMarketingConstraint.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(otherMarketingConstraintTitle);
                Helper.fadeView(otherMarketingConstraint);
            }
            else {
                otherMarketingConstraintTitle.setVisibility(View.GONE);
                otherMarketingConstraint.setVisibility(View.GONE);
            }
        }
    }
}
