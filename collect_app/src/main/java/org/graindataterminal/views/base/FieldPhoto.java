package org.graindataterminal.views.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.network.LocationService;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

import java.io.File;
import java.io.IOException;

public class FieldPhoto extends BaseFragment {
    private String photoPath = null;
    private ImageView photoView = null;
    private Button photoButton = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FieldPhoto fragment = new FieldPhoto();
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
        View view = inflater.inflate(R.layout.base_field_photo, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        field = DataHolder.getInstance().getCurrentField();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        if (!isModeLocked) {
            Location location = LocationService.getInstance().getLocation();
            if (location != null) {
                survey.getFieldCenterGPS().put("lat", location.getLatitude());
                survey.getFieldCenterGPS().put("lng", location.getLongitude());
                LocationService.getInstance().finishObtaining();
            }
        }

        photoView = (ImageView) view.findViewById(R.id.photoView);
        photoButton = (Button) view.findViewById(R.id.photoButton);
        photoButton.setEnabled(!isModeLocked);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeNewPhoto();
            }
        });

        if (!TextUtils.isEmpty(field.getFieldPhoto()))
            Helper.setImage(photoView, field.getFieldPhoto());

        return view;
    }

    protected void takeNewPhoto () {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Context context = Collect.getInstance().getContext();

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;

            try {
                String prefix = survey.getId() + String.valueOf(DataHolder.getInstance().getCurrentFieldIndex());
                photoFile = Helper.getOutputMediaFile(prefix, "IMG", ".jpg", Helper.PHOTO_TYPE_FIELD);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }

            if (photoFile != null) {
                photoPath = photoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, Helper.FIELD_PHOTO_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Helper.FIELD_PHOTO_REQUEST_CODE) {
                Helper.setImage(photoView, photoPath);
                field.setFieldPhoto(photoPath);

                Location location = LocationService.getInstance().getLocation();
                if (location != null) {
                    survey.getFieldCenterGPS().put("lat", location.getLatitude());
                    survey.getFieldCenterGPS().put("lng", location.getLongitude());
                    LocationService.getInstance().finishObtaining();
                }
            }
        }
    }
}
