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
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.ProductionEquipment;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerProductionEquipment extends BaseActivity {
    private ProductionEquipment currentEquipment = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] equipmentIdList = {"silos", "faucheuse", "botteleuse", "equipement_trait", "inc", "eq_tr", "m_roulant"};
    private final static String[] equipmentOperatingIdList = {"comptant", "credit", "don", "location", "autre_acq"};
    private final static String[] equipmentStateIdList = {"neuf", "occasion"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentEquipment = DataHolder.getInstance().getProductionEquipment();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateEquipmentName();
        updateEquipmentNumber();
        updateEquipmentCost();
        updateEquipmentAge();
        updateEquipmentOperating();
        updateEquipmentState();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_production_equipment;
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

            List<ProductionEquipment> equipmentList = ((SenegalSurvey) survey).getProductionEquipments();
            int index = DataHolder.getInstance().getProductionEquipmentIndex();

            if (index > equipmentList.size() - 1)
                return;

            equipmentList.remove(index);
            ((SenegalSurvey) survey).setProductionEquipments(equipmentList);
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
        String[] namesList = resources.getStringArray(R.array.senegal_production_equipment_name_list);

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

    protected void updateEquipmentOperating() {
        String operatingText = currentEquipment.getOperatingEquipment();

        final RadioGroup operatingGroup1 = (RadioGroup) findViewById(R.id.equipmentOperatingGroup1);
        final RadioGroup operatingGroup2 = (RadioGroup) findViewById(R.id.equipmentOperatingGroup2);

        operatingGroup1.setEnabled(!isModeLocked);
        operatingGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (operatingGroup2 != null && operatingGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        operatingGroup2.clearCheck();

                    String id = rb.getTag().toString();
                    currentEquipment.setOperatingEquipment(id);
                }
            }
        });

        operatingGroup2.setEnabled(!isModeLocked);
        operatingGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (operatingGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        operatingGroup1.clearCheck();

                    String id = rb.getTag().toString();
                    currentEquipment.setOperatingEquipment(id);
                }
            }
        });

        Resources resources = getResources();
        String[] modeList = resources.getStringArray(R.array.senegal_operating_equipment_mode_acquisition_list);

        int index = 1;
        boolean notSet = true;

        for (String mode: modeList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, operatingGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(mode);
            rb.setTag(equipmentOperatingIdList[index - 1]);
            rb.setId(index * 11);

            if (index - 1 <= modeList.length / 2)
                operatingGroup1.addView(rb);
            else
                operatingGroup2.addView(rb);

            if (!TextUtils.isEmpty(operatingText)) {
                if (rb.getTag().equals(operatingText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            operatingGroup1.check(11);
    }

    protected void updateEquipmentState() {
        String stateText = currentEquipment.getStateEquipment();

        RadioGroup stateGroup = (RadioGroup) findViewById(R.id.equipmentStateGroup);
        stateGroup.setEnabled(!isModeLocked);
        stateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                currentEquipment.setStateEquipment(id);
            }
        });

        Resources resources = getResources();
        String[] stateList = resources.getStringArray(R.array.senegal_operating_equipment_state_acquisition_list);

        int index = 1;
        boolean notSet = true;

        for (String state: stateList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, stateGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(state);
            rb.setTag(equipmentStateIdList[index - 1]);
            rb.setId(index * 12);

            stateGroup.addView(rb);

            if (!TextUtils.isEmpty(stateText)) {
                if (rb.getTag().equals(stateText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            stateGroup.check(12);
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
