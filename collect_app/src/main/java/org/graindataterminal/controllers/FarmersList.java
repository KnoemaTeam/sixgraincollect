package org.graindataterminal.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.graindataterminal.adapters.LinearListAdapter;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.odk.collect.android.R;
import org.graindataterminal.network.SurveySyncTask;
import org.graindataterminal.views.system.MessageBox;

import java.util.Arrays;
import java.util.List;

public class FarmersList extends BaseActivity {
    protected ImageButton addImageButton = null;
    protected ListView dataListView = null;
    protected LinearListAdapter dataAdapter = null;

    protected LinearLayout welcomeScreen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        setContentList();

        setAddImageButton();
        checkApplicationUpdates(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWelcomeScreen();

        if (dataAdapter != null)
            dataAdapter.setData(DataHolder.getInstance().getSurveys());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (welcomeScreen != null) {
            welcomeScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_update).setVisible(true);
        menu.findItem(R.id.action_send_mail).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_synchronize:
                postSurveys();
                break;
        }

        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.base_farmer_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_READ_MODE)
            return;

        if (resultCode == RESULT_OK) {
            if (requestCode == Helper.ADD_FARMER_REQUEST_CODE) {
                survey.setEndTime(Helper.getDate());
                survey.setUpdateTime(Helper.getDate());

                surveys.add(survey);
                MyApp.setSurveyList(surveys);

                DataHolder.getInstance().setCurrentSurveyIndex(surveys.size() - 1);
                DataHolder.getInstance().setCurrentSurvey(survey);

                if (!Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(survey.getSurveyVersion())) {
                    Intent intent = new Intent(this, ContentPager.class);
                    startActivity(intent);
                }
            }
        }
        else {
            if (requestCode == Helper.ADD_FARMER_REQUEST_CODE) {
                DataHolder.getInstance().setCurrentSurvey(null);
                DataHolder.getInstance().setCurrentSurveyIndex(0);
            }
        }

        if (dataAdapter != null)
            dataAdapter.setData(surveys);

        if (!DataHolder.getInstance().existsSurveys())
            welcomeScreen.setVisibility(View.VISIBLE);
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setIcon(R.drawable.ic_app_logo);
            }
        }
    }

    protected void setAddImageButton () {
        addImageButton = (ImageButton) findViewById(R.id.addNewItem);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFarmer(FarmersList.this);
            }
        });
    }

    protected void setWelcomeScreen() {
        List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();

        if (surveys.size() == 0) {
            if (welcomeScreen != null) {
                welcomeScreen.setVisibility(View.VISIBLE);
            }
            else {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CoordinatorLayout frameLayout = (CoordinatorLayout) findViewById(R.id.contentFrame);

                welcomeScreen = (LinearLayout) inflater.inflate(R.layout.app_welcome_view, frameLayout, false);
                frameLayout.addView(welcomeScreen);
            }
        }
    }

    protected void setContentList() {
        final List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();
        dataAdapter = new LinearListAdapter(LinearListAdapter.LIST_VIEW_TYPE_FARMERS, surveys);

        dataListView = (ListView) findViewById(R.id.farmersList);
        dataListView.setAdapter(dataAdapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataHolder.getInstance().setCurrentSurvey(surveys.get(position));
                DataHolder.getInstance().setCurrentSurveyIndex(position);

                String surveyVersion = DataHolder.getInstance().getCurrentSurvey().getSurveyVersion();
                if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(surveyVersion)) {
                    editCurrentFarmer(FarmersList.this);
                }
                else {
                    Intent intent = new Intent(FarmersList.this, ContentPager.class);
                    startActivity(intent);
                }
            }
        });
    }

    protected void postSurveys () {
        String interviewer = DataHolder.getInstance().getInterviewerName();

        if (TextUtils.isEmpty(interviewer)) {
            createMessage(MessageBox.DIALOG_TYPE_MESSAGE,
                    getString(R.string.information_message),
                    getString(R.string.information_message_interviewer),
                    getString(R.string.information_message));

            return;
        }

        try {
            for (BaseSurvey survey:  DataHolder.getInstance().getSurveys())
                new SurveySyncTask(survey, this) {
                    @Override
                    protected void onSuccess(Boolean result) {
                        dataAdapter.setData(DataHolder.getInstance().getSurveys());
                        MyApp.setSurveyList(DataHolder.getInstance().getSurveys());
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        if (getString(R.string.information_message).equals(tag))
            openInterviewerInfo();
        else if (getString(R.string.update_message_text).equals(tag))
            super.onDialogPositiveClick(tag);
    }
}
