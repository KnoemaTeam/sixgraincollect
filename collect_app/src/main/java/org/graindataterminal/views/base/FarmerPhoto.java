package org.graindataterminal.views.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FarmerPhoto extends BaseFragment {
    private String photoPath = null;
    private ImageView photoView = null;

    public static Fragment getInstance(int screenIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(SCREEN_INDEX, screenIndex);

        FarmerPhoto fragment = new FarmerPhoto();
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
        View view = inflater.inflate(R.layout.base_farmer_photo, container, false);

        survey = DataHolder.getInstance().getCurrentSurvey();
        isModeLocked = survey.getMode() == BaseSurvey.SURVEY_READ_MODE || survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED;

        TextView photoTitle = (TextView) view.findViewById(R.id.photoTitle);
        if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion()))
            photoTitle.setText(R.string.farmer_photo_senegal);
        else
            photoTitle.setText(R.string.farmer_photo_zambia);

        photoView = (ImageView) view.findViewById(R.id.photoView);

        Button photoButton = (Button) view.findViewById(R.id.photoButton);
        photoButton.setEnabled(!isModeLocked);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeNewPhoto();
            }
        });

        String farmerPhotoPath = survey.getFarmerPhoto();

        if (!TextUtils.isEmpty(farmerPhotoPath))
            Helper.setImage(photoView, farmerPhotoPath);

        return view;
    }

    protected void takeNewPhoto () {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Context context = Collect.getInstance().getContext();

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = Helper.getOutputMediaFile(survey.getId(), "IMG", ".jpg", Helper.PHOTO_TYPE_FARMER);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }

            if (photoFile != null) {
                photoPath = photoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                startActivityForResult(intent, Helper.FARMER_PHOTO_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Helper.FARMER_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Helper.setImage(photoView, photoPath);
                survey.setFarmerPhoto(photoPath);
            }
        }
    }
}
