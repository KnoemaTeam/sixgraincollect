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
import org.graindataterminal.models.senegal.EmployeeLivestock;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;

import java.util.List;

public class FarmerEmployeeLivestock extends BaseActivity {
    private EmployeeLivestock currentEmployeeLivestock = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] specialityIdList = {"vet", "zoo", "agr", "gen_rural", "ite", "ate", "commercial_elevage", "comptable", "ressources", "sec", "ouvr", "manoeuvre_elevage", "planton", "chauff", "gar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentEmployeeLivestock = DataHolder.getInstance().getEmployeeLivestock();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateSpeciality();
        updateTotalCount();
        updateMaleEmployeeCount();
        updateFemaleEmployeeCount();
        updatePermanentEmployeeCount();
        updateSeasonalEmployeeCount();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_employee_livestock;
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

            List<EmployeeLivestock> employeeList = ((SenegalSurvey) survey).getEmployeeLivestocks();
            int index = DataHolder.getInstance().getEmployeeLivestockIndex();

            if (index > employeeList.size() - 1)
                return;

            employeeList.remove(index);
            ((SenegalSurvey) survey).setEmployeeLivestocks(employeeList);
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

    protected void updateSpeciality() {
        String specialityText = currentEmployeeLivestock.getEmploySpecialty();

        final RadioGroup specialityGroup1 = (RadioGroup) findViewById(R.id.specialityGroup1);
        final RadioGroup specialityGroup2 = (RadioGroup) findViewById(R.id.specialityGroup2);

        specialityGroup1.setEnabled(!isModeLocked);
        specialityGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (specialityGroup2 != null && specialityGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        specialityGroup2.clearCheck();

                    currentEmployeeLivestock.setEmploySpecialty(rb.getTag().toString());
                    currentEmployeeLivestock.setTitle(rb.getText().toString());
                }
            }
        });

        specialityGroup2.setEnabled(!isModeLocked);
        specialityGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (specialityGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        specialityGroup1.clearCheck();

                    currentEmployeeLivestock.setEmploySpecialty(rb.getTag().toString());
                    currentEmployeeLivestock.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] specialityList = resources.getStringArray(R.array.senegal_employee_livestock_speciality_list);

        int index = 1;
        boolean notSet = true;

        for (String speciality: specialityList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, specialityGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(speciality);
            rb.setTag(specialityIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= specialityList.length / 2)
                specialityGroup1.addView(rb);
            else
                specialityGroup2.addView(rb);

            if (!TextUtils.isEmpty(specialityText)) {
                if (rb.getTag().equals(specialityText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            specialityGroup1.check(10);
    }

    protected void updateTotalCount() {
        String totalCountText = currentEmployeeLivestock.getTotalWorkforceCount();

        EditText totalCount = (EditText) findViewById(R.id.totalCountText);
        totalCount.setEnabled(!isModeLocked);
        totalCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEmployeeLivestock.setTotalWorkforceCount(null);
                else
                    currentEmployeeLivestock.setTotalWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(totalCountText))
            totalCount.setText(totalCountText);
    }

    protected void updateMaleEmployeeCount() {
        String maleCountText = currentEmployeeLivestock.getMaleSpecialteWorkforceCount();

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
                    currentEmployeeLivestock.setMaleSpecialteWorkforceCount(null);
                else
                    currentEmployeeLivestock.setMaleSpecialteWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(maleCountText))
            maleCount.setText(maleCountText);
    }

    protected void updateFemaleEmployeeCount() {
        String femaleCountText = currentEmployeeLivestock.getFemaleSpecialteWorkforceCount();

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
                    currentEmployeeLivestock.setFemaleSpecialteWorkforceCount(null);
                else
                    currentEmployeeLivestock.setFemaleSpecialteWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(femaleCountText))
            femaleCount.setText(femaleCountText);
    }

    protected void updatePermanentEmployeeCount() {
        String permanentCountText = currentEmployeeLivestock.getPermanentWorkforceCount();

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
                    currentEmployeeLivestock.setPermanentWorkforceCount(null);
                else
                    currentEmployeeLivestock.setPermanentWorkforceCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(permanentCountText))
            permanentCount.setText(permanentCountText);
    }

    protected void updateSeasonalEmployeeCount() {
        String seasonalCountText = currentEmployeeLivestock.getSeasonalWorkforceCount();

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
                    currentEmployeeLivestock.setSeasonalWorkforceCount(null);
                else
                    currentEmployeeLivestock.setSeasonalWorkforceCount(s.toString());
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
