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
import org.graindataterminal.models.senegal.ProductionDestinationGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerDestinationProduction extends BaseFragment {

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerDestinationProduction fragment = new FarmerDestinationProduction();
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
        View view = inflater.inflate(R.layout.sn_farmer_destination_production, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateHoneyProduction(view, inflater);
        updateEggsProduction(view, inflater);
        updateMilkProduction(view, inflater);
        updateMeatProduction(view, inflater);

        return view;
    }

    protected void updateMeatProduction(View parentView, LayoutInflater inflater) {
        final ProductionDestinationGroup destinationGroup = ((SenegalSurvey) survey).getDestinationGroup();
        final List<String> types = destinationGroup.getMeatDestinationProduction();

        LinearLayout typeLayout = (LinearLayout) parentView.findViewById(R.id.meatProductionGroup);
        typeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_main_production_list);

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

                destinationGroup.setMeatDestinationProduction(types);
                ((SenegalSurvey) survey).setDestinationGroup(destinationGroup);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(ProductionDestinationGroup.mainProductionIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            typeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateMilkProduction(View parentView, LayoutInflater inflater) {
        final ProductionDestinationGroup destinationGroup = ((SenegalSurvey) survey).getDestinationGroup();
        final List<String> types = destinationGroup.getMilkDestinationProduction();

        LinearLayout typeLayout= (LinearLayout) parentView.findViewById(R.id.milkProductionGroup);
        typeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_main_production_list);

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

                destinationGroup.setMilkDestinationProduction(types);
                ((SenegalSurvey) survey).setDestinationGroup(destinationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(ProductionDestinationGroup.mainProductionIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            typeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateEggsProduction(View parentView, LayoutInflater inflater) {
        final ProductionDestinationGroup destinationGroup = ((SenegalSurvey) survey).getDestinationGroup();
        final List<String> types = destinationGroup.getEggsDestinationProduction();

        LinearLayout typeLayout = (LinearLayout) parentView.findViewById(R.id.eggsProductionGroup);
        typeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_main_production_list);

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

                destinationGroup.setEggsDestinationProduction(types);
                ((SenegalSurvey) survey).setDestinationGroup(destinationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(ProductionDestinationGroup.mainProductionIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            typeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateHoneyProduction(View parentView, LayoutInflater inflater) {
        final ProductionDestinationGroup destinationGroup = ((SenegalSurvey) survey).getDestinationGroup();
        final List<String> types = destinationGroup.getHoneyDestinationProduction();

        LinearLayout typeLayout = (LinearLayout) parentView.findViewById(R.id.honeyProductionGroup);
        typeLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_main_production_list);

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

                destinationGroup.setHoneyDestinationProduction(types);
                ((SenegalSurvey) survey).setDestinationGroup(destinationGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(ProductionDestinationGroup.mainProductionIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            typeLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
