package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.FarmOwner;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerOwner extends BaseActivity {
    private FarmOwner currentOwner = null;
    private boolean isModeLocked = false;

    private final static String[] speciesNameIdList = {"bovin_vache", "bovin_taureau", "bovin_male_castre", "bovin_jeune_femelle", "bovin_jeune_male", "bovin_femelle_non_sevre", "bonvin_male_nonsevre", "bovin_trait", "ovin_male", "ovin_femelle", "ov_m", "ov_f", "ovin_jeunemale", "ovin_jeunefemelle", "ovin_belier", "ovin_brebi", "caprin_malenonsevre", "caprin_femellenonsevre", "c_m", "c_f", "caprin_jeunemale", "caprin_jeunefemelle", "caprin_bouc", "caprin_chevre", "equin_malenonsevre", "equin_femellenonsevre", "equin_cheval", "equin_jument", "e_m", "e_f", "equin_cheval_course", "equi_trait", "porcin_male", "porcin_femelle", "p_m", "p_f", "porcin_porc", "porcin_truie", "poule", "coq", "pintatade", "canne", "pigeon", "autre_vollaie", "abeille", "as_m", "as_f", "as_ma", "as_fa"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentOwner = DataHolder.getInstance().getFarmOwner();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateSpeciesName();
        updateEmployeeCount();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_owner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_owner));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_owner));

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

    protected void deleteOwner() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<FarmOwner> ownerList = ((SenegalSurvey) survey).getFarmOwners();
            int index = DataHolder.getInstance().getFarmOwnerIndex();

            if (index > ownerList.size() - 1)
                return;

            ownerList.remove(index);
            ((SenegalSurvey) survey).setFarmOwners(ownerList);
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
                getString(R.string.action_delete_owner),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_owner),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteOwner();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void updateSpeciesName() {
        String speciesNameText = currentOwner.getName();

        ArrayAdapter<CharSequence> speciesAdapter = ArrayAdapter.createFromResource(this, R.array.senegal_species_name_list, android.R.layout.simple_spinner_item);
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner speciesSpinner = (Spinner) findViewById(R.id.speciesSpinner);
        speciesSpinner.setEnabled(!isModeLocked);
        speciesSpinner.setAdapter(speciesAdapter);
        speciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isModeLocked)
                    return;

                currentOwner.setName(speciesNameIdList[position]);
                currentOwner.setTitle(String.valueOf(parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(speciesNameText)) {
            for (int i = 0; i < speciesNameIdList.length; i++) {
                if (speciesNameIdList[i].equals(speciesNameText)) {
                    speciesSpinner.setSelection(i);
                }
            }
        }
    }

    protected void updateEmployeeCount() {
        String employeeCountText = currentOwner.getEmployeesCount();

        EditText employeeCount = (EditText) findViewById(R.id.employeeCountText);
        employeeCount.setEnabled(!isModeLocked);
        employeeCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentOwner.setEmployeesCount(null);
                else
                    currentOwner.setEmployeesCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(employeeCountText))
            employeeCount.setText(employeeCountText);
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
