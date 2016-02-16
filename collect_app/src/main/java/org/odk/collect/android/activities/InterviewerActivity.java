package org.odk.collect.android.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.odk.collect.android.R;
import org.graindataterminal.network.LocationService;

public class InterviewerActivity extends AppCompatActivity {
    protected TextView interviewerLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interviewer);

        setToolbar();
        setContentList();

        //LocationEngine.getInstance().connectLocationEngine(this, false);
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

    protected void setContentList () {
        Location location = LocationService.getInstance().getLocation();
        interviewerLocation = (TextView) findViewById(R.id.interviewerLocation);
        interviewerLocation.setText(getString(R.string.field_obtaining_location));

        if (location != null)
            interviewerLocation.setText(String.format(getString(R.string.field_center_gps_values), location.getLatitude(), location.getLongitude()));

        LocationService.getInstance().finishObtaining();
        int type = DataHolder.getInstance().getSurveysType();

        updateInterviewerName();
        updateControllerName(type == 2);
        updateSupervisorName(type == 2);
        updateChannelType();
        updateSurveysType();
    }

    protected void updateInterviewerName() {
        String name = DataHolder.getInstance().getInterviewerName();
        EditText interviewerName = (EditText) findViewById(R.id.interviewerName);
        interviewerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataHolder.getInstance().setInterviewerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(name))
            interviewerName.setText(name);
    }

    protected void updateControllerName(boolean isVisible) {
        String controllerNameText = DataHolder.getInstance().getControllerName();

        TextView controllerNameTitle = (TextView) findViewById(R.id.controllerNameTitle);
        controllerNameTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        EditText controllerName = (EditText) findViewById(R.id.controllerName);
        controllerName.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        controllerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataHolder.getInstance().setControllerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(controllerNameText))
            controllerName.setText(controllerNameText);
    }

    protected void updateSupervisorName(boolean isVisible) {
        String supervisorNameText = DataHolder.getInstance().getSupervisorName();

        TextView supervisorNameTitle = (TextView) findViewById(R.id.supervisorNameTitle);
        supervisorNameTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        EditText supervisorName = (EditText) findViewById(R.id.supervisorName);
        supervisorName.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        supervisorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataHolder.getInstance().setSupervisorName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!TextUtils.isEmpty(supervisorNameText))
            supervisorName.setText(supervisorNameText);
    }

    protected void updateSurveysType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.survey_name_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner spinner = (Spinner) findViewById(R.id.surveysType);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        DataHolder.getInstance().setSurveysType(BaseSurvey.SURVEY_TYPE_ZAMBIA);
                        break;
                    case 1:
                        DataHolder.getInstance().setSurveysType(BaseSurvey.SURVEY_TYPE_TUNISIA);
                        break;
                    case 2:
                        DataHolder.getInstance().setSurveysType(BaseSurvey.SURVEY_TYPE_SENEGAL);
                        break;
                    case 3:
                        DataHolder.getInstance().setSurveysType(BaseSurvey.SURVEY_TYPE_CAMEROON);
                        break;
                    case 4:
                        DataHolder.getInstance().setSurveysType(BaseSurvey.SURVEY_TYPE_GAMBIA);
                        break;
                }

                updateControllerName(position == 2);
                updateSupervisorName(position == 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int type = DataHolder.getInstance().getSurveysType();
        if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA)
            spinner.setSelection(0);
        else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA)
            spinner.setSelection(1);
        else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL)
            spinner.setSelection(2);
        else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON)
            spinner.setSelection(3);
    }

    protected void updateChannelType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.update_channel_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner spinner = (Spinner) findViewById(R.id.updateTypeSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DataHolder.getInstance().setUpdateType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int type = DataHolder.getInstance().getUpdateType();
        spinner.setSelection(type);
    }
}
