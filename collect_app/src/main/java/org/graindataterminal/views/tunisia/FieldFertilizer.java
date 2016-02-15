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
import android.widget.TextView;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaCrop;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

public class FieldFertilizer extends BaseFragment {
    private static String[] answerIdList = {"oui", "non"};
    private final static String FERTILIZER_AMOUNT_KEY = "fertilizer_amount";
    private final static String FERTILIZER_COST_KEY = "fertilizer_cost";

    private TextView fertilizerAmountTitle = null;
    private EditText fertilizerAmount = null;

    private TextView fertilizerCostTitle = null;
    private EditText fertilizerCost = null;

    private ScrollView scrollView = null;
    private FieldsPager parentActivity = null;

    private boolean isFertilizerUsed = true;

    private FragmentNotificationListener notificationListener = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldFertilizer fragment = new FieldFertilizer();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            screenIndex = bundle.getInt(SCREEN_INDEX);
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
        View view = inflater.inflate(R.layout.tn_field_fertilizer, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFertilizerAmount(view);
        updateFertilizerCost(view);
        updateIsFertilizerUsed(view, inflater);

        if (getActivity() != null) {
            parentActivity = (FieldsPager) getActivity();
            parentActivity.setNotificationListener(screenIndex,  new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (fertilizerAmount != null && parentActivity.checkRequiredFieldByKey(screenIndex, FERTILIZER_AMOUNT_KEY)) {
                        fertilizerAmount.setHint(R.string.title_required_field);
                        fertilizerAmount.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, fertilizerAmount.getTop());
                    }

                    if (fertilizerCost != null && parentActivity.checkRequiredFieldByKey(screenIndex, FERTILIZER_COST_KEY)) {
                        fertilizerCost.setHint(R.string.title_required_field);
                        fertilizerCost.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, fertilizerCost.getTop());
                    }
                }
            });
        }

        return view;
    }

    protected void updateIsFertilizerUsed(View parentView, LayoutInflater inflater) {
        String isFertilizerUsedText = ((TunisiaCrop) crop).getIsFertilizerUsed();

        RadioGroup isFertilizerUsedGroup = (RadioGroup) parentView.findViewById(R.id.isFertilizerUsedGroup);
        isFertilizerUsedGroup.setEnabled(!isModeLocked);
        isFertilizerUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                isFertilizerUsed = answerIdList[0].equals(id);
                ((TunisiaCrop) crop).setIsFertilizerUsed(id);

                updateFertilizerVisibility(id, true);

                notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_AMOUNT_KEY, isFertilizerUsed && TextUtils.isEmpty(fertilizerAmount.getText()));
                notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_COST_KEY, isFertilizerUsed && TextUtils.isEmpty(fertilizerCost.getText()));
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, isFertilizerUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            isFertilizerUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isFertilizerUsedText)) {
                if (rb.getTag().equals(isFertilizerUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            isFertilizerUsedGroup.check(10);
        else
            updateFertilizerVisibility(isFertilizerUsedText, false);
    }

    protected void updateFertilizerVisibility(String answer, boolean withAnimation) {
        if (answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(fertilizerAmountTitle);
                Helper.showView(fertilizerAmount);
                Helper.showView(fertilizerCostTitle);
                Helper.showView(fertilizerCost);
            }
            else {
                fertilizerAmountTitle.setVisibility(View.VISIBLE);
                fertilizerAmount.setVisibility(View.VISIBLE);
                fertilizerCostTitle.setVisibility(View.VISIBLE);
                fertilizerCost.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(fertilizerAmountTitle);
                Helper.fadeView(fertilizerAmount);
                Helper.fadeView(fertilizerCostTitle);
                Helper.fadeView(fertilizerCost);
            }
            else {
                fertilizerAmountTitle.setVisibility(View.GONE);
                fertilizerAmount.setVisibility(View.GONE);
                fertilizerCostTitle.setVisibility(View.GONE);
                fertilizerCost.setVisibility(View.GONE);
            }
        }
    }

    protected void updateFertilizerAmount(View parentView) {
        String fertilizerAmountText = ((TunisiaCrop) crop).getFertilizerAmount();

        fertilizerAmountTitle = (TextView) parentView.findViewById(R.id.fertilizerAmountTitle);
        fertilizerAmount = (EditText) parentView.findViewById(R.id.fertilizerAmountText);
        fertilizerAmount.setEnabled(!isModeLocked);
        fertilizerAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked || !isFertilizerUsed)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setFertilizerAmount(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_AMOUNT_KEY, true);
                }
                else {
                    ((TunisiaCrop) crop).setFertilizerAmount(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_AMOUNT_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fertilizerAmountText))
            fertilizerAmount.setText(fertilizerAmountText);
    }

    protected void updateFertilizerCost(View parentView) {
        String fertilizerCostText = ((TunisiaCrop) crop).getFertilizerCost();

        fertilizerCostTitle = (TextView) parentView.findViewById(R.id.fertilizerCostTitle);
        fertilizerCost = (EditText) parentView.findViewById(R.id.fertilizerCostText);
        fertilizerCost.setEnabled(!isModeLocked);
        fertilizerCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked || !isFertilizerUsed)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setFertilizerCost(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_COST_KEY, true);
                } else {
                    ((TunisiaCrop) crop).setFertilizerCost(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, FERTILIZER_COST_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(fertilizerCostText))
            fertilizerCost.setText(fertilizerCostText);
    }
}
