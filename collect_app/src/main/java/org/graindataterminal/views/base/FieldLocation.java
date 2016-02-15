package org.graindataterminal.views.base;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.FieldsPager;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.network.LocationService;
import org.graindataterminal.views.system.MessageBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldLocation extends BaseFragment {
    private final static int TIME_INTERVAL = 5000;
    private final static String FIELD_CORNER_GPS_KEY = "field_corner_gps_key";
    private final static String LONGITUDE_KEY = "lng";
    private final static String LATITUDE_KEY = "lat";

    private List<Map<String, Double>> corners = null;
    private TextView cornerGPSTextView = null;
    private FragmentNotificationListener notificationListener = null;

    private Handler handler = new Handler();
    private Runnable locationTask = new Runnable() {
        @Override
        public void run() {
            if (corners != null) {
                final Location location = LocationService.getInstance().getLocation();
                if (location != null) {
                    corners.add(new HashMap<String, Double>() {{
                        put(LONGITUDE_KEY, location.getLongitude());
                        put(LATITUDE_KEY, location.getLatitude());
                    }});

                    if (cornerGPSTextView != null) {
                        Map<String, Double> coordinate = corners.get(corners.size() - 1);
                        Double longitude = coordinate.get(LONGITUDE_KEY);
                        Double latitude = coordinate.get(LATITUDE_KEY);

                        String cornerGPS = String.format(getString(R.string.field_center_gps_new_values),
                                String.valueOf(latitude),
                                String.valueOf(longitude),
                                location.getProvider(),
                                location.getAccuracy());

                        cornerGPSTextView.setText(cornerGPS);
                    }
                }

                handler.postDelayed(locationTask, TIME_INTERVAL);
                Log.i(getClass().getSimpleName(), corners.toString());
            }
        }
    };

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldLocation fragment = new FieldLocation();
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
        View view = inflater.inflate(R.layout.base_field_location, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        updateGPSCoordinatesView(view);
        updateLocationButton(view);

        final FieldsPager parentActivity = (FieldsPager) getActivity();
        if (parentActivity != null) {
            parentActivity.setNotificationListener(screenIndex, new BaseActivity.NotificationListener() {
                @Override
                public void refreshFragmentView() {
                    if (!isModeLocked) {
                        field.setCorners(corners);
                        notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CORNER_GPS_KEY, corners.isEmpty());
                    }

                    if (parentActivity.checkRequiredFieldByKey(screenIndex, FIELD_CORNER_GPS_KEY)) {
                        createMessage(MessageBox.DIALOG_TYPE_MESSAGE,
                                getString(R.string.information_message),
                                getString(R.string.field_corners_location_values),
                                getString(R.string.information_message));
                    }

                    stopLocationTask();
                    //LocationEngine.getInstance().stopListenLocationUpdates();
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

    protected void startLocationTask() {
        locationTask.run();
    }

    private void stopLocationTask() {
        handler.removeCallbacks(locationTask);
    }

    protected void updateGPSCoordinatesView(View parentView) {
        corners = field.getCorners();
        cornerGPSTextView = (TextView) parentView.findViewById(R.id.cornerGPSText);

        if (!corners.isEmpty()) {
            Map<String, Double> coordinate = corners.get(corners.size() - 1);
            Double longitude = coordinate.get(LONGITUDE_KEY);
            Double latitude = coordinate.get(LATITUDE_KEY);

            String cornerGPS = String.format(getString(R.string.field_center_gps_values),
                    longitude == null ? 0.0 : longitude,
                    latitude == null ? 0.0 : latitude);

            cornerGPSTextView.setText(cornerGPS);
            cornerGPSTextView.setVisibility(View.VISIBLE);
        }
    }

    protected void updateLocationButton(View parentView) {
        if (!isModeLocked)
            notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CORNER_GPS_KEY, corners.isEmpty());

        final View animationView = parentView.findViewById(R.id.animationView);
        ImageButton button = (ImageButton) parentView.findViewById(R.id.takeLocationButton);
        button.setEnabled(!isModeLocked);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animationView.getAnimation() != null) {
                    animationView.clearAnimation();
                    animationView.setAlpha(0.0f);

                    stopLocationTask();
                    field.setCorners(corners);
                    notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CORNER_GPS_KEY, corners.isEmpty());
                    LocationService.getInstance().finishObtaining();
                }
                else {
                    Animation pulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse_animation);
                    animationView.setAlpha(1.0f);
                    animationView.startAnimation(pulseAnimation);

                    corners.clear();
                    cornerGPSTextView.setVisibility(View.VISIBLE);
                    notificationListener.onRequiredFieldChanged(screenIndex, FIELD_CORNER_GPS_KEY, corners.isEmpty());

                    startLocationTask();
                }
            }
        });
    }

    /*
    @Override
    public void updateLocation(final Location location) {
        Log.i(getClass().getSimpleName(), location.toString());

        corners.add(new HashMap<String, Double>() {{
            put(LONGITUDE_KEY, location.getLongitude());
            put(LATITUDE_KEY, location.getLatitude());
        }});

        if (cornerGPSTextView != null) {
            Map<String, Double> coordinate = corners.get(corners.size() - 1);
            Double longitude = coordinate.get(LONGITUDE_KEY);
            Double latitude = coordinate.get(LATITUDE_KEY);

            String cornerGPS = String.format(getString(R.string.field_center_gps_new_values),
                    String.valueOf(latitude),
                    String.valueOf(longitude),
                    location.getProvider(),
                    location.getAccuracy());

            cornerGPSTextView.setText(cornerGPS);
            cornerGPSTextView.setVisibility(View.VISIBLE);
        }

        field.setCorners(corners);
    }
    */
}