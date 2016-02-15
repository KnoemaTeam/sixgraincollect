package org.graindataterminal.views.senegal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
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
import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.Infrastructure;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;

import java.util.List;

public class FarmerInfrastructure extends BaseActivity {
    private Infrastructure currentInfrastructure = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] infrastructureNameIdList = {"station_filtraton", "batiment_tech", "magasin_stock", "chambre_froid", "reservoir_eau", "abreuvoir_fixe", "toilettes", "unit_cond", "bassin", "guerite", "forage", "pui", "serre", "reseau_irr", "station_pompage", "installation_solaire", "autres_infrastructure"};
    private final static String[] acquisitionModeIdList = {"comptant", "credit", "c_cr", "don", "location", "autre_acq"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentInfrastructure = DataHolder.getInstance().getInfrastructure();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateInfrastructureName();
        updateAcquisition();
        updateAcquisitionMode();
        updateInfrastructureCost();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_infrastructure;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_infrastructure));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_infrastructure));

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

    protected void deleteInfrastructure() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<Infrastructure> infrastructureList = ((SenegalSurvey) survey).getInfrastructures();
            int index = DataHolder.getInstance().getInfrastructureIndex();

            if (index > infrastructureList.size() - 1)
                return;

            infrastructureList.remove(index);
            ((SenegalSurvey) survey).setInfrastructures(infrastructureList);
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
                getString(R.string.confirmation_message_delete_infrastructure),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_infrastructure),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteInfrastructure();
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

    protected void updateInfrastructureName() {
        String infrastructureNameText = currentInfrastructure.getName();

        final RadioGroup infrastructureGroup1 = (RadioGroup) findViewById(R.id.infrastructureGroup1);
        final RadioGroup infrastructureGroup2 = (RadioGroup) findViewById(R.id.infrastructureGroup2);

        infrastructureGroup1.setEnabled(!isModeLocked);
        infrastructureGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (infrastructureGroup2 != null && infrastructureGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        infrastructureGroup2.clearCheck();

                    currentInfrastructure.setName(rb.getTag().toString());
                    currentInfrastructure.setTitle(rb.getText().toString());
                }
            }
        });

        infrastructureGroup2.setEnabled(!isModeLocked);
        infrastructureGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (infrastructureGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        infrastructureGroup1.clearCheck();

                    currentInfrastructure.setName(rb.getTag().toString());
                    currentInfrastructure.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] names = resources.getStringArray(R.array.senegal_infrastructure_list);

        int index = 1;
        boolean notSet = true;

        for (String name: names) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, infrastructureGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(infrastructureNameIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= names.length / 2)
                infrastructureGroup1.addView(rb);
            else
                infrastructureGroup2.addView(rb);

            if (!TextUtils.isEmpty(infrastructureNameText)) {
                if (rb.getTag().equals(infrastructureNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            infrastructureGroup1.check(10);
    }

    protected void updateAcquisition() {
        String acquisitionText = currentInfrastructure.getAcquisition();

        final EditText acquisition = (EditText) findViewById(R.id.infrastructureAcquisitionText);
        acquisition.setEnabled(!isModeLocked);
        acquisition.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        acquisition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    currentInfrastructure.setAcquisition(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(acquisition.getText().toString())) {
                                acquisition.setText(correctDate);
                                acquisition.setSelection(correctDate.length());
                            }

                            currentInfrastructure.setAcquisition(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(acquisitionText))
            acquisition.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", acquisitionText));
    }

    protected void updateAcquisitionMode() {
        String acquisitionModeText = currentInfrastructure.getAcquisitionMode();

        final RadioGroup acquisitionModeGroup1 = (RadioGroup) findViewById(R.id.infrastructureAcquisitionModeGroup1);
        final RadioGroup acquisitionModeGroup2 = (RadioGroup) findViewById(R.id.infrastructureAcquisitionModeGroup2);

        acquisitionModeGroup1.setEnabled(!isModeLocked);
        acquisitionModeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (acquisitionModeGroup2 != null && acquisitionModeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        acquisitionModeGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    currentInfrastructure.setAcquisitionMode(id);
                }
            }
        });

        acquisitionModeGroup2.setEnabled(!isModeLocked);
        acquisitionModeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (acquisitionModeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        acquisitionModeGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    currentInfrastructure.setAcquisitionMode(id);
                }
            }
        });

        Resources resources = getResources();
        String[] modes = resources.getStringArray(R.array.senegal_operating_equipment_mode_acquisition_list);

        int index = 1;
        boolean notSet = true;

        for (String mode: modes) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, acquisitionModeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(mode);
            rb.setTag(acquisitionModeIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 < modes.length / 2)
                acquisitionModeGroup1.addView(rb);
            else
                acquisitionModeGroup2.addView(rb);

            if (!TextUtils.isEmpty(acquisitionModeText)) {
                if (rb.getTag().equals(acquisitionModeText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            acquisitionModeGroup1.check(11);
    }

    protected void updateInfrastructureCost() {
        String infrastructureCostText = currentInfrastructure.getCost();

        EditText infrastructureCost = (EditText) findViewById(R.id.infrastructureCostText);
        infrastructureCost.setEnabled(!isModeLocked);
        infrastructureCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentInfrastructure.setCost(null);
                else
                    currentInfrastructure.setCost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(infrastructureCostText))
            infrastructureCost.setText(infrastructureCostText);
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
            button.setText(R.string.action_finish);
    }
}
