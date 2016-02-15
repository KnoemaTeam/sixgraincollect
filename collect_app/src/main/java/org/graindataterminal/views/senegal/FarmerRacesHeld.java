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
import org.graindataterminal.models.senegal.RacesArrestedGroup;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.views.base.BaseFragment;

import java.util.List;

public class FarmerRacesHeld extends BaseFragment {
    private final static String[] raceIdList = {"bovin", "ovin", "caprin", "equin", "porcin", "volaille", "lapin", "autrre_r"};

    private TextView raceLocalTitle = null;
    private LinearLayout raceLocalLayout1 = null;
    private LinearLayout raceLocalLayout2 = null;

    private TextView raceExoticTitle = null;
    private LinearLayout raceExoticLayout1 = null;
    private LinearLayout raceExoticLayout2 = null;

    private TextView raceMixedTitle = null;
    private LinearLayout raceMixedLayout1 = null;
    private LinearLayout raceMixedLayout2 = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerRacesHeld fragment = new FarmerRacesHeld();
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
        View view = inflater.inflate(R.layout.sn_farmer_races_held, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateRaceMixed(view, inflater);
        updateRaceExotic(view, inflater);
        updateRaceLocal(view, inflater);
        updateRaceType(view, inflater);

        return view;
    }

    protected void updateRaceType(View parentView, LayoutInflater inflater) {
        final RacesArrestedGroup racesGroup = ((SenegalSurvey) survey).getRacesArrestedGroup();
        final List<String> types = racesGroup.getRaceType();

        LinearLayout typeLayout1 = (LinearLayout) parentView.findViewById(R.id.raceTypeGroup1);
        typeLayout1.setEnabled(!isModeLocked);

        LinearLayout typeLayout2 = (LinearLayout) parentView.findViewById(R.id.raceTypeGroup2);
        typeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesSource =  resources.getStringArray(R.array.senegal_animal_races_held_list);

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

                racesGroup.setRaceType(types);
                ((SenegalSurvey) survey).setRacesArrestedGroup(racesGroup);

                updateСontentVisibility(types, true);
            }
        };

        int index = 1;
        for (String type: typesSource) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, typeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 10);
            checkBox.setTag(RacesArrestedGroup.raceHeldIdList[index - 1]);
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
        if (answers.contains(RacesArrestedGroup.raceHeldIdList[0])) {
            if (withAnimation) {
                Helper.showView(raceLocalTitle);
                Helper.showView(raceLocalLayout1);
                Helper.showView(raceLocalLayout2);
            }
            else {
                raceLocalTitle.setVisibility(View.VISIBLE);
                raceLocalLayout1.setVisibility(View.VISIBLE);
                raceLocalLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(raceLocalTitle);
                Helper.fadeView(raceLocalLayout1);
                Helper.fadeView(raceLocalLayout2);
            }
            else {
                raceLocalTitle.setVisibility(View.GONE);
                raceLocalLayout1.setVisibility(View.GONE);
                raceLocalLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(RacesArrestedGroup.raceHeldIdList[1])) {
            if (withAnimation) {
                Helper.showView(raceExoticTitle);
                Helper.showView(raceExoticLayout1);
                Helper.showView(raceExoticLayout2);
            }
            else {
                raceExoticTitle.setVisibility(View.VISIBLE);
                raceExoticLayout1.setVisibility(View.VISIBLE);
                raceExoticLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(raceExoticTitle);
                Helper.fadeView(raceExoticLayout1);
                Helper.fadeView(raceExoticLayout2);
            }
            else {
                raceExoticTitle.setVisibility(View.GONE);
                raceExoticLayout1.setVisibility(View.GONE);
                raceExoticLayout2.setVisibility(View.GONE);
            }
        }

        if (answers.contains(RacesArrestedGroup.raceHeldIdList[2])) {
            if (withAnimation) {
                Helper.showView(raceMixedTitle);
                Helper.showView(raceMixedLayout1);
                Helper.showView(raceMixedLayout2);
            }
            else {
                raceMixedTitle.setVisibility(View.VISIBLE);
                raceMixedLayout1.setVisibility(View.VISIBLE);
                raceMixedLayout2.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (withAnimation) {
                Helper.fadeView(raceMixedTitle);
                Helper.fadeView(raceMixedLayout1);
                Helper.fadeView(raceMixedLayout2);
            }
            else {
                raceMixedTitle.setVisibility(View.GONE);
                raceMixedLayout1.setVisibility(View.GONE);
                raceMixedLayout2.setVisibility(View.GONE);
            }
        }
    }

    protected void updateRaceLocal(View parentView, LayoutInflater inflater) {
        final RacesArrestedGroup racesGroup = ((SenegalSurvey) survey).getRacesArrestedGroup();
        final List<String> types = racesGroup.getRaceLocal();

        raceLocalTitle = (TextView) parentView.findViewById(R.id.raceLocalTitle);
        raceLocalLayout1 = (LinearLayout) parentView.findViewById(R.id.raceLocalGroup1);
        raceLocalLayout1.setEnabled(!isModeLocked);

        raceLocalLayout2 = (LinearLayout) parentView.findViewById(R.id.raceLocalGroup2);
        raceLocalLayout2.setEnabled(!isModeLocked);

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

                racesGroup.setRaceLocal(types);
                ((SenegalSurvey) survey).setRacesArrestedGroup(racesGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, raceLocalLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                raceLocalLayout1.addView(checkBox);
            else
                raceLocalLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateRaceExotic(View parentView, LayoutInflater inflater) {
        final RacesArrestedGroup racesGroup = ((SenegalSurvey) survey).getRacesArrestedGroup();
        final List<String> types = racesGroup.getRaceExotic();

        raceExoticTitle = (TextView) parentView.findViewById(R.id.raceExoticTitle);
        raceExoticLayout1 = (LinearLayout) parentView.findViewById(R.id.raceExoticGroup1);
        raceExoticLayout1.setEnabled(!isModeLocked);

        raceExoticLayout2 = (LinearLayout) parentView.findViewById(R.id.raceExoticGroup2);
        raceExoticLayout2.setEnabled(!isModeLocked);

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

                racesGroup.setRaceExotic(types);
                ((SenegalSurvey) survey).setRacesArrestedGroup(racesGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, raceExoticLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                raceExoticLayout1.addView(checkBox);
            else
                raceExoticLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateRaceMixed(View parentView, LayoutInflater inflater) {
        final RacesArrestedGroup racesGroup = ((SenegalSurvey) survey).getRacesArrestedGroup();
        final List<String> types = racesGroup.getRaceMixed();

        raceMixedTitle = (TextView) parentView.findViewById(R.id.raceMixedTitle);
        raceMixedLayout1 = (LinearLayout) parentView.findViewById(R.id.raceMixedGroup1);
        raceMixedLayout1.setEnabled(!isModeLocked);

        raceMixedLayout2 = (LinearLayout) parentView.findViewById(R.id.raceMixedGroup2);
        raceMixedLayout2.setEnabled(!isModeLocked);

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

                racesGroup.setRaceMixed(types);
                ((SenegalSurvey) survey).setRacesArrestedGroup(racesGroup);
            }
        };

        int index = 1;
        for (String source: sources) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, raceMixedLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(raceIdList[index - 1]);
            checkBox.setText(source);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < sources.length / 2)
                raceMixedLayout1.addView(checkBox);
            else
                raceMixedLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && types.contains(id))
                checkBox.setChecked(true);

            if (types.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }
}
