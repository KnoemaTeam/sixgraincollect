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
import org.graindataterminal.models.senegal.PetsFoodGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerPetsFood extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "porcin", "volaille", "lapin", "autrre_r"};

    private TextView foodDrivingTitle = null;
    private LinearLayout foodDrivingLayout1 = null;
    private LinearLayout foodDrivingLayout2 = null;

    private TextView foodTroughtTitle = null;
    private LinearLayout foodTroughtLayout1 = null;
    private LinearLayout foodTroughtLayout2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerPetsFood fragment = new FarmerPetsFood();
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
        View view = inflater.inflate(R.layout.sn_farmer_pets_food, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateFoodTrought(view, inflater);
        updateFoodDriving(view, inflater);
        updateFoodFeed(view, inflater);

        return view;
    }

    protected void updateFoodFeed(View parentView, LayoutInflater inflater) {
        final PetsFoodGroup foodGroup = ((SenegalSurvey) survey).getPetsFoodGroup();
        final List<String> types = foodGroup.getFoodFeed();

        LinearLayout typeLayout1 = (LinearLayout) parentView.findViewById(R.id.foodFeedGroup1);
        typeLayout1.setEnabled(!isModeLocked);

        LinearLayout typeLayout2 = (LinearLayout) parentView.findViewById(R.id.foodFeedGroup2);
        typeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_power_mode_list);

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

                foodGroup.setFoodFeed(types);
                ((SenegalSurvey) survey).setPetsFoodGroup(foodGroup);

                updateСontentVisibility(types, true);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(PetsFoodGroup.modeAlimentationIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            typeLayout1.addView(checkBox);

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
        if (answers.contains(PetsFoodGroup.modeAlimentationIdList[0])) {
            if (withAnimation) {
                Helper.showView(foodDrivingTitle);
                Helper.showView(foodDrivingLayout1);
                Helper.showView(foodDrivingLayout2);
            }
            else {
                foodDrivingTitle.setVisibility(View.VISIBLE);
                foodDrivingLayout1.setVisibility(View.VISIBLE);
                foodDrivingLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(foodDrivingTitle);
                Helper.fadeView(foodDrivingLayout1);
                Helper.fadeView(foodDrivingLayout2);
            }
            else {
                foodDrivingTitle.setVisibility(View.GONE);
                foodDrivingLayout1.setVisibility(View.GONE);
                foodDrivingLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(PetsFoodGroup.modeAlimentationIdList[1])) {
            if (withAnimation) {
                Helper.showView(foodTroughtTitle);
                Helper.showView(foodTroughtLayout1);
                Helper.showView(foodTroughtLayout2);
            }
            else {
                foodTroughtTitle.setVisibility(View.VISIBLE);
                foodTroughtLayout1.setVisibility(View.VISIBLE);
                foodTroughtLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(foodTroughtTitle);
                Helper.fadeView(foodTroughtLayout1);
                Helper.fadeView(foodTroughtLayout2);
            }
            else {
                foodTroughtTitle.setVisibility(View.GONE);
                foodTroughtLayout1.setVisibility(View.GONE);
                foodTroughtLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateFoodDriving(View parentView, LayoutInflater inflater) {
        final PetsFoodGroup foodGroup = ((SenegalSurvey) survey).getPetsFoodGroup();
        final List<String> types = foodGroup.getFoodDriving();

        foodDrivingTitle = (TextView) parentView.findViewById(R.id.foodDrivingTitle);
        foodDrivingLayout1 = (LinearLayout) parentView.findViewById(R.id.foodDrivingGroup1);
        foodDrivingLayout1.setEnabled(!isModeLocked);

        foodDrivingLayout2 = (LinearLayout) parentView.findViewById(R.id.foodDrivingGroup2);
        foodDrivingLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_4_list);

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

                foodGroup.setFoodDriving(types);
                ((SenegalSurvey) survey).setPetsFoodGroup(foodGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, foodDrivingLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                foodDrivingLayout1.addView(checkBox);
            else
                foodDrivingLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateFoodTrought(View parentView, LayoutInflater inflater) {
        final PetsFoodGroup foodGroup = ((SenegalSurvey) survey).getPetsFoodGroup();
        final List<String> types = foodGroup.getFoodTrough();

        foodTroughtTitle = (TextView) parentView.findViewById(R.id.foodTroughtTitle);
        foodTroughtLayout1 = (LinearLayout) parentView.findViewById(R.id.foodTroughtGroup1);
        foodTroughtLayout1.setEnabled(!isModeLocked);

        foodTroughtLayout2 = (LinearLayout) parentView.findViewById(R.id.foodTroughtGroup2);
        foodTroughtLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sources =  resources.getStringArray(R.array.senegal_animal_race_4_list);

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

                foodGroup.setFoodTrough(types);
                ((SenegalSurvey) survey).setPetsFoodGroup(foodGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, foodTroughtLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                foodTroughtLayout1.addView(checkBox);
            else
                foodTroughtLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
