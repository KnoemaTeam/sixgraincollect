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
import org.graindataterminal.models.senegal.LivestockServicesGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerLivestockServices extends BaseFragment {
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

    private TextView creditUsageTitle = null;
    private LinearLayout creditUsageLayout1 = null;
    private LinearLayout creditUsageLayout2 = null;

    private TextView creditInfoTitle = null;
    private LinearLayout creditInfoLayout1 = null;
    private LinearLayout creditInfoLayout2 = null;

    private final static String[] creditDestinationIdList = {"ex_expl", "ch_fonc", "ac_equip", "atr_dest_crd_ele"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerLivestockServices fragment = new FarmerLivestockServices();
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
        View view = inflater.inflate(R.layout.sn_farmer_livestock_servicies, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateCreditAmount(view);
        updateCreditSource(view, inflater);
        updateCreditCompletionDate(view);
        updateCreditFirstDate(view);
        updateCreditLastDate(view);
        updateCreditUsage(view, inflater);
        updateCreditInfo(view, inflater);
        updateHasCredit(view, inflater);

        return view;
    }

    protected void updateHasCredit(View parentView, LayoutInflater inflater) {
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        String hasCreditText = servicesGroup.getHasLivestockCredit();

        RadioGroup hasCreditGroup = (RadioGroup) parentView.findViewById(R.id.hasLivestockCreditGroup);
        hasCreditGroup.setEnabled(!isModeLocked);
        hasCreditGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                servicesGroup.setHasLivestockCredit(id);
                ((SenegalSurvey) survey).setServicesGroup(servicesGroup);

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
                Helper.showView(creditUsageTitle);
                Helper.showView(creditUsageLayout1);
                Helper.showView(creditUsageLayout2);
                Helper.showView(creditInfoTitle);
                Helper.showView(creditInfoLayout1);
                Helper.showView(creditInfoLayout2);
            } else {
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
                creditUsageTitle.setVisibility(View.VISIBLE);
                creditUsageLayout1.setVisibility(View.VISIBLE);
                creditUsageLayout2.setVisibility(View.VISIBLE);
                creditInfoTitle.setVisibility(View.VISIBLE);
                creditInfoLayout1.setVisibility(View.VISIBLE);
                creditInfoLayout2.setVisibility(View.VISIBLE);
            }
        } else {
            if (withAnimation) {
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
                Helper.fadeView(creditUsageTitle);
                Helper.fadeView(creditUsageLayout1);
                Helper.fadeView(creditUsageLayout2);
                Helper.fadeView(creditInfoTitle);
                Helper.fadeView(creditInfoLayout1);
                Helper.fadeView(creditInfoLayout2);
            } else {
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
                creditUsageTitle.setVisibility(View.GONE);
                creditUsageLayout1.setVisibility(View.GONE);
                creditUsageLayout2.setVisibility(View.GONE);
                creditInfoTitle.setVisibility(View.GONE);
                creditInfoLayout1.setVisibility(View.GONE);
                creditInfoLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateCreditAmount(View parentView) {
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        String creditAmountText = servicesGroup.getLivestockCreditAmount();

        creditAmountTitle = (TextView) parentView.findViewById(R.id.livestockCreditAmountTitle);
        creditAmount = (EditText) parentView.findViewById(R.id.livestockCreditAmountText);
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
                    servicesGroup.setLivestockCreditAmount(null);
                else
                    servicesGroup.setLivestockCreditAmount(s.toString());

                ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(creditAmountText))
            creditAmount.setText(creditAmountText);
    }

    protected void updateCreditSource(View parentView, LayoutInflater inflater) {
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        final List<String> sources = servicesGroup.getLivestockCreditSource();

        creditSourceTitle = (TextView) parentView.findViewById(R.id.livestockCreditSourceTitle);
        creditSourceLayout1 = (LinearLayout) parentView.findViewById(R.id.livestockCreditSourceGroup1);
        creditSourceLayout1.setEnabled(!isModeLocked);

        creditSourceLayout2 = (LinearLayout) parentView.findViewById(R.id.livestockCreditSourceGroup2);
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

                servicesGroup.setLivestockCreditSource(sources);
                ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String source: creditSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditSourceLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
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
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        String completionDateText = servicesGroup.getLivestockCreditCompletionDate();

        completionDateTitle = (TextView) parentView.findViewById(R.id.livestockCreditDateCompletionTitle);
        completionDate = (EditText) parentView.findViewById(R.id.livestockCreditDateCompletionText);
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
                    servicesGroup.setLivestockCreditCompletionDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(completionDate.getText().toString())) {
                                completionDate.setText(correctDate);
                                completionDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setLivestockCreditCompletionDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
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
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        String firstDateText = servicesGroup.getLivestockCreditFirstDate();

        firstDateTitle = (TextView) parentView.findViewById(R.id.livestockCreditFirstDateTitle);
        firstDate = (EditText) parentView.findViewById(R.id.livestockCreditFirstDateText);
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
                    servicesGroup.setLivestockCreditFirstDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(firstDate.getText().toString())) {
                                firstDate.setText(correctDate);
                                firstDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setLivestockCreditFirstDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
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
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        String lastDateText = servicesGroup.getLivestockCreditLastDate();

        lastDateTitle = (TextView) parentView.findViewById(R.id.livestockCreditLastDateTitle);
        lastDate = (EditText) parentView.findViewById(R.id.livestockCreditLastDateText);
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
                    servicesGroup.setLivestockCreditLastDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(lastDate.getText().toString())) {
                                lastDate.setText(correctDate);
                                lastDate.setSelection(correctDate.length());
                            }

                            servicesGroup.setLivestockCreditLastDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                            ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
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

    protected void updateCreditUsage(View parentView, LayoutInflater inflater) {
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        final List<String> usages = servicesGroup.getLivestockCreditUsage();

        creditUsageTitle = (TextView) parentView.findViewById(R.id.livestockCreditUsageTitle);
        creditUsageLayout1 = (LinearLayout) parentView.findViewById(R.id.livestockCreditUsageGroup1);
        creditUsageLayout1.setEnabled(!isModeLocked);

        creditUsageLayout2 = (LinearLayout) parentView.findViewById(R.id.livestockCreditUsageGroup2);
        creditUsageLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] destinationSources =  resources.getStringArray(R.array.senegal_livestock_credit_destination_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !usages.contains(id))
                        usages.add(id);
                }
                else {
                    usages.remove(id);
                }

                servicesGroup.setLivestockCreditUsage(usages);
                ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String destination: destinationSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditUsageLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(creditDestinationIdList[index - 1]);
            checkBox.setText(destination);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < destinationSources.length / 2)
                creditUsageLayout1.addView(checkBox);
            else
                creditUsageLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && usages.contains(id))
                checkBox.setChecked(true);

            if (usages.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateCreditInfo(View parentView, LayoutInflater inflater) {
        final LivestockServicesGroup servicesGroup = ((SenegalSurvey) survey).getServicesGroup();
        final List<String> info =servicesGroup.getLivestockCreditInfo();

        creditInfoTitle = (TextView) parentView.findViewById(R.id.livestockCreditInfoTitle);
        creditInfoLayout1 = (LinearLayout) parentView.findViewById(R.id.livestockCreditInfoGroup1);
        creditInfoLayout1.setEnabled(!isModeLocked);

        creditInfoLayout2 = (LinearLayout) parentView.findViewById(R.id.livestockCreditInfoGroup2);
        creditInfoLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] destinationSources =  resources.getStringArray(R.array.senegal_agricultural_information_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !info.contains(id))
                        info.add(id);
                }
                else {
                    info.remove(id);
                }

                servicesGroup.setLivestockCreditInfo(info);
                ((SenegalSurvey) survey).setServicesGroup(servicesGroup);
            }
        };

        int index = 1;
        for (String destination: destinationSources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, creditInfoLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(SenegalSurvey.informationSourceIdList[index - 1]);
            checkBox.setText(destination);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= destinationSources.length / 2)
                creditInfoLayout1.addView(checkBox);
            else
                creditInfoLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && info.contains(id))
                checkBox.setChecked(true);

            if (info.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
