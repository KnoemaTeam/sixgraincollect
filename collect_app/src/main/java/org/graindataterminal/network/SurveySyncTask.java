package org.graindataterminal.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.odk.collect.android.R;
import org.graindataterminal.controllers.LoadingIndicator;
import org.graindataterminal.models.base.BaseCrop;
import org.graindataterminal.models.base.BaseField;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.models.senegal.SenegalSurvey;

import java.util.Arrays;
import java.util.List;

public abstract class SurveySyncTask extends AsyncTask<Void, String, Boolean> {
    protected Activity activity = null;
    protected BaseSurvey survey = null;
    protected LoadingIndicator indicator = null;

    protected final HttpSimpleClient client = new HttpSimpleClient();
    protected abstract void onSuccess(Boolean result);

    public SurveySyncTask(BaseSurvey survey, Activity activity) {
        this.survey = survey;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        indicator = new LoadingIndicator(activity);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values.length == 0)
            indicator.stopAnimation();
        else if (!TextUtils.isEmpty(values[0]) && !TextUtils.isEmpty(values[1]))
            indicator.updateDialog(values[0], values[1]);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (survey.getKey() == null) {
            // Backward compatibility check. Do not touch old submitted surveys again
            if (survey.getState() == BaseSurvey.SURVEY_STATE_SUBMITTED)
                return true;

            survey.setInterviewerName(DataHolder.getInstance().getInterviewerName());

            if (survey instanceof SenegalSurvey) {
                ((SenegalSurvey) survey).setControllerName(DataHolder.getInstance().getControllerName());
                ((SenegalSurvey) survey).setSupervisorName(DataHolder.getInstance().getSupervisorName());
            }

            if (postSurvey() == -1)
                return false;
        }

        Boolean farmPostStatus = postFarmerPhoto();
        Boolean fieldPostStatus = postFieldPhoto();
        Boolean cropPostStatus = postCropPhoto();

        if (farmPostStatus && fieldPostStatus && cropPostStatus) {
            survey.setState(BaseSurvey.SURVEY_STATE_SUBMITTED);
            return true;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        indicator.stopAnimation();
        onSuccess(aBoolean);
    }

    protected Integer postSurvey () {
        Gson gson = new GsonBuilder().create();
        String jsonSurvey = gson.toJson(survey);
        String name = survey.getFarmerName();

        if (TextUtils.isEmpty(name))
            name = survey.getHeadName();

        if (survey instanceof CameroonSurvey)
            name = ((CameroonSurvey) survey).getFarmerGeneralInfo().getFirstName();

        publishProgress(activity.getString(R.string.information_message_uploading_surveys),
                activity.getString(R.string.information_message_uploading_surveys_for) + " " + name);

        String response = client.post(Connection.BASE_URL + "?" + Connection.SECRET_KEY, jsonSurvey);

        if (response != null) {
            Integer result = Integer.valueOf(response);
            survey.setKey(result);

            return result;
        }
        else {
            survey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
            return -1;
        }
    }

    protected Boolean postFarmerPhoto() {
        if (TextUtils.isEmpty(survey.getFarmerPhoto()))
            return true;

        if (survey.getFarmerPhotoKey() != null)
            return true;

        String name = survey.getFarmerName();

        if (TextUtils.isEmpty(name))
            name = survey.getHeadName();

        if (survey instanceof CameroonSurvey)
            name = ((CameroonSurvey) survey).getFarmerGeneralInfo().getFirstName();

        String formattedMessage = String.format(activity.getString(R.string.information_message_uploading_image), "farmer", name);
        publishProgress(activity.getString(R.string.information_message_uploading_surveys), formattedMessage);

        String response = client.postImage(Connection.BASE_URL + "/" + String.valueOf(survey.getKey()) + "/image/farmer/0?" + Connection.SECRET_KEY, survey.getFarmerPhoto());

        if (response != null) {
            Integer result = Integer.valueOf(response);
            survey.setFarmerPhotoKey(result);
            return true;
        }
        else {
            survey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
            return false;
        }
    }

    protected Boolean postFieldPhoto() {
        Boolean result = true;

        List<BaseField> baseFieldList = survey.getFields();
        if (baseFieldList == null || baseFieldList.isEmpty())
            return true;

        String name = survey.getFarmerName();
        if (TextUtils.isEmpty(name))
            name = survey.getHeadName();

        if (survey instanceof CameroonSurvey)
            name = ((CameroonSurvey) survey).getFarmerGeneralInfo().getFirstName();

        for (int i = 0; i < baseFieldList.size(); i++) {
            final BaseField baseField = baseFieldList.get(i);

            if (!TextUtils.isEmpty(baseField.getFieldPhoto()) && baseField.getFieldPhotoKey() == null) {
                String formattedMessage = String.format(activity.getString(R.string.information_message_uploading_image), "field", name);
                publishProgress(activity.getString(R.string.information_message_uploading_surveys), formattedMessage);

                String response = client.postImage(Connection.BASE_URL + "/" + String.valueOf(survey.getKey()) + "/image/field/" + String.valueOf(i) + "?" + Connection.SECRET_KEY, baseField.getFieldPhoto());

                if (response != null)
                    baseField.setFieldPhotoKey(Integer.valueOf(response));
                else {
                    survey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
                    result = false;
                }
            }
        }

        return result;
    }

    protected Boolean postCropPhoto() {
        Boolean result = true;

        List<BaseField> baseFieldList = survey.getFields();
        if (baseFieldList == null || baseFieldList.isEmpty())
            return true;

        String name = survey.getFarmerName();
        if (TextUtils.isEmpty(name))
            name = survey.getHeadName();

        for (int i = 0; i < baseFieldList.size(); i++) {
            final BaseField baseField = baseFieldList.get(i);

            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(survey.getSurveyVersion())) {
                List<SenegalCrop> senegalCropList = ((SenegalField) baseField).getCropList();

                for (int j = 0; j < senegalCropList.size(); j++) {
                    final SenegalCrop senegalCrop = senegalCropList.get(j);

                    if (!TextUtils.isEmpty(senegalCrop.getCropPhoto()) && senegalCrop.getCropPhotoKey() == null) {
                        String formattedMessage = String.format(activity.getString(R.string.information_message_uploading_image), "field", name);
                        publishProgress(activity.getString(R.string.information_message_uploading_surveys), formattedMessage);

                        String response = client.postImage(Connection.BASE_URL + "/" + String.valueOf(survey.getKey()) + "/image/field" + String.valueOf(i) + "crop/" + String.valueOf(j) + "?" + Connection.SECRET_KEY, senegalCrop.getCropPhoto());
                        if (response != null) {
                            senegalCrop.setCropPhotoKey(Integer.valueOf(response));
                        }
                        else {
                            survey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
                            result = false;
                        }
                    }
                }
            }
            else {
                final BaseCrop baseCrop = baseField.getCrop();

                if (baseCrop != null && !TextUtils.isEmpty(baseCrop.getCropPhoto()) && baseCrop.getCropPhotoKey() == null) {
                    String formattedMessage = String.format(activity.getString(R.string.information_message_uploading_image), "field", name);
                    publishProgress(activity.getString(R.string.information_message_uploading_surveys), formattedMessage);

                    String response = client.postImage(Connection.BASE_URL + "/" + String.valueOf(survey.getKey()) + "/image/crop/" + String.valueOf(i) + "?" + Connection.SECRET_KEY, baseCrop.getCropPhoto());
                    if (response != null)
                        baseCrop.setCropPhotoKey(Integer.valueOf(response));
                    else {
                        survey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
                        result = false;
                    }
                }
            }
        }

        return result;
    }
}
