package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.Movement;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;

import java.util.List;

public class FarmerMovement extends BaseActivity {
    private Movement currentMovement = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] speciesIdList = {"bovin_male", "bov_fem", "ov_m", "ov_f", "cap_m", "cap_f", "eq_m", "eq_f", "por_m", "por_f", "vol_m", "vol_f", "ab_m", "ab_f", "lap_m", "lap_f"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentMovement = DataHolder.getInstance().getMovement();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolbar();

        updateSpeciesName();
        updateBirthNumber();
        updateHeritage();
        updateShopping();
        updateDonations();
        updateConfiage();
        updateOtherEntries();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_movement;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_movement));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_movement));

        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
        if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
            menu.findItem(R.id.action_mode).setVisible(false);
        else if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            menu.findItem(R.id.action_edit_item).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_delete_item)
            showConfirmation();
        else if (item.getItemId() == R.id.action_edit_item)
            updateMode(getIntent(), -1);

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

    protected void deleteMovement() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<Movement> movementList = ((SenegalSurvey) survey).getMovements();
            int index = DataHolder.getInstance().getMovementIndex();

            if (index > movementList.size() - 1)
                return;

            movementList.remove(index);
            ((SenegalSurvey) survey).setMovements(movementList);
            MyApp.setSurveyList(surveyList);
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
                getString(R.string.action_delete_movement),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_movement),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteMovement();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void updateSpeciesName() {
        String nameText = currentMovement.getSpecies();

        final RadioGroup speciesGroup1 = (RadioGroup) findViewById(R.id.speciesGroup1);
        final RadioGroup speciesGroup2 = (RadioGroup) findViewById(R.id.speciesGroup2);

        speciesGroup1.setEnabled(!isModeLocked);
        speciesGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (speciesGroup2 != null && speciesGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        speciesGroup2.clearCheck();

                    currentMovement.setSpecies(rb.getTag().toString());
                    currentMovement.setTitle(rb.getText().toString());
                }
            }
        });

        speciesGroup2.setEnabled(!isModeLocked);
        speciesGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (speciesGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        speciesGroup1.clearCheck();

                    currentMovement.setSpecies(rb.getTag().toString());
                    currentMovement.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] names = resources.getStringArray(R.array.senegal_escape_species_name_list);

        int index = 1;
        boolean notSet = true;

        for (String name: names) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, speciesGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(speciesIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < names.length / 2)
                speciesGroup1.addView(rb);
            else
                speciesGroup2.addView(rb);

            if (!TextUtils.isEmpty(nameText)) {
                if (rb.getTag().equals(nameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            speciesGroup1.check(10);
    }

    protected void updateBirthNumber() {
        String birthNumberText = currentMovement.getBirthNumber();

        EditText birthNumber = (EditText) findViewById(R.id.birthNumberText);
        birthNumber.setEnabled(!isModeLocked);
        birthNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setBirthNumber(null);
                else
                    currentMovement.setBirthNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(birthNumberText))
            birthNumber.setText(birthNumberText);
    }

    protected void updateHeritage() {
        String heritageText = currentMovement.getHeritage();

        EditText heritage = (EditText) findViewById(R.id.heritageText);
        heritage.setEnabled(!isModeLocked);
        heritage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setHeritage(null);
                else
                    currentMovement.setHeritage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(heritageText))
            heritage.setText(heritageText);
    }

    protected void updateShopping() {
        String shoppingText = currentMovement.getShopping();

        EditText shopping = (EditText) findViewById(R.id.shoppingText);
        shopping.setEnabled(!isModeLocked);
        shopping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setShopping(null);
                else
                    currentMovement.setShopping(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(shoppingText))
            shopping.setText(shoppingText);
    }

    protected void updateDonations() {
        String donationsText = currentMovement.getDonations();

        EditText donations = (EditText) findViewById(R.id.donationsText);
        donations.setEnabled(!isModeLocked);
        donations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setDonations(null);
                else
                    currentMovement.setDonations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(donationsText))
            donations.setText(donationsText);
    }

    protected void updateConfiage() {
        String confiageText = currentMovement.getConfiage();

        EditText confiage = (EditText) findViewById(R.id.confiageText);
        confiage.setEnabled(!isModeLocked);
        confiage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setConfiage(null);
                else
                    currentMovement.setConfiage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(confiageText))
            confiage.setText(confiageText);
    }

    protected void updateOtherEntries() {
        String entriesText = currentMovement.getOtherEntries();

        EditText entries = (EditText) findViewById(R.id.otherEntriesText);
        entries.setEnabled(!isModeLocked);
        entries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentMovement.setOtherEntries(null);
                else
                    currentMovement.setOtherEntries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(entriesText))
            entries.setText(entriesText);
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
