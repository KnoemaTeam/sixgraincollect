package org.graindataterminal.views.cameroon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.odk.collect.android.R;
import org.graindataterminal.helpers.DictionaryManager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.cameroon.FarmerGeneralInfo;
import org.graindataterminal.views.base.BaseFragment;

public class FarmerRegion extends BaseFragment {
    private static String[] regionIdList = {"adamawa_region", "centre_region", "east_region", "far_north_region", "littoral_region", "north_region", "northwest_region", "south_region", "southwest_region", "west_region"};

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerRegion fragment = new FarmerRegion();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            screenIndex = getArguments().getInt(SCREEN_INDEX, 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cm_farmer_region, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateRegionSpinner(view);

        return view;
    }

    protected void updateRegionSpinner(final View parentView) {
        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String regionText = farmerGeneralInfo.getRegion();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cameroon_region_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner regionSpinner = (Spinner) parentView.findViewById(R.id.regionSpinner);
        regionSpinner.setEnabled(!isModeLocked);
        regionSpinner.setAdapter(adapter);
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ident = regionIdList[position];

                farmerGeneralInfo.setRegion(ident);
                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);

                updateDepartmentSpinner(ident, parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(regionText)) {
            for (int i = 0; i < regionIdList.length; i++) {
                if (regionIdList[i].equals(regionText)) {
                    regionSpinner.setSelection(i);
                    updateDepartmentSpinner(regionIdList[i], parentView);
                    break;
                }
            }
        }
    }

    private void updateDepartmentSpinner(String region, View parentView) {
        int department;

        switch(region) {
            case "adamawa_region":
                department = R.array.cameroon_adamawa_region_list;
                break;

            case "centre_region":
                department = R.array.cameroon_centre_region_list;
                break;

            case "east_region":
                department = R.array.cameroon_east_region_list;
                break;

            case "far_north_region":
                department = R.array.cameroon_far_north_region_list;
                break;

            case "littoral_region":
                department = R.array.cameroon_littoral_region_list;
                break;

            case "north_region":
                department = R.array.cameroon_north_region_list;
                break;

            case "northwest_region":
                department = R.array.cameroon_northwest_region_list;
                break;

            case "south_region":
                department = R.array.cameroon_south_region_list;
                break;

            case "southwest_region":
                department = R.array.cameroon_southwest_region_list;
                break;

            case "west_region":
                department = R.array.cameroon_west_region_list;
                break;

            default:
                department = R.array.cameroon_adamawa_region_list;
                break;
        }

        final FarmerGeneralInfo farmerGeneralInfo = ((CameroonSurvey) survey).getFarmerGeneralInfo();
        String departmentText = farmerGeneralInfo.getDepartement();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), department, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner departmentSpinner = (Spinner) parentView.findViewById(R.id.departmentSpinner);
        departmentSpinner.setEnabled(!isModeLocked);
        departmentSpinner.setAdapter(adapter);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                String text = DictionaryManager.getInstance().findKeyInDictionary(survey.getSurveyVersion(), value, null);

                farmerGeneralInfo.setDepartement(text);
                ((CameroonSurvey) survey).setFarmerGeneralInfo(farmerGeneralInfo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!TextUtils.isEmpty(departmentText)) {
            String text = DictionaryManager.getInstance().findValueInDictionaryWithName(survey.getSurveyVersion(), departmentText);
            int position = adapter.getPosition(text);

            if (position != View.NO_ID) {
                departmentSpinner.setSelection(position);
            }
        }
    }
}
