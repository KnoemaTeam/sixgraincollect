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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerCPPOrders;
import org.graindataterminal.models.cameroon.FarmerGeneralInfo;
import org.graindataterminal.models.cameroon.FarmerSustainability;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerCppOrders extends BaseFragment {
    private final static String[] cppOrdersSourceIdList = {"dealers", "market", "cooperative"};
    private final static String[] cppOrdersBuyersIdList = {"me", "buy_cooperative", "buy_officer", "buy_dealer"};
    private final static String[] cppOrdersTechniqueIdList = {"t_officer", "t_cooperative", "t_dealer"};

    private TextView cppOrderTechniqueTitle = null;
    private LinearLayout cppTechniqueProviderLayout = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCppOrders fragment = new FarmerCppOrders();
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
        View view = inflater.inflate(R.layout.cm_farmer_cpp_orders, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCppOrdersSource(view, inflater);
        updateCppOrdersBuyers(view, inflater);
        updateCppOrdersTechniqueProvider(view, inflater);
        updateCppOrdersAdviceReceived(view, inflater);

        return view;
    }

    protected void updateCppOrdersSource(View parentView, LayoutInflater inflater) {
        final FarmerCPPOrders cppOrders = ((CameroonSurvey) survey).getFarmerCPPOrders();
        final List<String> sources = cppOrders.getCppSourceList();

        LinearLayout cppSourceLayout = (LinearLayout) parentView.findViewById(R.id.cppOrderSourceGroup);
        cppSourceLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sourceList = resources.getStringArray(R.array.cameroon_cpp_order_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                cppOrders.setCppSourceList(sources);
                ((CameroonSurvey) survey).setFarmerCPPOrders(cppOrders);
            }
        };

        int index = 1;
        for (String source: sourceList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, cppSourceLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(cppOrdersSourceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            cppSourceLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateCppOrdersBuyers(View parentView, LayoutInflater inflater) {
        final FarmerCPPOrders cppOrders = ((CameroonSurvey) survey).getFarmerCPPOrders();
        final List<String> sources = cppOrders.getCppBuyReasonList();

        LinearLayout cppSourceLayout1 = (LinearLayout) parentView.findViewById(R.id.cppOrdersBuyerGroup1);
        cppSourceLayout1.setEnabled(!isModeLocked);

        LinearLayout cppSourceLayout2 = (LinearLayout) parentView.findViewById(R.id.cppOrdersBuyerGroup2);
        cppSourceLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sourceList = resources.getStringArray(R.array.cameroon_cpp_order_buyer_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                cppOrders.setCppBuyReasonList(sources);
                ((CameroonSurvey) survey).setFarmerCPPOrders(cppOrders);
            }
        };

        int index = 1;
        for (String source: sourceList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, cppSourceLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(cppOrdersBuyersIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sourceList.length / 2)
                cppSourceLayout1.addView(checkBox);
            else
                cppSourceLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateCppOrdersAdviceReceived(View parentView, LayoutInflater inflater) {
        final FarmerCPPOrders cppOrders = ((CameroonSurvey) survey).getFarmerCPPOrders();
        String isAdviceReceivedText = cppOrders.getIsAdviceReceived();

        RadioGroup adviceReceivedGroup = (RadioGroup) parentView.findViewById(R.id.cppOrdersAdviceReceivedGroup);
        adviceReceivedGroup.setEnabled(!isModeLocked);
        adviceReceivedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                updateControlVisibility(id, true);

                if (!CameroonSurvey.answerIdList[0].equals(id)) {
                    clearSelectionInViewGroup(cppTechniqueProviderLayout);
                    selectElementInViewGroup(cppTechniqueProviderLayout, 13);
                }

                cppOrders.setIsAdviceReceived(id);
                ((CameroonSurvey) survey).setFarmerCPPOrders(cppOrders);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, adviceReceivedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(CameroonSurvey.answerIdList[index - 1]);
            rb.setId(index * 12);

            adviceReceivedGroup.addView(rb);

            if (!TextUtils.isEmpty(isAdviceReceivedText)) {
                if (rb.getTag().equals(isAdviceReceivedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            adviceReceivedGroup.check(12);
        else
            updateControlVisibility(isAdviceReceivedText, false);
    }

    protected void updateControlVisibility(String answer, boolean withAnimation) {
        if (CameroonSurvey.answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(cppOrderTechniqueTitle);
                Helper.showView(cppTechniqueProviderLayout);
            }
            else {
                cppOrderTechniqueTitle.setVisibility(View.VISIBLE);
                cppTechniqueProviderLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(cppOrderTechniqueTitle);
                Helper.fadeView(cppTechniqueProviderLayout);
            }
            else {
                cppOrderTechniqueTitle.setVisibility(View.GONE);
                cppTechniqueProviderLayout.setVisibility(View.GONE);
            }
        }
    }

    protected void updateCppOrdersTechniqueProvider(View parentView, LayoutInflater inflater) {
        final FarmerCPPOrders cppOrders = ((CameroonSurvey) survey).getFarmerCPPOrders();
        final List<String> sources = cppOrders.getAdviceSourceList();

        cppOrderTechniqueTitle = (TextView) parentView.findViewById(R.id.cppOrderTechniqueTitle);
        cppTechniqueProviderLayout = (LinearLayout) parentView.findViewById(R.id.cppOrderTechniqueGroup);
        cppTechniqueProviderLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sourceList = resources.getStringArray(R.array.cameroon_cpp_order_technique_provider_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sources.contains(id))
                        sources.add(id);
                }
                else {
                    sources.remove(id);
                }

                cppOrders.setAdviceSourceList(sources);
                ((CameroonSurvey) survey).setFarmerCPPOrders(cppOrders);
            }
        };

        int index = 1;
        for (String source: sourceList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, cppTechniqueProviderLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(cppOrdersTechniqueIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            cppTechniqueProviderLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
