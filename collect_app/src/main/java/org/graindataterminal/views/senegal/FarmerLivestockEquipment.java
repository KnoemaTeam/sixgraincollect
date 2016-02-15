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
import org.graindataterminal.models.senegal.LivestockEquipment;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;

import java.util.List;

public class FarmerLivestockEquipment extends BaseActivity {
    private LivestockEquipment currentEquipment = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] equipmentIdList = {"poulailler", "etable", "magasin", "fosse", "hangar", "fabrique", "transformation", "soins_vet", "bat_bureau", "bergerie", "abreuvoir_elevage", "porcerie", "mang"};
    private final static String[] financeSourceIdList = {"credit", "fonds_propres", "f_p_c", "autre_source_financement"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentEquipment = DataHolder.getInstance().getLivestockEquipment();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateEquipmentName();
        updateEquipmentNumber();
        updateEquipmentCost();
        updateEquipmentAge();
        updateEquipmentFinanceSource();
        updateEquipmentArea();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_livestock_equipment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_equipment));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_equipment));

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

    protected void deleteEquipment() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<LivestockEquipment> equipmentList = ((SenegalSurvey) survey).getLivestockEquipments();
            int index = DataHolder.getInstance().getLivestockEquipmentIndex();

            if (index > equipmentList.size() - 1)
                return;

            equipmentList.remove(index);
            ((SenegalSurvey) survey).setLivestockEquipments(equipmentList);
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
                getString(R.string.confirmation_message_delete_equipment),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_equipment),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteEquipment();
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

    protected void updateEquipmentName() {
        String equipmentNameText = currentEquipment.getName();

        final RadioGroup equipmentNameGroup1 = (RadioGroup) findViewById(R.id.equipmentNameGroup1);
        final RadioGroup equipmentNameGroup2 = (RadioGroup) findViewById(R.id.equipmentNameGroup2);

        equipmentNameGroup1.setEnabled(!isModeLocked);
        equipmentNameGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (equipmentNameGroup2 != null && equipmentNameGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        equipmentNameGroup2.clearCheck();

                    currentEquipment.setName(rb.getTag().toString());
                    currentEquipment.setTitle(rb.getText().toString());
                }
            }
        });

        equipmentNameGroup2.setEnabled(!isModeLocked);
        equipmentNameGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (equipmentNameGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        equipmentNameGroup1.clearCheck();

                    currentEquipment.setName(rb.getTag().toString());
                    currentEquipment.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] namesList = resources.getStringArray(R.array.senegal_livestock_equipment_name_list);

        int index = 1;
        boolean notSet = true;

        for (String name: namesList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, equipmentNameGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(equipmentIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= namesList.length / 2)
                equipmentNameGroup1.addView(rb);
            else
                equipmentNameGroup2.addView(rb);

            if (!TextUtils.isEmpty(equipmentNameText)) {
                if (rb.getTag().equals(equipmentNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            equipmentNameGroup1.check(10);
    }

    protected void updateEquipmentNumber() {
        String numberText = currentEquipment.getNumber();

        EditText number = (EditText) findViewById(R.id.equipmentNumberText);
        number.setEnabled(!isModeLocked);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEquipment.setNumber(null);
                else
                    currentEquipment.setNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(numberText))
            number.setText(numberText);
    }

    protected void updateEquipmentCost() {
        String costText = currentEquipment.getCost();

        EditText cost = (EditText) findViewById(R.id.equipmentCostText);
        cost.setEnabled(!isModeLocked);
        cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEquipment.setCost(null);
                else
                    currentEquipment.setCost(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(costText))
            cost.setText(costText);
    }

    protected void updateEquipmentAge() {
        String ageText = currentEquipment.getAge();

        EditText age = (EditText) findViewById(R.id.equipmentAgeText);
        age.setEnabled(!isModeLocked);
        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEquipment.setAge(null);
                else
                    currentEquipment.setAge(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(ageText))
            age.setText(ageText);
    }

    protected void updateEquipmentFinanceSource() {
        String financeSourceText = currentEquipment.getFundingSource();

        RadioGroup financeSourceGroup = (RadioGroup) findViewById(R.id.equipmentFinanceSourceGroup);
        financeSourceGroup.setEnabled(!isModeLocked);
        financeSourceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                currentEquipment.setFundingSource(id);
            }
        });

        Resources resources = getResources();
        String[] financeSourceList = resources.getStringArray(R.array.senegal_equipment_finance_source_list);

        int index = 1;
        boolean notSet = true;

        for (String source: financeSourceList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, financeSourceGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(source);
            rb.setTag(financeSourceIdList[index - 1]);
            rb.setId(index * 11);

            financeSourceGroup.addView(rb);

            if (!TextUtils.isEmpty(financeSourceText)) {
                if (rb.getTag().equals(financeSourceText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            financeSourceGroup.check(11);
    }

    protected void updateEquipmentArea() {
        String areaText = currentEquipment.getAreaSize();

        EditText area = (EditText) findViewById(R.id.equipmentAreaText);
        area.setEnabled(!isModeLocked);
        area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEquipment.setAreaSize(null);
                else
                    currentEquipment.setAreaSize(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(areaText))
            area.setText(areaText);
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
