package org.odk.collect.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.constants.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static void setSurveysType (String type) {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.SURVEYS_TYPE_KEY, type);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getSurveysType () {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            return sharedPreferences.getString(Constants.SURVEYS_TYPE_KEY, BaseSurvey.SURVEY_TYPE_NONE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return BaseSurvey.SURVEY_TYPE_NONE;
    }

    public static void setSurveyListType(Map<String, String> types) {
        try {
            Type objectType = new TypeToken<Map<String, String>>(){}.getType();
            Gson gson = new GsonBuilder().create();
            String jsonObject = gson.toJson(types, objectType);

            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Constants.SURVEYS_LIST_TYPE_KEY, jsonObject);
            editor.apply();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Map<String, String> getSurveyListType() {
        try {
            SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
            String jsonObject = sharedPreferences.getString(Constants.SURVEYS_LIST_TYPE_KEY, null);

            if (android.text.TextUtils.isEmpty(jsonObject) || jsonObject.equals("null"))
                return BaseSurvey.SURVEY_TYPES;

            Gson gson = new GsonBuilder().create();
            Type objectType = new TypeToken<Map<String, String>>(){}.getType();

            return gson.fromJson(jsonObject,objectType);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return BaseSurvey.SURVEY_TYPES;
    }

    public static void setSurveyList(List surveyList) {
        try {
            for(Object baseSurvey: surveyList) {
                if (Arrays.asList(BaseSurvey.SURVEY_VERSION_NONE).contains(((BaseSurvey) baseSurvey).getSurveyVersion()))
                    continue;

                String version = ((BaseSurvey) baseSurvey).getSurveyVersion();
                File file = Helper.getOutputMediaFile(version.substring(0, 2) + ((BaseSurvey) baseSurvey).getId());

                if (file != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    Type objectType = null;

                    if (Arrays.asList(BaseSurvey.SURVEY_VERSION_ZAMBIA).contains(version))
                        objectType = new TypeToken<ZambiaSurvey>(){}.getType();
                    else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_TUNISIA).contains(version))
                        objectType = new TypeToken<TunisiaSurvey>(){}.getType();
                    else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_SENEGAL).contains(version))
                        objectType = new TypeToken<SenegalSurvey>(){}.getType();
                    else if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(version))
                        objectType = new TypeToken<CameroonSurvey>(){}.getType();

                    if (objectType != null) {
                        String jsonObject = gson.toJson(baseSurvey, objectType);

                        FileOutputStream outputStream = new FileOutputStream(file);
                        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                        streamWriter.write(jsonObject);
                        streamWriter.flush();
                        streamWriter.close();
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static List getSurveyList() {
        try {
            List surveyList = new ArrayList();
            File instancesPath = new File(Constants.INSTANCES_PATH);
            if (instancesPath.isDirectory()) {
                File[] instancesPaths = instancesPath.listFiles();

                for (File instancePath: instancesPaths) {
                    if (instancePath.getName().equals("storage"))
                        continue;

                    if (instancePath.isDirectory()) {
                        String basePath = instancePath.getAbsolutePath() + File.separator + instancePath.getName() + ".txt";
                        File instance = new File(basePath);

                        if (!instance.exists())
                            continue;

                        FileInputStream inputStream = new FileInputStream(instance);
                        InputStreamReader streamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder(inputStream.available());
                        String string;

                        while ((string = bufferedReader.readLine()) != null)
                            stringBuilder.append(string);

                        inputStream.close();

                        String version = instancePath.getName().substring(0, 2);
                        String rawString = stringBuilder.toString();

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();

                        Type objectType = null;
                        switch (version) {
                            case "za":
                                objectType = new TypeToken<ZambiaSurvey>(){}.getType();
                                break;
                            case "tn":
                                objectType = new TypeToken<TunisiaSurvey>(){}.getType();
                                break;
                            case "sn":
                                objectType = new TypeToken<SenegalSurvey>(){}.getType();
                                break;
                            case "cm":
                                objectType = new TypeToken<CameroonSurvey>(){}.getType();
                                break;
                        }

                        if (objectType != null)
                            surveyList.add(gson.fromJson(rawString, objectType));
                    }
                }
            }

            if (surveyList.isEmpty()) {
                SharedPreferences sharedPreferences = Collect.getInstance().getContext().getSharedPreferences(Collect.getInstance().getContext().getString(R.string.app_preferences_name), Context.MODE_PRIVATE);
                String savedPreferences = sharedPreferences.getString(Constants.PREFERENCES_KEY, null);

                if (savedPreferences != null) {
                    String type = DataHolder.getInstance().getSurveysType();
                    Type objectType = new TypeToken<List<BaseSurvey>>() {
                    }.getType();

                    switch (type) {
                        case BaseSurvey.SURVEY_TYPE_ZAMBIA:
                            objectType = new TypeToken<List<ZambiaSurvey>>() {}.getType();
                            break;
                        case BaseSurvey.SURVEY_TYPE_TUNISIA:
                            objectType = new TypeToken<List<TunisiaSurvey>>() {}.getType();
                            break;
                        case BaseSurvey.SURVEY_TYPE_SENEGAL:
                            objectType = new TypeToken<List<SenegalSurvey>>() {}.getType();
                            break;
                        case BaseSurvey.SURVEY_TYPE_CAMEROON:
                            objectType = new TypeToken<List<CameroonSurvey>>() {}.getType();
                            break;
                    }

                    Gson gson = new GsonBuilder().create();
                    surveyList = gson.fromJson(savedPreferences, objectType);
                }
            }

            return surveyList;

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
