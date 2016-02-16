package org.odk.collect.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.constants.Constants;

import java.lang.reflect.Type;
import java.util.List;

public class DataUtils {

    public static void setUpdateType(int type) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(Constants.UPDATE_TYPE_KEY, type);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int getUpdateType() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Constants.UPDATE_TYPE_KEY, -1);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return -1;
    }

    public static void setInterviewerName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.INTERVIEWER_NAME_KEY, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getInterviewerName() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getString(Constants.INTERVIEWER_NAME_KEY, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setControllerName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.CONTROLLER_NAME_KEY, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getControllerName() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getString(Constants.CONTROLLER_NAME_KEY, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setSupervisorName (String interviewer) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.SUPERVISOR_NAME_KEY, interviewer);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getSupervisorName() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getString(Constants.SUPERVISOR_NAME_KEY, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void setSurveysType (int type) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(Constants.SURVEYS_TYPE_KEY, type);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int getSurveysType () {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Constants.SURVEYS_TYPE_KEY, BaseSurvey.SURVEY_TYPE_NONE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return BaseSurvey.SURVEY_TYPE_NONE;
    }

    public static void setSurveyList(List surveyList) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            int type = DataHolder.getInstance().getSurveysType();
            Type objectType = new TypeToken<List<BaseSurvey>>(){}.getType();

            if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA)
                objectType = new TypeToken<List<ZambiaSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA)
                objectType = new TypeToken<List<TunisiaSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL)
                objectType = new TypeToken<List<SenegalSurvey>>(){}.getType();
            else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON)
                objectType = new TypeToken<List<CameroonSurvey>>(){}.getType();

            String jsonString = gson.toJson(surveyList, objectType);
            editor.putString(Constants.PREFERENCES_KEY, jsonString);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static List getSurveyList() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            String savedPreferences = sharedPreferences.getString(Constants.PREFERENCES_KEY, null);

            if (savedPreferences != null) {
                int type = DataHolder.getInstance().getSurveysType();
                Type objectType = new TypeToken<List<BaseSurvey>>() {
                }.getType();

                if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA)
                    objectType = new TypeToken<List<ZambiaSurvey>>() {
                    }.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA)
                    objectType = new TypeToken<List<TunisiaSurvey>>() {
                    }.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL)
                    objectType = new TypeToken<List<SenegalSurvey>>() {
                    }.getType();
                else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON)
                    objectType = new TypeToken<List<CameroonSurvey>>() {
                    }.getType();

                Gson gson = new GsonBuilder().create();
                return gson.fromJson(savedPreferences, objectType);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
