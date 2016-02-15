package org.graindataterminal.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.graindataterminal.adapters.NoticeDialogListener;
import org.graindataterminal.helpers.DictionaryManager;
import org.graindataterminal.helpers.ExceptionHandler;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.models.cameroon.CameroonSurvey;
import org.graindataterminal.models.senegal.SenegalCrop;
import org.graindataterminal.models.senegal.SenegalField;
import org.graindataterminal.models.senegal.SenegalSurvey;
import org.graindataterminal.models.tunisia.TunisiaSurvey;
import org.graindataterminal.models.zambia.ZambiaSurvey;
import org.graindataterminal.network.Connection;
import org.graindataterminal.network.HttpDataTask;
import org.graindataterminal.network.HttpGetTask;
import org.odk.collect.android.R;
import org.graindataterminal.network.LocationService;
import org.graindataterminal.views.system.MessageBox;
import org.graindataterminal.views.system.SpinnerIndicator;
import org.odk.collect.android.activities.*;
import org.odk.collect.android.application.Collect;

import java.io.File;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements NoticeDialogListener {
    protected View statusBar = null;
    protected Toolbar toolbar = null;
    protected LoadingIndicator indicator = null;
    protected SpinnerIndicator spinner = null;
    protected String updateURL = Connection.UPDATE_URL;

    protected boolean isUpdateDialogShown = false;
    protected boolean isLockedState = false;

    protected SparseArray<Map<String, Boolean>> requiredScreensFields = new SparseArray<>();
    protected SparseArray<NotificationListener> notificationListener = new SparseArray<>();

    public interface NotificationListener {
        void refreshFragmentView ();
    }

    public void setNotificationListener(Integer screenIndex, NotificationListener notificationListener) {
        this.notificationListener.put(screenIndex, notificationListener);
    }

    public NotificationListener getNotificationListener(Integer screenIndex) {
        return notificationListener.get(screenIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        indicator = new LoadingIndicator(this);
        spinner = SpinnerIndicator.getInstance(this);

        LocationService.getInstance().getLocation();
        LocationService.getInstance().finishObtaining();

        DictionaryManager.getInstance().updateDictionaries();
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getSimpleName(), "onStart");
        //LocationEngine.getInstance().connectLocationEngine(this, false);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        isUpdateDialogShown = false;
        //LocationEngine.getInstance().disconnectLocationEngine();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_settings:
                break;

            case R.id.action_synchronize:
                break;

            case R.id.action_update:
                checkApplicationUpdates(true);
                break;

            case R.id.action_send_mail:
                sendMail();
                break;

            case R.id.action_interviewer:
                openInterviewerInfo();
                break;

            case R.id.action_device:
                openDeviceInfo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected abstract int getLayoutResourceId();

    protected void updateMode (Intent intent, int selectedPage) {
        DataHolder.getInstance().getCurrentSurvey().setMode(BaseSurvey.SURVEY_EDIT_MODE);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        if (selectedPage != -1)
            intent.putExtra(Helper.SELECTED_SURVEY_PAGE, selectedPage);

        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }

    protected void openInterviewerInfo () {
        try {
            Intent intent = new Intent(this, InterviewerActivity.class);
            startActivity(intent);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void openDeviceInfo () {
        try {
            Intent intent = new Intent(this, org.odk.collect.android.activities.DeviceActivity.class);
            startActivity(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void hideSoftKeyboard () {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addNewFarmer (Context context) {
        try {
            int type = DataHolder.getInstance().getSurveysType();
            if (type == BaseSurvey.SURVEY_TYPE_ZAMBIA) {
                ZambiaSurvey zambiaSurvey = new ZambiaSurvey();
                zambiaSurvey.setSurveyVersion(BaseSurvey.SURVEY_VERSION_ZAMBIA[1]);

                DataHolder.getInstance().setCurrentSurvey(zambiaSurvey);
            }
            else if (type == BaseSurvey.SURVEY_TYPE_TUNISIA) {
                TunisiaSurvey tunisiaSurvey = new TunisiaSurvey();
                tunisiaSurvey.setSurveyVersion(BaseSurvey.SURVEY_VERSION_TUNISIA[0]);

                DataHolder.getInstance().setCurrentSurvey(tunisiaSurvey);
            }
            else if (type == BaseSurvey.SURVEY_TYPE_SENEGAL) {
                SenegalSurvey senegalSurvey = new SenegalSurvey();
                senegalSurvey.setSurveyVersion(BaseSurvey.SURVEY_VERSION_SENEGAL[2]);

                DataHolder.getInstance().setCurrentSurvey(senegalSurvey);
            }
            else if (type == BaseSurvey.SURVEY_TYPE_CAMEROON) {
                CameroonSurvey cameroonSurvey = new CameroonSurvey();
                cameroonSurvey.setSurveyVersion(BaseSurvey.SURVEY_VERSION_CAMEROON[1]);

                DataHolder.getInstance().setCurrentSurvey(cameroonSurvey);
            }

            List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();

            DataHolder.getInstance().setCurrentSurveyIndex(surveys.size());
            DataHolder.getInstance().getCurrentSurvey().setAppVersion(MyApp.getAppVersionName());
            DataHolder.getInstance().getCurrentSurvey().setState(BaseSurvey.SURVEY_STATE_NEW);
            DataHolder.getInstance().getCurrentSurvey().setMode(BaseSurvey.SURVEY_EDIT_MODE);
            DataHolder.getInstance().getCurrentSurvey().setStartTime(Helper.getDate());

            Intent intent = new Intent(context, FarmersPager.class);
            startActivityForResult(intent, Helper.ADD_FARMER_REQUEST_CODE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void editCurrentFarmer (Context context) {
        try {
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
            survey.setMode(BaseSurvey.SURVEY_READ_MODE);

            DataHolder.getInstance().setCurrentSurvey(survey.clone());

            Intent intent = new Intent(context, FarmersPager.class);
            startActivityForResult(intent, Helper.EDIT_FARMER_REQUEST_CODE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void deleteSurvey() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

            int index = DataHolder.getInstance().findSurveyIndex(survey.getId());

            File farmerPhoto = Helper.getOutputMediaFile(survey.getId(), "IMG", ".jpg", Helper.PHOTO_TYPE_FARMER);
            if (farmerPhoto != null) {
                if (farmerPhoto.delete())
                    System.out.println("Successful deleted farmer photo");
            }

            List fields = survey.getFields();
            if (fields != null) {
                for (int i = 0; i < fields.size(); i++) {
                    File fieldPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(i), "IMG", ".jpg", Helper.PHOTO_TYPE_FIELD);
                    if (fieldPhoto != null) {
                        if (fieldPhoto.delete())
                            System.out.println("Successful deleted field photo");
                    }

                    if (survey instanceof SenegalSurvey) {
                        List<SenegalCrop> cropsList = ((SenegalField) fields.get(i)).getCropList();
                        for (int j = 0; j < cropsList.size(); j++) {
                            File cropPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(i) + String.valueOf(j), "IMG", ".jpg", Helper.PHOTO_TYPE_CROP);
                            if (cropPhoto != null) {
                                if (cropPhoto.delete())
                                    System.out.println("Successful deleted crop photo");
                            }
                        }
                    } else {
                        File cropPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(i), "IMG", ".jpg", Helper.PHOTO_TYPE_CROP);
                        if (cropPhoto != null) {
                            if (cropPhoto.delete())
                                System.out.println("Successful deleted crop photo");
                        }
                    }
                }
            }

            if (index != -1) {
                surveyList.remove(index);
                MyApp.setSurveyList(surveyList);
            }

            Intent intent = new Intent(this, FarmersList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void deleteField () {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();
            BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();
            List fields = survey.getFields();

            int surveyIndex = DataHolder.getInstance().findSurveyIndex(survey.getId());
            int fieldIndex = DataHolder.getInstance().getCurrentFieldIndex();

            File fieldPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(fieldIndex), "IMG", ".jpg", Helper.PHOTO_TYPE_FIELD);
            if (fieldPhoto != null) {
                if (fieldPhoto.delete())
                    System.out.println("Successful deleted field photo");
            }

            if (survey instanceof SenegalSurvey) {
                List<SenegalCrop> cropsList = ((SenegalField) fields.get(fieldIndex)).getCropList();
                for (int i = 0; i < cropsList.size(); i++) {
                    File cropPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(fieldIndex) + String.valueOf(i), "IMG", ".jpg", Helper.PHOTO_TYPE_CROP);
                    if (cropPhoto != null) {
                        if (cropPhoto.delete())
                            System.out.println("Successful deleted crop photo");
                    }
                }
            }
            else {
                File cropPhoto = Helper.getOutputMediaFile(survey.getId() + String.valueOf(fieldIndex), "IMG", ".jpg", Helper.PHOTO_TYPE_CROP);
                if (cropPhoto != null) {
                    if (cropPhoto.delete())
                        System.out.println("Successful deleted crop photo");
                }
            }

            if (surveyIndex != -1 && fieldIndex <= fields.size() - 1) {
                fields.remove(fieldIndex);
                MyApp.setSurveyList(surveyList);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    protected void createMessage(int type, String title, String message, String tag) {
        MessageBox box = new MessageBox();
        Bundle arguments = new Bundle();
        arguments.putInt(MessageBox.DIALOG_TYPE_KEY, type);
        arguments.putString(MessageBox.DIALOG_TITLE_KEY, title);
        arguments.putString(MessageBox.DIALOG_MESSAGE_KEY, message);
        arguments.putString(MessageBox.DIALOG_TAG_KEY, tag);

        box.setArguments(arguments);
        box.show(getFragmentManager(), tag);
    }

    public void showUpdateMessage() {
        if (!isUpdateDialogShown) {
            isUpdateDialogShown = true;

            createMessage(MessageBox.DIALOG_TYPE_CHOICE,
                    getString(R.string.update_message),
                    getString(R.string.update_message_text),
                    getString(R.string.update_message_text));
        }
    }

    public void showUpdateNotAvailableMessage() {
        createMessage(MessageBox.DIALOG_TYPE_MESSAGE,
                getString(R.string.information_message),
                getString(R.string.update_message_text_not_available),
                getString(R.string.update_message_text_not_available));
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        isUpdateDialogShown = false;

        if (getResources().getString(R.string.update_message_text).equals(tag))
            updateApplication();
    }

    @Override
    public void onDialogNegativeClick(String tag) {
        isUpdateDialogShown = false;
    }

    public void checkApplicationUpdates(final boolean isManualUpdate) {
        if (!Connection.getInstance().isConnectionAvailable())
            return;

        checkChannelsInfo(isManualUpdate);
    }

    protected void checkChannelsInfo(final boolean isManualUpdate) {
        if (isManualUpdate)
            spinner.show();

        new HttpGetTask() {
            @Override
            protected void onSuccess(final String result) {
                if (isManualUpdate)
                    spinner.dismiss();

                if (TextUtils.isEmpty(result))
                    checkVersionInfo(isManualUpdate);
                else {
                    int type = DataHolder.getInstance().getUpdateType();
                    String[] channels = result.split("\\n");

                    if (channels.length >= 2) {
                        String channel = channels[0];

                        if (type == DataHolder.UPDATE_TYPE_BETA)
                            channel = channels[1];

                        if (!TextUtils.isEmpty(channel)) {
                            String[] updateInfo = channel.split("=>");
                            if (updateInfo.length > 0)  {
                                if (isNeedUpdate(updateInfo[1])) {
                                    updateURL = updateInfo[2];
                                    showUpdateMessage();
                                }
                                else if (isManualUpdate) {
                                    showUpdateNotAvailableMessage();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            protected void onError(int responseCode, Exception exception) {
                if (isManualUpdate)
                    spinner.dismiss();

                if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                    checkVersionInfo(isManualUpdate);
            }
        }.execute(Connection.CHANNELS_URL);
    }

    protected void checkVersionInfo(final boolean isManualUpdate) {
        if (isManualUpdate)
            spinner.show();

        new HttpGetTask() {
            @Override
            protected void onSuccess(final String result) {
                if (isManualUpdate)
                    spinner.dismiss();

                if (!TextUtils.isEmpty(result)) {
                    if (isNeedUpdate(result)) {
                        showUpdateMessage();
                    }
                    else if (isManualUpdate)
                        showUpdateNotAvailableMessage();
                }
            }

            @Override
            protected void onError(int responseCode, Exception exception) {
                if (isManualUpdate)
                    spinner.dismiss();
            }
        }.execute(Connection.VERSION_URL);
    }

    protected boolean isNeedUpdate(String result) {
        String currentVersionName = MyApp.getAppVersionName();
        String[] currentVersions = currentVersionName.split("\\.");
        String[] serverVersions = result.split("\\.");

        boolean isAppNeedUpdate = false;

        for (int i = 0; i < currentVersions.length; i++) {
            int currentVersion = Integer.valueOf(currentVersions[i]);
            int serverVersion = Integer.valueOf(serverVersions[i]);

            if (serverVersion > currentVersion) {
                isAppNeedUpdate = true;
                break;
            }
        }

        return isAppNeedUpdate;
    }

    public void updateApplication() {
        new HttpDataTask(this) {
            @Override
            protected void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        File file = new File(result);
                        Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                        updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        updateIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                        Collect.getContext().startActivity(updateIntent);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }

            @Override
            protected void onError(int responseCode, Exception exception) {

            }
        }.execute(updateURL, MyApp.UPDATE_FILE_INSTALLER);
    }

    protected void sendMail() {
        try {
            List<BaseSurvey> surveyList = DataHolder.getInstance().getSurveys();

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

            String jsonString = gson.toJson(surveyList, objectType);
            Helper.sendEmail(this, getString(R.string.action_send_mail_title), getString(R.string.action_send_mail_message), jsonString, Helper.MAIL_TYPE_SURVEY_INFO);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean checkRequiredFieldByKey (Integer screenIndex, String key) {
        Map<String, Boolean> requiredFields = null;

        if (requiredScreensFields != null)
            requiredFields = requiredScreensFields.get(screenIndex);

        if (requiredFields == null)
            return false;

        if (requiredFields.get(key) == null)
            return false;

        return requiredFields.get(key);
    }
}
