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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.AgriculturalServicesGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerCreditInfo extends BaseFragment {
    private final static String[] creditTypeIdList = {"credit_inves", "credit_camp"};
    private final static String[] creditDestinationIdList = {"extention_exploitation", "travaux_amenagement_terre", "installation_irrigation", "construction_infrastructure", "achat_materiel", "charge_fonctionnement", "autre_destination"};

    private TextView creditTypeTitle = null;
    private LinearLayout creditTypeLayout = null;

    private TextView creditAmountTitle = null;
    private EditText creditAmount = null;

    private TextView creditSourceTitle = null;
    private LinearLayout creditSourceLayout1 = null;
    private LinearLayout creditSourceLayout2 = null;

    private TextView completionDateTitle = null;
    private EditText completionDate = null;

    private TextView firstDateTitle = null;
    private EditText firstDate = null;

    private TextView lastDateTitle = null;
    private EditText lastDate = null;

    private TextView creditDestinationTitle = null;
    private LinearLayout creditDestinationLayout1 = null;
    private LinearLayout creditDestinationLayout2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerCreditInfo fragment = new FarmerCreditInfo();
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
        View view = inflater.inflate(R.layout.sn_farmer_credit_info, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCreditType(view, inflater);
        updateCreditAmount(view);
        updateCreditSource(view, inflater);
        updateCreditCompletionDate(view);
        updateCreditFirstDate(view);
        updateCreditLastDate(view);
        updateCreditDestination(view, inflater);
        updateHasCredit(view, inflater);

        return view;
    }

    protected void updateHasCredit(View parentView, LayoutInflater inflater) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        String hasCreditText = servicesGroup.getHasCredit();

        RadioGroup hasCreditGroup = (RadioGroup) parentView.findViewById(R.id.hasCreditGroup);
        hasCreditGroup.setEnabled(!isModeLocked);
        hasCreditGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                servicesGroup.setHasCredit(id);
                ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);

                updateCreditInfoVisibility(id, true);
            }
        });

        Resources resources = getResources();
        String[] answers = resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String answer: answers) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasCreditGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(answer);
            rb.setTag(SenegalSurvey.answerIdList[index - 1]);
            rb.setId(index * 10);

            hasCreditGroup.addView(rb);

            if (!TextUtils.isEmpty(hasCreditText)) {
                if (rb.getTag().equals(hasCreditText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasCreditGroup.check(10);
        else
            updateCreditInfoVisibility(hasCreditText, false);
    }

    protected void updateCreditInfoVisibility(String answer, boolean withAnimation) {
        if (SenegalSurvey.answerIdList[0].equalsIgnoreCase(answer)) {
            if (withAnimation) {
                Helper.showView(creditTypeTitle);
                Helper.showView(creditTypeLayout);
                Helper.showView(creditAmountTitle);
                Helper.showView(creditAmount);
                Helper.showView(creditSourceTitle);
                Helper.showView(creditSourceLayout1);
                Helper.showView(creditSourceLayout2);
                Helper.showView(completionDateTitle);
                Helper.showView(completionDate);
                Helper.showView(firstDateTitle);
                Helper.showView(firstDate);
                Helper.showView(lastDateTitle);
                Helper.showView(lastDate);
                Helper.showView(creditDestinationTitle);
                Helper.showView(creditDestinationLayout1);
                Helper.showView(creditDestinationLayout2);
            }
            else {
                creditTypeTitle.setVisibility(View.VISIBLE);
                creditTypeLayout.setVisibility(View.VISIBLE);
                creditAmountTitle.setVisibility(View.VISIBLE);
                creditAmount.setVisibility(View.VISIBLE);
                creditSourceTitle.setVisibility(View.VISIBLE);
                creditSourceLayout1.setVisibility(View.VISIBLE);
                creditSourceLayout2.setVisibility(View.VISIBLE);
                completionDateTitle.setVisibility(View.VISIBLE);
                completionDate.setVisibility(View.VISIBLE);
                firstDateTitle.setVisibility(View.VISIBLE);
                firstDate.setVisibility(View.VISIBLE);
                lastDateTitle.setVisibility(View.VISIBLE);
                lastDate.setVisibility(View.VISIBLE);

                creditDestinationTitle.setVisibility(View.VISIBLE);
                creditDestinationLayout1.setVisibility(View.VISIBLE);
                creditDestinationLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(creditTypeTitle);
                Helper.fadeView(creditTypeLayout);
                Helper.fadeView(creditAmountTitle);
                Helper.fadeView(creditAmount);
                Helper.fadeView(creditSourceTitle);
                Helper.fadeView(creditSourceLayout1);
                Helper.fadeView(creditSourceLayout2);
                Helper.fadeView(completionDateTitle);
                Helper.fadeView(completionDate);
                Helper.fadeView(firstDateTitle);
                Helper.fadeView(firstDate);
                Helper.fadeView(lastDateTitle);
                Helper.fadeView(lastDate);
                Helper.fadeView(creditDestinationTitle);
                Helper.fadeView(creditDestinationLayout1);
                Helper.fadeView(creditDestinationLayout2);
            }
            else {
                creditTypeTitle.setVisibility(View.GONE);
                creditTypeLayout.setVisibility(View.GONE);
                creditAmountTitle.setVisibility(View.GONE);
                creditAmount.setVisibility(View.GONE);
                creditSourceTitle.setVisibility(View.GONE);
                creditSourceLayout1.setVisibility(View.GONE);
                creditSourceLayout2.setVisibility(View.GONE);
                completionDateTitle.setVisibility(View.GONE);
                completionDate.setVisibility(View.GONE);
                firstDateTitle.setVisibility(View.GONE);
                firstDate.setVisibility(View.GONE);
                lastDateTitle.setVisibility(View.GONE);
                lastDate.setVisibility(View.GONE);
                creditDestinationTitle.setVisibility(View.GONE);
                creditDestinationLayout1.setVisibility(View.GONE);
                creditDestinationLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateCreditType(View parentView, LayoutInflater inflater) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        final List<String> types = servicesGroup.getCreditType();

        creditTypeTitle = (TextView) parentView.findViewById(R.id.creditTypeTitle);
        creditTypeLayout = (LinearLayout) parentView.findViewById(R.id.creditTypeGroup);
        creditTypeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSources = resources.getStringArray(R.array.senegal_credit_type_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !types.contains(id))
                        types.add(id);
                }
                else {
                    types.remove(id);
                }

                servicesGroup.setCreditType(types);
                ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String type: typesSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditTypeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(creditTypeIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            creditTypeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateCreditAmount(View parentView) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        String creditAmountText = servicesGroup.getCreditAmount();

        creditAmountTitle = (TextView) parentView.findViewById(R.id.creditAmountTitle);
        creditAmount = (EditText) parentView.findViewById(R.id.creditAmountText);
        creditAmount.setEnabled(!isModeLocked);
        creditAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    servicesGroup.setCreditAmount(null);
                else
                    servicesGroup.setCreditAmount(s.toString());

                ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(creditAmountText))
            creditAmount.setText(creditAmountText);
    }

    protected void updateCreditSource(View parentView, LayoutInflater inflater) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        final List<String> sources = servicesGroup.getCreditSource();

        creditSourceTitle = (TextView) parentView.findViewById(R.id.creditSourceTitle);
        creditSourceLayout1 = (LinearLayout) parentView.findViewById(R.id.creditSourceGroup1);
        creditSourceLayout1.setEnabled(!isModeLocked);

        creditSourceLayout2 = (LinearLayout) parentView.findViewById(R.id.creditSourceGroup2);
        creditSourceLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] creditSources = resources.getStringArray(R.array.senegal_credit_source_list);

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

                servicesGroup.setCreditSource(sources);
                ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String source: creditSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditSourceLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(SenegalSurvey.creditSourceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= creditSources.length / 2)
                creditSourceLayout1.addView(checkBox);
            else
                creditSourceLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sources.contains(id))
                checkBox.setChecked(true);

            if (sources.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateCreditCompletionDate(View parentView) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        String completionDateText = servicesGroup.getCreditCompletionDate();

        completionDateTitle = (TextView) parentView.findViewById(R.id.creditDateCompletionTitle);
        completionDate = (EditText) parentView.findViewById(R.id.creditDateCompletionText);
        completionDate.setEnabled(!isModeLocked);
        completionDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        completionDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((SenegalSurvey) survey).getAgriculturalServicesGroup().setCreditCompletionDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(completionDate.getText().toString())) {
                                completionDate.setText(correctDate);
                                completionDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setCreditCompletionDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(completionDateText))
            completionDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", completionDateText));
    }

    protected void updateCreditFirstDate(View parentView) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        String firstDateText = servicesGroup.getCreditFirstDateRepayment();

        firstDateTitle = (TextView) parentView.findViewById(R.id.creditFirstDateTitle);
        firstDate = (EditText) parentView.findViewById(R.id.creditFirstDateText);
        firstDate.setEnabled(!isModeLocked);
        firstDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        firstDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((SenegalSurvey) survey).getAgriculturalServicesGroup().setCreditFirstDateRepayment(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(firstDate.getText().toString())) {
                                firstDate.setText(correctDate);
                                firstDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setCreditFirstDateRepayment(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(firstDateText))
            firstDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", firstDateText));
    }

    protected void updateCreditLastDate(View parentView) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        String lastDateText = servicesGroup.getCreditLastDateRepayment();

        lastDateTitle = (TextView) parentView.findViewById(R.id.creditLastDateTitle);
        lastDate = (EditText) parentView.findViewById(R.id.creditLastDateText);
        lastDate.setEnabled(!isModeLocked);
        lastDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        lastDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    ((SenegalSurvey) survey).getAgriculturalServicesGroup().setCreditLastDateRepayment(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(lastDate.getText().toString())) {
                                lastDate.setText(correctDate);
                                lastDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setCreditLastDateRepayment(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(lastDateText))
            lastDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", lastDateText));
    }

    protected void updateCreditDestination(View parentView, LayoutInflater inflater) {
        final AgriculturalServicesGroup servicesGroup = ((SenegalSurvey) survey).getAgriculturalServicesGroup();
        final List<String> destinations = servicesGroup.getCreditDestinations();

        creditDestinationTitle = (TextView) parentView.findViewById(R.id.creditDestinationTitle);
        creditDestinationLayout1 = (LinearLayout) parentView.findViewById(R.id.creditDestinationGroup1);
        creditDestinationLayout1.setEnabled(!isModeLocked);

        creditDestinationLayout2 = (LinearLayout) parentView.findViewById(R.id.creditDestinationGroup2);
        creditDestinationLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] destinationSources =  resources.getStringArray(R.array.senegal_credit_destination_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !destinations.contains(id))
                        destinations.add(id);
                }
                else {
                    destinations.remove(id);
                }

                servicesGroup.setCreditDestinations(destinations);
                ((SenegalSurvey) survey).setAgriculturalServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String destination: destinationSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditDestinationLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(creditDestinationIdList[index - 1]);
            checkBox.setText(destination);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= destinationSources.length / 2)
                creditDestinationLayout1.addView(checkBox);
            else
                creditDestinationLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && destinations.contains(id))
                checkBox.setChecked(true);

            if (destinations.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
