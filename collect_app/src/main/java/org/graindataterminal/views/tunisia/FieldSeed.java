package org.graindataterminal.views.tunisia;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.tunisia.TunisiaCrop;
import org.odk.collect.android.R;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.views.base.BaseFragment;

public class FieldSeed extends BaseFragment {
    private static String[] answerIdList = {"oui", "non"};

    private final static String SEED_SOWN_KEY = "seed_sown";
    private final static String SEED_NAME_KEY = "seed_name";
    private final static String SEED_COST_KEY = "seed_cost";

    private ScrollView scrollView = null;
    private FieldsPager parentActivity = null;

    private EditText seedDateText = null;

    private TextView seedNameTitle = null;
    private EditText seedNameText = null;

    private TextView seedCostTitle = null;
    private EditText seedCostText = null;

    private boolean isSeedUsed = true;

    private FragmentNotificationListener notificationListener = null;

    public static Fragment getInstance (int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, index);

        FieldSeed fragment = new FieldSeed();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tn_field_seed, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        crop = field.getCrop();

        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateSeedDate(view);
        updateSeedName(view);
        updateSeedCost(view);
        updateIsSeedUsed(view, inflater);

        if (getActivity() != null) {
            parentActivity = (FieldsPager) getActivity();
            parentActivity.setNotificationListener(screenIndex,  new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (seedDateText != null && parentActivity.checkRequiredFieldByKey(screenIndex, SEED_SOWN_KEY)) {
                        seedDateText.setHint(R.string.title_required_field);
                        seedDateText.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, seedDateText.getTop());
                    }

                    if (seedNameText != null && parentActivity.checkRequiredFieldByKey(screenIndex, SEED_NAME_KEY)) {
                        seedNameText.setHint(R.string.title_required_field);
                        seedNameText.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, seedNameText.getTop());
                    }

                    if (seedCostText != null && parentActivity.checkRequiredFieldByKey(screenIndex, SEED_COST_KEY)) {
                        seedCostText.setHint(R.string.title_required_field);
                        seedCostText.setHintTextColor(getResources().getColor(R.color.color_edit_error_hint));

                        scrollView.smoothScrollTo(0, seedCostText.getTop());
                    }
                }
            });
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

    protected void updateSeedDate(View parentView) {
        String seedDate = ((TunisiaCrop) crop).getSeedSown();

        seedDateText = (EditText) parentView.findViewById(R.id.seedDateText);
        seedDateText.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        seedDateText.setEnabled(!isModeLocked);
        seedDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setSeedSown(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(seedDateText.getText().toString())) {
                                seedDateText.setText(correctDate);
                                seedDateText.setSelection(correctDate.length());
                            }

                            ((TunisiaCrop) crop).setSeedSown(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seedDate))
            seedDateText.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", seedDate));
    }

    protected void updateIsSeedUsed(View parentView, LayoutInflater inflater) {
        String isSeedUsedText = ((TunisiaCrop) crop).getSeedPurchase();

        RadioGroup seedUsedGroup = (RadioGroup) parentView.findViewById(R.id.isSeedUsedGroup);
        seedUsedGroup.setEnabled(!isModeLocked);
        seedUsedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                isSeedUsed = FieldInfo.answerIdList[0].equals(id);
                ((TunisiaCrop) crop).setSeedPurchase(id);

                updateSeedVisibility(id, true);

                notificationListener.onRequiredFieldChanged(screenIndex, SEED_NAME_KEY, isSeedUsed && TextUtils.isEmpty(seedNameText.getText()));
                notificationListener.onRequiredFieldChanged(screenIndex, SEED_COST_KEY, isSeedUsed && TextUtils.isEmpty(seedCostText.getText()));
            }
        });

        Resources resources = getResources();
        String[] answers =  resources.getStringArray(R.array.zambia_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, seedUsedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 10);

            seedUsedGroup.addView(rb);

            if (!TextUtils.isEmpty(isSeedUsedText)) {
                if (rb.getTag().equals(isSeedUsedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            seedUsedGroup.check(10);
        else
            updateSeedVisibility(isSeedUsedText, false);
    }

    protected void updateSeedVisibility(String answer, boolean withAnimation) {
        if (answerIdList[0].equals(answer)) {
            if (withAnimation) {
                Helper.showView(seedNameTitle);
                Helper.showView(seedNameText);
                Helper.showView(seedCostTitle);
                Helper.showView(seedCostText);
            }
            else {
                seedNameTitle.setVisibility(View.VISIBLE);
                seedNameText.setVisibility(View.VISIBLE);
                seedCostTitle.setVisibility(View.VISIBLE);
                seedCostText.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(seedNameTitle);
                Helper.fadeView(seedNameText);
                Helper.fadeView(seedCostTitle);
                Helper.fadeView(seedCostText);
            }
            else {
                seedNameTitle.setVisibility(View.GONE);
                seedNameText.setVisibility(View.GONE);
                seedCostTitle.setVisibility(View.GONE);
                seedCostText.setVisibility(View.GONE);
            }
        }
    }

    protected void updateSeedName(View parentView) {
        String seedName = ((TunisiaCrop) crop).getSeedName();

        seedNameTitle = (TextView) parentView.findViewById(R.id.seedNameTitle);
        seedNameText = (EditText) parentView.findViewById(R.id.seedNameText);
        seedNameText.setEnabled(!isModeLocked);
        seedNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked || !isSeedUsed)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setSeedName(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, SEED_NAME_KEY, true);
                }
                else {
                    ((TunisiaCrop) crop).setSeedName(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, SEED_NAME_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seedName))
            seedNameText.setText(seedName);
    }

    protected void updateSeedCost(View parentView) {
        String seedCost = ((TunisiaCrop) crop).getSeedCost();

        seedCostTitle = (TextView) parentView.findViewById(R.id.seedCostTitle);
        seedCostText = (EditText) parentView.findViewById(R.id.seedCostText);
        seedCostText.setEnabled(!isModeLocked);
        seedCostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked || !isSeedUsed)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((TunisiaCrop) crop).setSeedCost(null);
                    notificationListener.onRequiredFieldChanged(screenIndex, SEED_COST_KEY, true);
                } else {
                    ((TunisiaCrop) crop).setSeedCost(s.toString());
                    notificationListener.onRequiredFieldChanged(screenIndex, SEED_COST_KEY, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seedCost))
            seedCostText.setText(seedCost);
    }
}
