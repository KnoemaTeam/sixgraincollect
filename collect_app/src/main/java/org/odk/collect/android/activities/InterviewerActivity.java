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
import org.odk.collect.android.utilities.DataUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        String type = DataHolder.getInstance().getSurveysType();

        updateInterviewerName();
        updateControllerName(false);
        updateSupervisorName(false);
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
        final Map<String, String> typeMap = DataUtils.getSurveyListType();
        final List<String> nameList = new ArrayList<>();

        for (HashMap.Entry<String, String> entry: typeMap.entrySet()) {
            if (entry.getKey().equals(BaseSurvey.SURVEY_TYPE_SENEGAL))
                continue;

            nameList.add(entry.getValue());
        }


        Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner spinner = (Spinner) findViewById(R.id.surveysType);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                for (HashMap.Entry<String, String> entry: typeMap.entrySet())
                    if (entry.getValue().compareToIgnoreCase(name) == 0) {
                        String key = entry.getKey();

                        if (key.equals(BaseSurvey.SURVEY_TYPE_SENEGAL))
                            continue;

                        DataHolder.getInstance().setSurveysType(key);
                        updateControllerName(false);
                        updateSupervisorName(false);

                        break;
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String type = DataHolder.getInstance().getSurveysType();
        int position = nameList.indexOf(typeMap.get(type));

        if (position != View.NO_ID)
            spinner.setSelection(position);
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
