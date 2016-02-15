package org.graindataterminal.views.base;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.network.LocationService;
import org.graindataterminal.views.system.MessageBox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FieldCenterLocation extends BaseFragment {
    private final static String FIELD_CENTER_GPS_KEY = "field_center_gps_key";
    private final static String LONGITUDE_KEY = "lng";
    private final static String LATITUDE_KEY = "lat";

    private View view = null;
    private Map<String, Double> coordinates = new HashMap<>();
    private FragmentNotificationListener notificationListener = null;
    private TextView centerGPSText = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldCenterLocation fragment = new FieldCenterLocation();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_field_center_location, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateLocationFragment(view);

        final BaseActivity parentActivity = (BaseActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.setNotificationListener(screenIndex, new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (!isModeLocked) {
                        boolean isCoordinateEmpty = coordinates.get(LONGITUDE_KEY) == null || coordinates.get(LATITUDE_KEY) == null;

                        if (!isCoordinateEmpty) {
                            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(survey.getSurveyVersion()))
                                ((CameroonSurvey) survey).getFarmerGeneralInfo().setGeoLocation(new HashMap<String, Double>(coordinates));
                            else
                                survey.setFieldCenterGPS(new HashMap<String, Double>(coordinates));
                        }

                        notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CENTER_GPS_KEY, isCoordinateEmpty);
                    }

                    if (parentActivity.checkRequiredFieldByKey(screenIndex, FIELD_CENTER_GPS_KEY)) {
                        if (survey.getMode() == BaseSurvey.SURVEY_EDIT_MODE)
                            createMessage(MessageBox.DIALOG_TYPE_MESSAGE,
                                    getString(R.string.information_message),
                                    getString(R.string.field_center_gps_location),
                                    getString(R.string.information_message));
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                notificationListener = (FragmentNotificationListener) context;
            }
            catch (ClassCastException exception) {
                throw new ClassCastException(context.toString() + " " + "must implement FarmerNotificationListener");
            }
        }
    }

    protected void updateLocationFragment(View parentView) {
        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(survey.getSurveyVersion()))
            coordinates = ((CameroonSurvey) survey).getFarmerGeneralInfo().getGeoLocation();
        else
            coordinates = survey.getFieldCenterGPS();

        Double longitude = coordinates.get(LONGITUDE_KEY);
        Double latitude = coordinates.get(LATITUDE_KEY);

        String centerGPS = String.format(getString(R.string.field_center_gps_values),
                latitude == null ? 0.0 : latitude,
                longitude == null ? 0.0 : longitude);

        centerGPSText = (TextView) parentView.findViewById(R.id.centerGPSText);
        centerGPSText.setEnabled(!isModeLocked);
        centerGPSText.setText(centerGPS);

        if (!isModeLocked)
            notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CENTER_GPS_KEY, coordinates.get(LONGITUDE_KEY) == null || coordinates.get(LATITUDE_KEY) == null);

        Button button = (Button) parentView.findViewById(R.id.takeLocationButton);
        button.setEnabled(!isModeLocked);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerGPSText.setText(getString(R.string.field_obtaining_location));
                coordinates.clear();

                Location location = LocationService.getInstance().getLocation();
                if (location != null) {
                    coordinates.put(LONGITUDE_KEY, location.getLongitude());
                    coordinates.put(LATITUDE_KEY, location.getLatitude());

                    centerGPSText.setText(String.format(getString(R.string.field_center_gps_new_values),
                            String.valueOf(coordinates.get(LATITUDE_KEY)),
                            String.valueOf(coordinates.get(LONGITUDE_KEY)),
                            location.getProvider(),
                            location.getAccuracy()));

                    if (!coordinates.isEmpty()) {
                        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(survey.getSurveyVersion()))
                            ((CameroonSurvey) survey).getFarmerGeneralInfo().setGeoLocation(new HashMap<String, Double>(coordinates));
                        else
                            survey.setFieldCenterGPS(new HashMap<String, Double>(coordinates));
                    }

                    notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CENTER_GPS_KEY, coordinates.get(LONGITUDE_KEY) == null || coordinates.get(LATITUDE_KEY) == null);
                    LocationService.getInstance().finishObtaining();
                }
                else {
                    centerGPSText.setText("Obtaining location...");
                }

                /*
                if (!isModeLocked)
                    notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CENTER_GPS_KEY, true);

                LocationEngine.getInstance().connectLocationEngine(FieldCenterLocation.this, false);
                */
            }
        });
    }

    /*
    @Override
    public void updateLocation(Location location) {
        Log.i(getClass().getSimpleName(), location.toString());
        coordinates.put(LONGITUDE_KEY, location.getLongitude());
        coordinates.put(LATITUDE_KEY, location.getLatitude());

        centerGPSText.setText(String.format(getString(R.string.field_center_gps_new_values),
                String.valueOf(coordinates.get(LATITUDE_KEY)),
                String.valueOf(coordinates.get(LONGITUDE_KEY)),
                location.getProvider(),
                location.getAccuracy()));

        notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CENTER_GPS_KEY, false);
    }
    */
}
