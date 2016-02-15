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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerActivities extends BaseFragment {
    private final static String[] activitiesIdList = {"agriculture_pluviale", "maraichage", "arboriculture", "fl_florn", "fl_fc", "fl_f", "production_cereale", "aviculture", "elevage_bovin", "elevage_ruminants", "apiculture", "pisciculture"};
    private final static String[] livelihoodIdList = {"in_exploitation", "out_exploitation"};
    private final static String[] reasonsIdList = {"jachere", "paturage", "arbustives_savane", "arbustive", "foret_ouverte", "foret_modere", "foret_dense", "zone_humide", "urbaine_continue", "urbaine_discontinue", "steriel", "plan_eau", "moyen_limite", "force_travail_limite", "autre_inculte"};
    private final static String[] unitIdList = {"hectare", "metre_carre", "are"};

    private TextView otherReasonsTitle = null;
    private EditText otherReasons = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerActivities fragment = new FarmerActivities();
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
        View view = inflater.inflate(R.layout.sn_farmer_activities, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateActivities(view, inflater);
        updateMainSourceLivelihood(view, inflater);
        updatePlotsNumber(view);

        updateHoldingArea(view);
        updateHoldingAreaUnit(view, inflater);

        updateCultivableArea(view);
        updateCultivableUnit(view, inflater);

        updateHarvestedArea(view);
        updateHarvestedAreaUnit(view, inflater);

        updateOtherReason(view);
        updateExploitedReasons(view, inflater);

        return view;
    }

    protected void updateActivities(View parentView, LayoutInflater inflater) {
        final List<String> activities = ((SenegalSurvey) survey).getOperatingActivities();

        LinearLayout activitiesLayout1 = (LinearLayout) parentView.findViewById(R.id.activitiesGroup1);
        activitiesLayout1.setEnabled(!isModeLocked);

        LinearLayout activitiesLayout2 = (LinearLayout) parentView.findViewById(R.id.activitiesGroup2);
        activitiesLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] activitiesList =  resources.getStringArray(R.array.senegal_activities_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !activities.contains(id))
                        activities.add(id);
                }
                else {
                    activities.remove(id);
                }

                ((SenegalSurvey) survey).setOperatingActivities(activities);
            }
        };

        int index = 1;
        for (String activity: activitiesList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, activitiesLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(activitiesIdList[index - 1]);
            checkBox.setText(activity);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < activitiesList.length / 2)
                activitiesLayout1.addView(checkBox);
            else
                activitiesLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && activities.contains(id))
                checkBox.setChecked(true);

            if (activities.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateMainSourceLivelihood(View parentView, LayoutInflater inflater) {
        String livelihoodText = ((SenegalSurvey) survey).getLivelihoodSource();

        RadioGroup livelihoodGroup = (RadioGroup) parentView.findViewById(R.id.livelihoodSourceGroup);
        livelihoodGroup.setEnabled(!isModeLocked);
        livelihoodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setLivelihoodSource(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_livelihood_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, livelihoodGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(livelihoodIdList[index - 1]);
            rb.setId(index * 11);

            livelihoodGroup.addView(rb);

            if (!TextUtils.isEmpty(livelihoodText)) {
                if (rb.getTag().equals(livelihoodText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            livelihoodGroup.check(11);
    }

    protected void updatePlotsNumber(View parentView) {
        String plotsNumberText = ((SenegalSurvey) survey).getPlotsNumber();

        EditText plotsNumber = (EditText) parentView.findViewById(R.id.numberPlotsText);
        plotsNumber.setEnabled(!isModeLocked);
        plotsNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setPlotsNumber(null);
                else
                    ((SenegalSurvey) survey).setPlotsNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(plotsNumberText))
            plotsNumber.setText(plotsNumberText);
    }

    protected void updateHoldingArea(View parentView) {
        String holdingAreaText = ((SenegalSurvey) survey).getAreaSize();

        EditText holdingArea = (EditText) parentView.findViewById(R.id.holdingAreaText);
        holdingArea.setEnabled(!isModeLocked);
        holdingArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setAreaSize(null);
                else
                    ((SenegalSurvey) survey).setAreaSize(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(holdingAreaText))
            holdingArea.setText(holdingAreaText);
    }

    protected void updateHoldingAreaUnit(View parentView, LayoutInflater inflater) {
        String unitText = ((SenegalSurvey) survey).getAreaUnit();

        RadioGroup unit = (RadioGroup) parentView.findViewById(R.id.unitHoldingGroup);
        unit.setEnabled(!isModeLocked);
        unit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setAreaUnit(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_unit_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, unit, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(unitIdList[index - 1]);
            rb.setId(index * 12);

            unit.addView(rb);

            if (!TextUtils.isEmpty(unitText)) {
                if (rb.getTag().equals(unitText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            unit.check(12);
    }

    protected void updateCultivableArea(View parentView) {
        String cultivableAreaText = ((SenegalSurvey) survey).getCultivableAreaSize();

        EditText cultivableArea = (EditText) parentView.findViewById(R.id.cultivableAreaText);
        cultivableArea.setEnabled(!isModeLocked);
        cultivableArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setCultivableAreaSize(null);
                else
                    ((SenegalSurvey) survey).setCultivableAreaSize(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(cultivableAreaText))
            cultivableArea.setText(cultivableAreaText);
    }

    protected void updateCultivableUnit(View parentView, LayoutInflater inflater) {
        String unitText = ((SenegalSurvey) survey).getCultivableUnit();

        RadioGroup unit = (RadioGroup) parentView.findViewById(R.id.unitCultivableAreaGroup);
        unit.setEnabled(!isModeLocked);
        unit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setCultivableUnit(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_unit_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, unit, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(unitIdList[index - 1]);
            rb.setId(index * 13);

            unit.addView(rb);

            if (!TextUtils.isEmpty(unitText)) {
                if (rb.getTag().equals(unitText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            unit.check(13);
    }

    protected void updateHarvestedArea(View parentView) {
        String harvestedAreaText = ((SenegalSurvey) survey).getHarvestedAreaSize();

        EditText harvestedArea = (EditText) parentView.findViewById(R.id.harvestedAreaText);
        harvestedArea.setEnabled(!isModeLocked);
        harvestedArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setHarvestedAreaSize(null);
                else
                    ((SenegalSurvey) survey).setHarvestedAreaSize(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(harvestedAreaText))
            harvestedArea.setText(harvestedAreaText);
    }

    protected void updateHarvestedAreaUnit(View parentView, LayoutInflater inflater) {
        String unitText = ((SenegalSurvey) survey).getHarvestedUnit();

        RadioGroup unit = (RadioGroup) parentView.findViewById(R.id.unitHarvestedAreaGroup);
        unit.setEnabled(!isModeLocked);
        unit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                ((SenegalSurvey) survey).setHarvestedUnit(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_unit_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, unit, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(unitIdList[index - 1]);
            rb.setId(index * 14);

            unit.addView(rb);

            if (!TextUtils.isEmpty(unitText)) {
                if (rb.getTag().equals(unitText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            unit.check(14);
    }

    protected void updateExploitedReasons(View parentView, LayoutInflater inflater) {
        final List<String> exploitedReasons = ((SenegalSurvey) survey).getUncultivatedReasons();

        LinearLayout exploitedReasonsLayout = (LinearLayout) parentView.findViewById(R.id.exploitedReasonsGroup);
        exploitedReasonsLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] reasons =  resources.getStringArray(R.array.senegal_exploited_reasons_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !exploitedReasons.contains(id))
                        exploitedReasons.add(id);
                }
                else {
                    exploitedReasons.remove(id);
                }

                ((SenegalSurvey) survey).setUncultivatedReasons(exploitedReasons);
                updateOtherReasonVisibility(exploitedReasons.contains(reasonsIdList[reasonsIdList.length - 1]), true);
            }
        };

        int index = 1;
        for (String reason: reasons) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, exploitedReasonsLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 15);
            checkBox.setTag(reasonsIdList[index - 1]);
            checkBox.setText(reason);
            checkBox.setOnCheckedChangeListener(event);

            exploitedReasonsLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && exploitedReasons.contains(id))
                checkBox.setChecked(true);

            if (exploitedReasons.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateOtherReasonVisibility(boolean hasOtherReason, boolean withAnimation) {
        if (hasOtherReason) {
            if (withAnimation) {
                Helper.showView(otherReasonsTitle);
                Helper.showView(otherReasons);
            }
            else {
                otherReasonsTitle.setVisibility(View.VISIBLE);
                otherReasons.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!isModeLocked) {
                ((SenegalSurvey) survey).setOtherUncultivatedReasons(null);
                otherReasons.setText(null);
            }

            if (withAnimation) {
                Helper.fadeView(otherReasonsTitle);
                Helper.fadeView(otherReasons);
            }
            else {
                otherReasonsTitle.setVisibility(View.GONE);
                otherReasons.setVisibility(View.GONE);
            }
        }
    }

    protected void updateOtherReason(View parentView) {
        String otherReasonsText = ((SenegalSurvey) survey).getOtherUncultivatedReasons();

        otherReasonsTitle = (TextView) parentView.findViewById(R.id.otherReasonsTitle);
        otherReasons = (EditText) parentView.findViewById(R.id.otherReasonsText);
        otherReasons.setEnabled(!isModeLocked);
        otherReasons.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    ((SenegalSurvey) survey).setOtherUncultivatedReasons(null);
                else
                    ((SenegalSurvey) survey).setOtherUncultivatedReasons(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(otherReasonsText))
            otherReasons.setText(otherReasonsText);
    }
}
