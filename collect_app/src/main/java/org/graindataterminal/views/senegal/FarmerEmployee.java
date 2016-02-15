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
import org.graindataterminal.models.senegal.Employee;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;

import java.util.List;

public class FarmerEmployee extends BaseActivity {
    private Employee currentEmployee = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] qualificatioIdList = {"agronome", "tech_agri", "genie_civil", "commercial", "comptable", "secretaire", "ouvrier", "manoeuvre", "planton", "chauffeur", "gardien", "autre_qualification"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentEmployee = DataHolder.getInstance().getEmployee();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolbar();

        updateQualification();
        updateHeadEmployeeCount();
        updateMaleEmployeeCount();
        updateFemaleEmployeeCount();
        updatePermanentEmployeeCount();
        updateSeasonalEmployeeCount();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_employee;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_employee));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_employee));

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

    protected void deleteEmployee() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<Employee> employeeList = ((SenegalSurvey) survey).getEmployees();
            int index = DataHolder.getInstance().getEmployeeIndex();

            if (index > employeeList.size() - 1)
                return;

            employeeList.remove(index);
            ((SenegalSurvey) survey).setEmployees(employeeList);
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
                getString(R.string.confirmation_message_delete_employee),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_employee),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteEmployee();
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

    protected void updateQualification() {
        String qualificationText = currentEmployee.getQualificationName();

        final RadioGroup qualificationGroup1 = (RadioGroup) findViewById(R.id.qualificationGroup1);
        final RadioGroup qualificationGroup2 = (RadioGroup) findViewById(R.id.qualificationGroup2);

        qualificationGroup1.setEnabled(!isModeLocked);
        qualificationGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (qualificationGroup2 != null && qualificationGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        qualificationGroup2.clearCheck();

                    currentEmployee.setQualificationName(rb.getTag().toString());
                    currentEmployee.setTitle(rb.getText().toString());
                }
            }
        });

        qualificationGroup2.setEnabled(!isModeLocked);
        qualificationGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (qualificationGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        qualificationGroup1.clearCheck();

                    currentEmployee.setQualificationName(rb.getTag().toString());
                    currentEmployee.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] names = resources.getStringArray(R.array.senegal_qualification_list);

        int index = 1;
        boolean notSet = true;

        for (String name: names) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, qualificationGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(qualificatioIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 < names.length / 2)
                qualificationGroup1.addView(rb);
            else
                qualificationGroup2.addView(rb);

            if (!TextUtils.isEmpty(qualificationText)) {
                if (rb.getTag().equals(qualificationText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            qualificationGroup1.check(10);
    }

    protected void updateHeadEmployeeCount() {
        String headCountText = currentEmployee.getHeadWorkforceCount();

        EditText headCount = (EditText) findViewById(R.id.headCountText);
        headCount.setEnabled(!isModeLocked);
        headCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployee.setHeadWorkforceCount(null);
                else
                    currentEmployee.setHeadWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(headCountText))
            headCount.setText(headCountText);
    }

    protected void updateMaleEmployeeCount() {
        String maleCountText = currentEmployee.getMaleWorkforceCount();

        EditText maleCount = (EditText) findViewById(R.id.maleCountText);
        maleCount.setEnabled(!isModeLocked);
        maleCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployee.setMaleWorkforceCount(null);
                else
                    currentEmployee.setMaleWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(maleCountText))
            maleCount.setText(maleCountText);
    }

    protected void updateFemaleEmployeeCount() {
        String femaleCountText = currentEmployee.getFemaleWorkforceCount();

        EditText femaleCount = (EditText) findViewById(R.id.femaleCountText);
        femaleCount.setEnabled(!isModeLocked);
        femaleCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployee.setFemaleWorkforceCount(null);
                else
                    currentEmployee.setFemaleWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(femaleCountText))
            femaleCount.setText(femaleCountText);
    }

    protected void updatePermanentEmployeeCount() {
        String permanentCountText = currentEmployee.getPermanentWorkforceCount();

        EditText permanentCount = (EditText) findViewById(R.id.permanentCountText);
        permanentCount.setEnabled(!isModeLocked);
        permanentCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployee.setPermanentWorkforceCount(null);
                else
                    currentEmployee.setPermanentWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(permanentCountText))
            permanentCount.setText(permanentCountText);
    }

    protected void updateSeasonalEmployeeCount() {
        String seasonalCountText = currentEmployee.getSeasonalWorkforceCount();

        EditText seasonalCount = (EditText) findViewById(R.id.seasonalCountText);
        seasonalCount.setEnabled(!isModeLocked);
        seasonalCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployee.setSeasonalWorkforceCount(null);
                else
                    currentEmployee.setSeasonalWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(seasonalCountText))
            seasonalCount.setText(seasonalCountText);
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
