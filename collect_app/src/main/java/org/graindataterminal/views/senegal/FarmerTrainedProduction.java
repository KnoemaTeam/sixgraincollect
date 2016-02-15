package org.graindataterminal.views.senegal;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.senegal.TrainedProductionGroup;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerTrainedProduction extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "pordin", "volaille", "autr_prod"};
    private final static String[] race1IdList = {"bovin", "ovin", "caprin", "porcin", "at"};
    private final static String[] race2IdList = {"bovin", "equin", "atrr", "atre"};
    private final static String[] race3IdList = {"bovin", "equin", "a_tre"};

    private TextView producersTitle = null;
    private LinearLayout producersLayout1 = null;
    private LinearLayout producersLayout2 = null;

    private TextView meatTitle = null;
    private LinearLayout meatLayout1 = null;
    private LinearLayout meatLayout2 = null;

    private TextView milkTitle = null;
    private LinearLayout milkLayout1 = null;
    private LinearLayout milkLayout2 = null;

    private TextView animalsTitle = null;
    private LinearLayout animalsLayout1 = null;
    private LinearLayout animalsLayout2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerTrainedProduction fragment = new FarmerTrainedProduction();
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
        View view = inflater.inflate(R.layout.sn_farmer_trained_production, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateDraughtAnimals(view, inflater);
        updateMilkProductions(view, inflater);
        updateMeatProductions(view, inflater);
        updateLivestockProducers(view, inflater);
        updateTrainedProductionType(view, inflater);

        return view;
    }

    protected void updateTrainedProductionType(View parentView, LayoutInflater inflater) {
        final TrainedProductionGroup productionGroup = ((SenegalSurvey) survey).getTrainedProductionGroup();
        final List<String> types = productionGroup.getTrainedProductionTypes();

        LinearLayout typeLayout1 = (LinearLayout) parentView.findViewById(R.id.trainedProductionTypeGroup1);
        typeLayout1.setEnabled(!isModeLocked);

        LinearLayout typeLayout2 = (LinearLayout) parentView.findViewById(R.id.trainedProductionTypeGroup2);
        typeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource = resources.getStringArray(R.array.senegal_animal_trainde_production_list);

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

                productionGroup.setTrainedProductionTypes(types);
                ((SenegalSurvey) survey).setTrainedProductionGroup(productionGroup);

                updateСontentVisibility(types, true);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(TrainedProductionGroup.productionIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= typesSource.length / 2)
                typeLayout1.addView(checkBox);
            else
                typeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }

        updateСontentVisibility(types, false);
    }

    protected void updateСontentVisibility(List<String> answers, boolean withAnimation) {
        if (answers.contains(TrainedProductionGroup.productionIdList[0])) {
            if (withAnimation) {
                Helper.showView(producersTitle);
                Helper.showView(producersLayout1);
                Helper.showView(producersLayout2);
            }
            else {
                producersTitle.setVisibility(View.VISIBLE);
                producersLayout1.setVisibility(View.VISIBLE);
                producersLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(producersTitle);
                Helper.fadeView(producersLayout1);
                Helper.fadeView(producersLayout2);
            }
            else {
                producersTitle.setVisibility(View.GONE);
                producersLayout1.setVisibility(View.GONE);
                producersLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(TrainedProductionGroup.productionIdList[1])) {
            if (withAnimation) {
                Helper.showView(meatTitle);
                Helper.showView(meatLayout1);
                Helper.showView(meatLayout2);
            }
            else {
                meatTitle.setVisibility(View.VISIBLE);
                meatLayout1.setVisibility(View.VISIBLE);
                meatLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(meatTitle);
                Helper.fadeView(meatLayout1);
                Helper.fadeView(meatLayout2);
            }
            else {
                meatTitle.setVisibility(View.GONE);
                meatLayout1.setVisibility(View.GONE);
                meatLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(TrainedProductionGroup.productionIdList[2])) {
            if (withAnimation) {
                Helper.showView(milkTitle);
                Helper.showView(milkLayout1);
                Helper.showView(milkLayout2);
            }
            else {
                milkTitle.setVisibility(View.VISIBLE);
                milkLayout1.setVisibility(View.VISIBLE);
                milkLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(milkTitle);
                Helper.fadeView(milkLayout1);
                Helper.fadeView(milkLayout2);
            }
            else {
                milkTitle.setVisibility(View.GONE);
                milkLayout1.setVisibility(View.GONE);
                milkLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(TrainedProductionGroup.productionIdList[TrainedProductionGroup.productionIdList.length - 2])) {
            if (withAnimation) {
                Helper.showView(animalsTitle);
                Helper.showView(animalsLayout1);
                //Helper.showView(milkLayout2);
            }
            else {
                animalsTitle.setVisibility(View.VISIBLE);
                animalsLayout1.setVisibility(View.VISIBLE);
                //animalsLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(animalsTitle);
                Helper.fadeView(animalsLayout1);
                //Helper.fadeView(animalsLayout2);
            }
            else {
                animalsTitle.setVisibility(View.GONE);
                animalsLayout1.setVisibility(View.GONE);
                //animalsLayout1.setVisibility(View.GONE);
            }
        }
    }

    protected void updateLivestockProducers(View parentView, LayoutInflater inflater) {
        final TrainedProductionGroup productionGroup = ((SenegalSurvey) survey).getTrainedProductionGroup();
        final List<String> types = productionGroup.getLivestockProducers();

        producersTitle = (TextView) parentView.findViewById(R.id.livestockProducerTitle);
        producersLayout1 = (LinearLayout) parentView.findViewById(R.id.livestockProducerGroup1);
        producersLayout1.setEnabled(!isModeLocked);

        producersLayout2 = (LinearLayout) parentView.findViewById(R.id.livestockProducerGroup2);
        producersLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_list);

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

                productionGroup.setLivestockProducers(types);
                ((SenegalSurvey) survey).setTrainedProductionGroup(productionGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, producersLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= sources.length / 2)
                producersLayout1.addView(checkBox);
            else
                producersLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateMeatProductions(View parentView, LayoutInflater inflater) {
        final TrainedProductionGroup productionGroup = ((SenegalSurvey) survey).getTrainedProductionGroup();
        final List<String> types = productionGroup.getMeatProductions();

        meatTitle = (TextView) parentView.findViewById(R.id.meatProductionTitle);
        meatLayout1 = (LinearLayout) parentView.findViewById(R.id.meatProductionGroup1);
        meatLayout1.setEnabled(!isModeLocked);

        meatLayout2 = (LinearLayout) parentView.findViewById(R.id.meatProductionGroup2);
        meatLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_1_list);

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

                productionGroup.setMeatProductions(types);
                ((SenegalSurvey) survey).setTrainedProductionGroup(productionGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, meatLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(race1IdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= sources.length / 2)
                meatLayout1.addView(checkBox);
            else
                meatLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateMilkProductions(View parentView, LayoutInflater inflater) {
        final TrainedProductionGroup productionGroup = ((SenegalSurvey) survey).getTrainedProductionGroup();
        final List<String> types = productionGroup.getMilkProductions();

        milkTitle = (TextView) parentView.findViewById(R.id.milkProductionTitle);
        milkLayout1 = (LinearLayout) parentView.findViewById(R.id.milkProductionGroup1);
        milkLayout1.setEnabled(!isModeLocked);

        milkLayout2 = (LinearLayout) parentView.findViewById(R.id.milkProductionGroup2);
        milkLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_2_list);

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

                productionGroup.setMilkProductions(types);
                ((SenegalSurvey) survey).setTrainedProductionGroup(productionGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, milkLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(race2IdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= sources.length / 2)
                milkLayout1.addView(checkBox);
            else
                milkLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateDraughtAnimals(View parentView, LayoutInflater inflater) {
        final TrainedProductionGroup productionGroup = ((SenegalSurvey) survey).getTrainedProductionGroup();
        final List<String> types = productionGroup.getDraughtAnimals();

        animalsTitle = (TextView) parentView.findViewById(R.id.draughtProductionTitle);
        animalsLayout1 = (LinearLayout) parentView.findViewById(R.id.draughtProductionGroup1);
        animalsLayout1.setEnabled(!isModeLocked);

        animalsLayout1 = (LinearLayout) parentView.findViewById(R.id.draughtProductionGroup2);
        animalsLayout1.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_3_list);

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

                productionGroup.setDraughtAnimals(types);
                ((SenegalSurvey) survey).setTrainedProductionGroup(productionGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, animalsLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 14);
            checkBox.setTag(race3IdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            animalsLayout1.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
