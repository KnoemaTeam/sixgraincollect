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
import org.graindataterminal.models.senegal.LivestockProduction;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerLivestockProduction extends BaseActivity {
    private LivestockProduction currentProduction = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] productioNameIdList = {"poussins", "oeufs", "lait_litre", "miel", "an_emb", "pol_chair"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentProduction = DataHolder.getInstance().getLivestockProduction();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateProductionName();
        updateProductionAverage();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_livestock_production;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_production));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_production));

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

    protected void deleteProduction() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<LivestockProduction> productionList = ((SenegalSurvey) survey).getLivestockProductions();
            int index = DataHolder.getInstance().getLivestockProductionIndex();

            if (index > productionList.size() - 1)
                return;

            productionList.remove(index);
            ((SenegalSurvey) survey).setLivestockProductions(productionList);
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
                getString(R.string.action_delete_production),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_production),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteProduction();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void updateProductionName() {
        String productionNameText = currentProduction.getName();

        RadioGroup productionNameGroup = (RadioGroup) findViewById(R.id.productionNameGroup);
        productionNameGroup.setEnabled(!isModeLocked);
        productionNameGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);

                currentProduction.setName(rb.getTag().toString());
                currentProduction.setTitle(rb.getText().toString());
            }
        });

        Resources resources = getResources();
        String[] nameList = resources.getStringArray(R.array.senegal_production_name_list);

        int index = 1;
        boolean notSet = true;

        for (String name: nameList) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, productionNameGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(name);
            rb.setTag(productioNameIdList[index - 1]);
            rb.setId(index * 10);

            productionNameGroup.addView(rb);

            if (!TextUtils.isEmpty(productionNameText)) {
                if (rb.getTag().equals(productionNameText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            productionNameGroup.check(10);
    }

    protected void updateProductionAverage() {
        String productionAverageText = currentProduction.getProductAvg();

        EditText productionAverage = (EditText) findViewById(R.id.productAverageText);
        productionAverage.setEnabled(!isModeLocked);
        productionAverage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentProduction.setProductAvg(null);
                else
                    currentProduction.setProductAvg(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(productionAverageText))
            productionAverage.setText(productionAverageText);
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
