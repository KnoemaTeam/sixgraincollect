package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.Animal;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerAnimals extends BaseActivity {
    private Animal currentAnimal = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] raceNameIdList = {"bovin", "ovin", "caprin", "equin", "pordin", "volaille"};
    private final static String[] productionIdList = {"naisseur", "embouche", "production_lait", "production_oeuf", "chevaux_course", "animaux_trait", "production_miel"};
    private final static String[] raceHeldIdList = {"locale", "exotique", "metisse"};
    private final static String[] powerModeIdList = {"paturage_alimentation", "auge", "autre_alimentation"};
    private final static String[] compTypeIdList = {"aucun", "s_p_agricole", "pr_transforme", "cmv"};
    private final static String[] vaccinationIdList = {"aucune_vaccin", "systematique", "occasionnelle"};
    private final static String[] geneticIdList = {"aucune_pratique_gen", "intro_geniteur", "insemination", "mixte"};
    private final static String[] controlIdList = {"aucune_controle", "par_jour", "par_semaine", "par_mois", "par_bimestre", "par_trimestre", "autre_controle"};
    private final static String[] mainProductionIdList = {"autoconsommation", "generateur_revenu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentAnimal = DataHolder.getInstance().getAnimal();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolbar();

        updateRaceName();
        updateTrainedProduction();
        updateRacesHeld();
        updatePowerMode();
        updateComplementationType();
        updateVaccination();
        updateDeworming();
        updateGeneticImprovement();
        updateSanitaryСontrol();
        updateMainProduction();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_animals;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_survey));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_survey));

        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            menu.findItem(R.id.action_mode).setVisible(false);
        else if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            menu.findItem(R.id.action_edit_item).setVisible(false);

        return true;
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_app_logo);
        }
    }

    protected void deleteAnimal() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            if (survey.getMode() == BaseSurvey.SURVEY_READ_MODE)
                return;

            List<Animal> animalList = ((SenegalSurvey) survey).getAnimals();
            int index = DataHolder.getInstance().getAnimalIndex();

            if (index > animalList.size() - 1)
                return;

            animalList.remove(index);
            ((SenegalSurvey) survey).setAnimals(animalList);
            DataUtils.setSurveyList(surveyList);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void showConfirmation () {
        createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                getString(R.string.confirmation_message),
                getString(R.string.confirmation_message_delete_animal),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_field),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteAnimal();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(String tag) {
        super.onDialogNegativeClick(tag);
    }

    protected void updateRaceName() {
        String raceNameText = currentAnimal.getRaceName();

        final RadioGroup raceGroup1 = (RadioGroup) findViewById(R.id.raceGroup1);
        final RadioGroup raceGroup2 = (RadioGroup) findViewById(R.id.raceGroup2);

        raceGroup1.setEnabled(!isModeLocked);
        raceGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (raceGroup2 != null && raceGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        raceGroup2.clearCheck();

                    currentAnimal.setRaceName(rb.getTag().toString());
                    currentAnimal.setTitle(rb.getText().toString());
                }
            }
        });

        raceGroup2.setEnabled(!isModeLocked);
        raceGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (raceGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        raceGroup1.clearCheck();

                    currentAnimal.setRaceName(rb.getTag().toString());
                    currentAnimal.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] names = resources.getStringArray(R.array.senegal_animal_race_list);

        int index = 1;
        boolean notSet = true;

        for (String name: names) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, raceGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(raceNameIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < names.length / 2)
                raceGroup1.addView(rb);
            else
                raceGroup2.addView(rb);

            if (!TextUtils.isEmpty(raceNameText)) {
                if (rb.getTag().equals(raceNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            raceGroup1.check(10);
    }

    protected void updateTrainedProduction() {
        final List<String> productions = currentAnimal.getTrainedProduction();

        LinearLayout trainedProductionLayout1 = (LinearLayout) findViewById(R.id.trainedProductionGroup1);
        trainedProductionLayout1.setEnabled(!isModeLocked);

        LinearLayout trainedProductionLayout2 = (LinearLayout) findViewById(R.id.trainedProductionGroup2);
        trainedProductionLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] productionList =  resources.getStringArray(R.array.senegal_animal_trainde_production_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !productions.contains(id))
                        productions.add(id);
                }
                else {
                    productions.remove(id);
                }

                currentAnimal.setTrainedProduction(productions);
            }
        };

        int index = 1;
        for (String production: productionList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, trainedProductionLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 11);
            checkBox.setTag(productionIdList[index - 1]);
            checkBox.setText(production);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < productionList.length / 2)
                trainedProductionLayout1.addView(checkBox);
            else
                trainedProductionLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && productions.contains(id))
                checkBox.setChecked(true);

            if (productions.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateRacesHeld() {
        final List<String> races = currentAnimal.getRacesHeld();

        LinearLayout raceHeldLayout = (LinearLayout) findViewById(R.id.raceHeldGroup);
        raceHeldLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] raceList =  resources.getStringArray(R.array.senegal_animal_races_held_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !races.contains(id))
                        races.add(id);
                }
                else {
                    races.remove(id);
                }

                currentAnimal.setRacesHeld(races);
            }
        };

        int index = 1;
        for (String race: raceList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, raceHeldLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 12);
            checkBox.setTag(raceHeldIdList[index - 1]);
            checkBox.setText(race);
            checkBox.setOnCheckedChangeListener(event);

            raceHeldLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && races.contains(id))
                checkBox.setChecked(true);

            if (races.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updatePowerMode() {
        final List<String> powerModes = currentAnimal.getPowerMode();

        LinearLayout powerModesLayout = (LinearLayout) findViewById(R.id.powerModeGroup);
        powerModesLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] modesList =  resources.getStringArray(R.array.senegal_animal_power_mode_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !powerModes.contains(id))
                        powerModes.add(id);
                }
                else {
                    powerModes.remove(id);
                }

                currentAnimal.setPowerMode(powerModes);
            }
        };

        int index = 1;
        for (String mode: modesList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, powerModesLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 13);
            checkBox.setTag(powerModeIdList[index - 1]);
            checkBox.setText(mode);
            checkBox.setOnCheckedChangeListener(event);

            powerModesLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && powerModes.contains(id))
                checkBox.setChecked(true);

            if (powerModes.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateComplementationType() {
        final List<String> complementationTypes = currentAnimal.getSupplementationType();

        LinearLayout complementationTypeLayout1 = (LinearLayout) findViewById(R.id.complementationTypeGroup1);
        complementationTypeLayout1.setEnabled(!isModeLocked);

        LinearLayout complementationTypeLayout2 = (LinearLayout) findViewById(R.id.complementationTypeGroup2);
        complementationTypeLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] typesList =  resources.getStringArray(R.array.senegal_animal_complementation_type_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !complementationTypes.contains(id))
                        complementationTypes.add(id);
                }
                else {
                    complementationTypes.remove(id);
                }

                currentAnimal.setSupplementationType(complementationTypes);
            }
        };

        int index = 1;
        for (String type: typesList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, complementationTypeLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 14);
            checkBox.setTag(compTypeIdList[index - 1]);
            checkBox.setText(type);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < typesList.length / 2)
                complementationTypeLayout1.addView(checkBox);
            else
                complementationTypeLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && complementationTypes.contains(id))
                checkBox.setChecked(true);

            if (complementationTypes.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateVaccination() {
        final List<String> vaccinations = currentAnimal.getPracticeVaccination();

        LinearLayout vaccinationLayout = (LinearLayout) findViewById(R.id.vaccinationGroup);
        vaccinationLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] vaccinationList =  resources.getStringArray(R.array.senegal_animal_vaccination_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !vaccinations.contains(id))
                        vaccinations.add(id);
                }
                else {
                    vaccinations.remove(id);
                }

                currentAnimal.setPracticeVaccination(vaccinations);
            }
        };

        int index = 1;
        for (String vaccination: vaccinationList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, vaccinationLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 15);
            checkBox.setTag(vaccinationIdList[index - 1]);
            checkBox.setText(vaccination);
            checkBox.setOnCheckedChangeListener(event);

            vaccinationLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && vaccinations.contains(id))
                checkBox.setChecked(true);

            if (vaccinations.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateDeworming() {
        final List<String> dewormings = currentAnimal.getPracticeDeworming();

        LinearLayout dewormingLayout = (LinearLayout) findViewById(R.id.dewormingGroup);
        dewormingLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] dewormingList =  resources.getStringArray(R.array.senegal_animal_vaccination_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !dewormings.contains(id))
                        dewormings.add(id);
                }
                else {
                    dewormings.remove(id);
                }

                currentAnimal.setPracticeDeworming(dewormings);
            }
        };

        int index = 1;
        for (String deworming: dewormingList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, dewormingLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 16);
            checkBox.setTag(vaccinationIdList[index - 1]);
            checkBox.setText(deworming);
            checkBox.setOnCheckedChangeListener(event);

            dewormingLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && dewormings.contains(id))
                checkBox.setChecked(true);

            if (dewormings.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateGeneticImprovement() {
        final List<String> geneticImprovements = currentAnimal.getPracticeGeneticImprovement();

        LinearLayout geneticImprovementLayout1 = (LinearLayout) findViewById(R.id.geneticImprovementGroup1);
        geneticImprovementLayout1.setEnabled(!isModeLocked);

        LinearLayout geneticImprovementLayout2 = (LinearLayout) findViewById(R.id.geneticImprovementGroup2);
        geneticImprovementLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] geneticImprovementList =  resources.getStringArray(R.array.senegal_animal_genetic_improvement_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !geneticImprovements.contains(id))
                        geneticImprovements.add(id);
                }
                else {
                    geneticImprovements.remove(id);
                }

                currentAnimal.setPracticeGeneticImprovement(geneticImprovements);
            }
        };

        int index = 1;
        for (String improvement: geneticImprovementList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, geneticImprovementLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 17);
            checkBox.setTag(geneticIdList[index - 1]);
            checkBox.setText(improvement);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 < geneticImprovementList.length / 2)
                geneticImprovementLayout1.addView(checkBox);
            else
                geneticImprovementLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && geneticImprovements.contains(id))
                checkBox.setChecked(true);

            if (geneticImprovements.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateSanitaryСontrol() {
        final List<String> sanitaryСontrols = currentAnimal.getSanitaryControl();

        LinearLayout sanitaryСontrolLayout1 = (LinearLayout) findViewById(R.id.sanitaryСontrolGroup1);
        sanitaryСontrolLayout1.setEnabled(!isModeLocked);

        LinearLayout sanitaryСontrolLayout2 = (LinearLayout) findViewById(R.id.sanitaryСontrolGroup2);
        sanitaryСontrolLayout2.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] sanitaryСontrolList =  resources.getStringArray(R.array.senegal_animal_sanitary_control_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !sanitaryСontrols.contains(id))
                        sanitaryСontrols.add(id);
                }
                else {
                    sanitaryСontrols.remove(id);
                }

                currentAnimal.setSanitaryControl(sanitaryСontrols);
            }
        };

        int index = 1;
        for (String control: sanitaryСontrolList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, sanitaryСontrolLayout1, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 18);
            checkBox.setTag(controlIdList[index - 1]);
            checkBox.setText(control);
            checkBox.setOnCheckedChangeListener(event);

            if (index - 1 <= sanitaryСontrolList.length / 2)
                sanitaryСontrolLayout1.addView(checkBox);
            else
                sanitaryСontrolLayout2.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && sanitaryСontrols.contains(id))
                checkBox.setChecked(true);

            if (sanitaryСontrols.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateMainProduction() {
        final List<String> mainProductions = currentAnimal.getMainUseProduction();

        LinearLayout mainProductionLayout = (LinearLayout) findViewById(R.id.mainProductionGroup);
        mainProductionLayout.setEnabled(!isModeLocked);

        Resources resources = getResources();
        String[] productionList =  resources.getStringArray(R.array.senegal_animal_main_production_list);

        CompoundButton.OnCheckedChangeListener event = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isModeLocked)
                    return;

                String id = buttonView.getTag().toString();

                if (isChecked) {
                    if (!TextUtils.isEmpty(id) && !mainProductions.contains(id))
                        mainProductions.add(id);
                }
                else {
                    mainProductions.remove(id);
                }

                currentAnimal.setMainUseProduction(mainProductions);
            }
        };

        int index = 1;
        for (String production: productionList) {
            CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.fragment_check_box_button, mainProductionLayout, false);
            checkBox.setEnabled(!isModeLocked);
            checkBox.setId(index * 19);
            checkBox.setTag(mainProductionIdList[index - 1]);
            checkBox.setText(production);
            checkBox.setOnCheckedChangeListener(event);

            mainProductionLayout.addView(checkBox);

            String id = checkBox.getTag().toString();
            if (!TextUtils.isEmpty(id) && mainProductions.contains(id))
                checkBox.setChecked(true);

            if (mainProductions.size() == 0 && index - 1 == 0)
                checkBox.setChecked(true);

            index++;
        }
    }

    protected void updateDoneButton() {
        Button button = (Button) findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isModeLocked)
                    setResult(RESULT_CANCELED, new Intent());
                else
                    setResult(RESULT_OK, new Intent());

                finish();
            }
        });

        if (isModeLocked)
            button.setText(getString(R.string.action_finish));
    }
}
