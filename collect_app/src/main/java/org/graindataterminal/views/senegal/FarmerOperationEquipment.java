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
import org.graindataterminal.helpers.EditTextInputFilter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.senegal.OperatingEquipment;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerOperationEquipment extends BaseActivity {
    private final static String[] equipmentNameIdList = {"tracteur", "moissoneuse", "ramasseuse", "motoculteur", "semoir", "camion", "motopompe", "pulverisateur", "charette", "groupeelectrogene", "polyculteur", "houe", "souleveuse", "bati_arara", "charrue", "offset", "pressehuile", "moulingrain", "decortiqueuse", "poudreuse", "abreuvoir", "mangeoire", "rampe", "camion_frigo", "couseuse"};
    private final static String[] acquisitionModeIdList = {"comptant", "credit", "c_cr", "don", "location", "autre_acq"};
    private final static String[] acquisitionStateIdList = {"neuf", "occasion"};
    private final static String[] answerIdList = {"oui", "non"};

    private OperatingEquipment currentEquipment = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentEquipment = DataHolder.getInstance().getOperatingEquipment();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolbar();

        updateEquipmentName();
        updateEquipmentCount();
        updateEquipmentCost();
        updateAcquisitionDate();
        updateAcquisitionMode();
        updateHasSubsidized();
        updateAcquisitionState();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_operation_equipment;
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

            List<OperatingEquipment> equipmentList = ((SenegalSurvey) survey).getOperatingEquipments();
            int index = DataHolder.getInstance().getOperatingEquipmentIndex();

            if (index > equipmentList.size() - 1)
                return;

            equipmentList.remove(index);
            ((SenegalSurvey) survey).setOperatingEquipments(equipmentList);
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

        final RadioGroup equipmentTypeGroup1 = (RadioGroup) findViewById(R.id.equipmentTypeGroup1);
        final RadioGroup equipmentTypeGroup2 = (RadioGroup) findViewById(R.id.equipmentTypeGroup2);

        equipmentTypeGroup1.setEnabled(!isModeLocked);
        equipmentTypeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (equipmentTypeGroup2 != null && equipmentTypeGroup2.getCheckedRadioButtonId() != View.NO_ID)
                        equipmentTypeGroup2.clearCheck();

                    currentEquipment.setName(rb.getTag().toString());
                    currentEquipment.setTitle(rb.getText().toString());
                }
            }
        });

        equipmentTypeGroup2.setEnabled(!isModeLocked);
        equipmentTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.isChecked()) {
                    if (equipmentTypeGroup1.getCheckedRadioButtonId() != View.NO_ID)
                        equipmentTypeGroup1.clearCheck();

                    currentEquipment.setName(rb.getTag().toString());
                    currentEquipment.setTitle(rb.getText().toString());
                }
            }
        });

        Resources resources = getResources();
        String[] names = resources.getStringArray(R.array.senegal_operating_equipment_list);

        int index = 1;
        boolean notSet = true;

        for (String name: names) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, equipmentTypeGroup1, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(equipmentNameIdList[index - 1]);
            rb.setId(index * 10);

            if (index - 1 <= names.length / 2)
                equipmentTypeGroup1.addView(rb);
            else
                equipmentTypeGroup2.addView(rb);

            if (!TextUtils.isEmpty(equipmentNameText)) {
                if (rb.getTag().equals(equipmentNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            equipmentTypeGroup1.check(10);
    }

    protected void updateEquipmentCount() {
        String equipmentCountText = currentEquipment.getCount();

        EditText equipmentCount = (EditText) findViewById(R.id.equipmentCountText);
        equipmentCount.setEnabled(!isModeLocked);
        equipmentCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentEquipment.setCount(null);
                else
                    currentEquipment.setCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(equipmentCountText))
            equipmentCount.setText(equipmentCountText);
    }

    protected void updateEquipmentCost() {
        String equipmentCostText = currentEquipment.getCost();

        EditText equipmentCost = (EditText) findViewById(R.id.equipmentCostText);
        equipmentCost.setEnabled(!isModeLocked);
        equipmentCost.addTextChangedListener(new TextWatcher() {
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

        if (!TextUtils.isEmpty(equipmentCostText))
            equipmentCost.setText(equipmentCostText);
    }

    protected void updateAcquisitionDate() {
        String acquisitionDateText = currentEquipment.getLastShoppingDate();

        final EditText acquisitionDate = (EditText) findViewById(R.id.yearAcquisitionText);
        acquisitionDate.setEnabled(!isModeLocked);
        acquisitionDate.setFilters(new InputFilter[]{
                new EditTextInputFilter(EditTextInputFilter.DATE_PATTERN)
        });
        acquisitionDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s)) {
                    currentEquipment.setLastShoppingDate(null);
                } else {
                    if (s.length() == 10) {
                        String correctDate = Helper.compareDate(s.toString());

                        if (correctDate != null) {
                            if (!correctDate.equals(acquisitionDate.getText().toString())) {
                                acquisitionDate.setText(correctDate);
                                acquisitionDate.setSelection(correctDate.length());
                            }

                            currentEquipment.setLastShoppingDate(Helper.getDate("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ssZZ", correctDate));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(acquisitionDateText))
            acquisitionDate.setText(Helper.getDate("yyyy-MM-dd'T'HH:mm:ssZZ", "dd-MM-yyyy", acquisitionDateText));
    }

    protected void updateAcquisitionMode() {
        String acquisitionModeText = currentEquipment.getPurchaseReason();

        final RadioGroup acquisitionModeGroup1 = (RadioGroup) findViewById(R.id.modeAcquisitionGroup1);
        final RadioGroup acquisitionModeGroup2 = (RadioGroup) findViewById(R.id.modeAcquisitionGroup2);

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
                    currentEquipment.setPurchaseReason(id);
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
                    currentEquipment.setPurchaseReason(id);
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

    protected void updateAcquisitionState() {
        String acquisitionStateText = currentEquipment.getStatePurchase();

        RadioGroup acquisitionStateGroup = (RadioGroup) findViewById(R.id.statePurchaseGroup);
        acquisitionStateGroup.setEnabled(!isModeLocked);
        acquisitionStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                currentEquipment.setStatePurchase(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_operating_equipment_state_acquisition_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, acquisitionStateGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(acquisitionStateIdList[index - 1]);
            rb.setId(index * 12);

            acquisitionStateGroup.addView(rb);

            if (!TextUtils.isEmpty(acquisitionStateText)) {
                if (rb.getTag().equals(acquisitionStateText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            acquisitionStateGroup.check(12);
    }

    protected void updateHasSubsidized() {
        String hasSubsidizedText = currentEquipment.getHasSubsidized();

        RadioGroup hasSubsidizedGroup = (RadioGroup) findViewById(R.id.hasSubsidizedGroup);
        hasSubsidizedGroup.setEnabled(!isModeLocked);
        hasSubsidizedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                String id = rb.getTag().toString();

                currentEquipment.setHasSubsidized(id);
            }
        });

        Resources resources = getResources();
        String[] types =  resources.getStringArray(R.array.senegal_answers_list);

        int index = 1;
        boolean notSet = true;

        for (String type: types) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, hasSubsidizedGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(type);
            rb.setTag(answerIdList[index - 1]);
            rb.setId(index * 13);

            hasSubsidizedGroup.addView(rb);

            if (!TextUtils.isEmpty(hasSubsidizedText)) {
                if (rb.getTag().equals(hasSubsidizedText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            hasSubsidizedGroup.check(13);
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
