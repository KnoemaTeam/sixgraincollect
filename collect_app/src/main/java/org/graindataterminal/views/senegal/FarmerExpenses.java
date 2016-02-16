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
import org.graindataterminal.models.senegal.Expenses;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.odk.collect.android.R;
import org.graindataterminal.views.system.MessageBox;
import org.odk.collect.android.utilities.DataUtils;

import java.util.List;

public class FarmerExpenses extends BaseActivity {
    private Expenses currentExpense = null;
    private boolean isModeLocked = false;

    private LayoutInflater inflater = null;
    private final static String[] expensesIdList = {"travaux_entretien", "main_oeuvre", "energie_elect", "energie_acha_carb", "energie_aut", "eau", "total"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();

        SenegalSurvey survey = (SenegalSurvey) DataHolder.getInstance().getCurrentSurvey();
        currentExpense = DataHolder.getInstance().getExpenses();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        updateNatureExpenditure();
        updateExpenseAmount();
        updateDoneButton();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.sn_farmer_expenses;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_mode).setVisible(true);

        menu.findItem(R.id.action_edit_item).setTitle(getString(R.string.action_edit_expense));
        menu.findItem(R.id.action_delete_item).setTitle(getString(R.string.action_delete_expense));

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

    protected void deleteExpense() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            List<Expenses> expensesList = ((SenegalSurvey) survey).getExpenses();
            int index = DataHolder.getInstance().getExpensesIndex();

            if (index > expensesList.size() - 1)
                return;

            expensesList.remove(index);
            ((SenegalSurvey) survey).setExpenses(expensesList);
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
                getString(R.string.confirmation_message_delete_expense),
                Helper.DELETE_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.confirmation_message),
                    getString(R.string.confirmation_message_close_expense),
                    Helper.CLOSE_MESSAGE);
        else
            super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (tag.equals(Helper.DELETE_MESSAGE)) {
            deleteExpense();
        }
        else if (tag.equals(Helper.CLOSE_MESSAGE)) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void updateNatureExpenditure() {
        String natureExpenditureText = currentExpense.getNature();

        RadioGroup natureExpenditureGroup = (RadioGroup) findViewById(R.id.natureExpenditureGroup);
        natureExpenditureGroup.setEnabled(!isModeLocked);
        natureExpenditureGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isModeLocked)
                    return;

                if (checkedId == View.NO_ID)
                    return;

                RadioButton rb = (RadioButton) group.findViewById(checkedId);

                currentExpense.setNature(rb.getTag().toString());
                currentExpense.setTitle(rb.getText().toString());
            }
        });

        Resources resources = getResources();
        String[] natures = resources.getStringArray(R.array.senegal_nature_list);

        int index = 1;
        boolean notSet = true;

        for (String nature: natures) {
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.fragment_radio_button, natureExpenditureGroup, false);
            rb.setEnabled(!isModeLocked);
            rb.setText(nature);
            rb.setTag(expensesIdList[index - 1]);
            rb.setId(index * 10);

            natureExpenditureGroup.addView(rb);

            if (!TextUtils.isEmpty(natureExpenditureText)) {
                if (rb.getTag().equals(natureExpenditureText)) {
                    rb.setChecked(true);
                    notSet = false;
                }
            }

            index++;
        }

        if (notSet)
            natureExpenditureGroup.check(10);
    }

    protected void updateExpenseAmount() {
        String expenseAmountText = currentExpense.getAmount();

        EditText expenseAmount = (EditText) findViewById(R.id.expenseAmountText);
        expenseAmount.setEnabled(!isModeLocked);
        expenseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isModeLocked)
                    return;

                if (TextUtils.isEmpty(s))
                    currentExpense.setAmount(null);
                else
                    currentExpense.setAmount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(expenseAmountText))
            expenseAmount.setText(expenseAmountText);
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
