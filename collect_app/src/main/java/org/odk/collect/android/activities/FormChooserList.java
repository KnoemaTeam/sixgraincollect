package org.odk.collect.android.activities;

import org.graindataterminal.adapters.LinearListAdapter;
import org.graindataterminal.controllers.BaseActivity;
import org.graindataterminal.controllers.ContentPager;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.models.base.BaseSurvey;
import org.graindataterminal.models.base.DataHolder;
import org.graindataterminal.network.SurveySyncTask;
import org.graindataterminal.views.system.MessageBox;
import org.javarosa.core.util.DataUtil;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.constants.Constants;
import org.odk.collect.android.listeners.DeleteInstancesListener;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.listeners.FormDownloaderListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.preferences.SettingsFragment;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.DeleteInstancesTask;
import org.odk.collect.android.tasks.DiskSyncTask;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.tasks.InstanceUploaderTask;
import org.odk.collect.android.utilities.DataUtils;
import org.odk.collect.android.views.DialogConstructor;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormChooserList extends BaseActivity implements DiskSyncListener, DeleteInstancesListener, LoaderManager.LoaderCallbacks<Cursor>, DialogConstructor.NotificationListener, InstanceUploaderListener, FormListDownloaderListener, FormDownloaderListener {
    public static final int FORM_LIST_VIEW_ID = 111110;
    public static final int DATA_LIST_VIEW_ID = 111111;
    //public static final int INSTANCE_DATA_LIST_ID = 111112;

    private static final boolean EXIT = true;
    private static final boolean DO_NOT_EXIT = false;

    private static final String t = "FormChooserList";
    private static final String syncMsgKey = "syncmsgkey";

    private static final String FORM_NAME = "formname";
    private static final String FORM_DETAIL_KEY = "formdetailkey";
    private static final String FORM_ID_DISPLAY = "formiddisplay";
    private static final String FORM_ID_KEY = "formid";
    private static final String FORM_VERSION_KEY = "formversion";

    private SharedPreferences mSettings = null;
    protected LinearLayout mSplashView = null;

    private DiskSyncTask mDiskSyncTask = null;
    private InstanceUploaderTask mInstanceUploaderTask = null;
    private DeleteInstancesTask mDeleteInstancesTask = null;
    private DownloadFormListTask mDownloadFormListTask = null;
    private DownloadFormsTask mDownloadFormsTask = null;

    private ListView mDataListView = null;
    private List<BaseSurvey> mDataList = new ArrayList<>();
    private SparseArray<String> mFormList = new SparseArray<>();
    private Map<String, FormDetails> mFormData = new HashMap<>();
    private List<Map<String, String>> mFormDataList = new ArrayList<>();
    private List<Long> mSelectedDataList = new ArrayList<>();

    private LinearListAdapter mDataAdapter = null;
    private SimpleCursorAdapter mInstanceDataAdapter = null;
    private DialogConstructor mLoadingIndicator = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // must be at the beginning of any activity that can be called from an external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), EXIT);
            return;
        }

        setToolbar();
        setFabButton();
        setFormListView();
        setInstanceListView();

        mLoadingIndicator = new DialogConstructor(this);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(SettingsFragment.SURVEY_SOURCE_URL_KEY, Constants.SURVEY_SOURCE_URL);
        editor.apply();

        mDeleteInstancesTask = (DeleteInstancesTask) getLastNonConfigurationInstance();
        runDiskSynchronizationTask();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.chooser_list_layout;
    }

    @Override
    protected void onResume() {
        if (mDiskSyncTask != null)
            mDiskSyncTask.setDiskSyncListener(this);

        if (mDeleteInstancesTask != null) {
            mDeleteInstancesTask.setDeleteListener(this);
        }

        super.onResume();

        if (mDiskSyncTask != null && mDiskSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            SyncComplete(mDiskSyncTask.getStatusMessage());
        }

        if (mDeleteInstancesTask != null
                && mDeleteInstancesTask.getStatus() == AsyncTask.Status.FINISHED) {
            deleteComplete(mDeleteInstancesTask.getDeleteCount());
        }
    }

    @Override
    protected void onPause() {
        if (mDiskSyncTask != null) {
            mDiskSyncTask.setDiskSyncListener(null);
        }

        if (mDeleteInstancesTask != null ) {
            mDeleteInstancesTask.setDeleteListener(null);
        }

        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(this);
        getSupportLoaderManager().initLoader(DATA_LIST_VIEW_ID, null, this);
    }

    @Override
    protected void onStop() {
        Collect.getInstance().getActivityLogger().logOnStop(this);
        getSupportLoaderManager().destroyLoader(DATA_LIST_VIEW_ID);

        if (mSplashView != null)
            mSplashView.setVisibility(View.GONE);

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instance_chooser_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_interviewer_profile:
                Collect.getInstance().getActivityLogger().logAction(this, "onOptionsItemSelected", "MENU_PREFERENCES");
                //startActivity(new Intent(this, SettingsActivity.class));
                startActivity(new Intent(FormChooserList.this, org.odk.collect.android.activities.InterviewerActivity.class));
                break;

            case R.id.action_update:
                checkApplicationUpdates(true);
                break;

            case R.id.action_send_mail:
                sendMail();
                break;

            case R.id.action_device:
                startActivity(new Intent(this, DeviceActivity.class));
                break;

            case R.id.action_synchronize:
                downloadSurveyFormList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<BaseSurvey> surveys = DataHolder.getInstance().getSurveys();
        BaseSurvey survey = DataHolder.getInstance().getCurrentSurvey();

        if (survey.getMode() == BaseSurvey.SURVEY_READ_MODE)
            return;

        if (resultCode == RESULT_OK) {
            if (requestCode == Helper.ADD_FARMER_REQUEST_CODE) {
                survey.setEndTime(Helper.getDate());
                survey.setUpdateTime(Helper.getDate());

                surveys.add(survey);
                DataUtils.setSurveyList(surveys);

                DataHolder.getInstance().setCurrentSurveyIndex(surveys.size() - 1);
                DataHolder.getInstance().setCurrentSurvey(survey);

                if (!Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(survey.getSurveyVersion())) {
                    Intent intent = new Intent(this, ContentPager.class);
                    startActivity(intent);
                }
            }
        }
        else {
            if (requestCode == Helper.ADD_FARMER_REQUEST_CODE) {
                DataHolder.getInstance().setCurrentSurvey(null);
                DataHolder.getInstance().setCurrentSurveyIndex(0);
            }
        }

        if (mDataAdapter != null)
            mDataAdapter.setData(surveys);
    }

    protected void runDiskSynchronizationTask () {
        // DiskSyncTask checks the disk for any forms not already in the content provider
        // that is, put here by dragging and dropping onto the SDCard
        mDiskSyncTask = (DiskSyncTask) getLastNonConfigurationInstance();
        if (mDiskSyncTask == null) {
            Log.i(t, "Starting new disk sync task");
            mDiskSyncTask = new DiskSyncTask();
            mDiskSyncTask.setDiskSyncListener(this);
            mDiskSyncTask.execute((Void[]) null);
        }
    }

    protected void onListItemClick(BaseSurvey baseSurvey) {
        //Cursor c = (Cursor) mInstanceDataAdapter.getItem(position);
        //Uri instanceUri = ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI, c.getLong(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)));
        Uri instanceUri = ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI, Long.valueOf(baseSurvey.getId()));
        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", instanceUri.toString());

        // the form can be edited if it is incomplete or if, when it was
        // marked as complete, it was determined that it could be edited
        // later.
        //String status = c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS));
        //String strCanEditWhenComplete = c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

        //boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE) || Boolean.parseBoolean(strCanEditWhenComplete);
        boolean canEdit = baseSurvey.getState() != BaseSurvey.SURVEY_STATE_SUBMITTED;
        if (!canEdit) {
            createErrorDialog(getString(R.string.cannot_edit_completed_form), DO_NOT_EXIT);
        }
        else {
            startActivity(new Intent(Intent.ACTION_EDIT, instanceUri));
        }
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setIcon(R.drawable.ic_app_logo);
            }
        }
    }

    protected void setFabButton() {
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = DataHolder.getInstance().getSurveysType();
                switch (type) {
                    case BaseSurvey.SURVEY_TYPE_ZAMBIA:
                    case BaseSurvey.SURVEY_TYPE_TUNISIA:
                    case BaseSurvey.SURVEY_TYPE_SENEGAL:
                    case BaseSurvey.SURVEY_TYPE_CAMEROON:
                        createSurvey(FormChooserList.this);
                        break;

                    default:
                        createNewSurvey(type);
                        break;
                }
            }
        });
    }

    protected void createNewSurvey(String type) {
        Map<String, String> types = DataUtils.getSurveyListType();
        for(HashMap.Entry<String, String> entry: types.entrySet()) {
            if (entry.getKey().length() < 2)
                continue;

            if (entry.getKey().contains(type)) {
                for (int i = 0; i < mFormList.size(); i++) {
                    int key = mFormList.keyAt(i);
                    String value = mFormList.get(key);

                    if (entry.getKey().compareToIgnoreCase(value) == 0) {
                        Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, key);
                        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", formUri.toString());

                        String action = getIntent().getAction();
                        if (Intent.ACTION_PICK.equals(action)) {
                            // caller is waiting on a picked form
                            setResult(RESULT_OK, new Intent().setData(formUri));
                        } else {
                            // caller wants to view/edit a form, so launch formentryactivity
                            startActivity(new Intent(Intent.ACTION_EDIT, formUri));
                        }

                        break;
                    }
                }

                break;
            }
        }
    }

    protected void setFormListView() {
        getSupportLoaderManager().initLoader(FORM_LIST_VIEW_ID, null, this);
    }

    protected void setInstanceListView() {
        getSupportLoaderManager().initLoader(DATA_LIST_VIEW_ID, null, this);

        String[] data = new String[] {InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, InstanceProviderAPI.InstanceColumns.DISPLAY_SUBTEXT};
        int[] view = new int[] {R.id.text1, R.id.text2};

        mDataAdapter = new LinearListAdapter(LinearListAdapter.LIST_VIEW_TYPE_FARMERS, mDataList);
        mInstanceDataAdapter = new SimpleCursorAdapter(this, R.layout.two_item, null, data, view, Adapter.NO_SELECTION);

        mDataListView = (ListView) findViewById(R.id.dataListView);
        mDataListView.setAdapter(mDataAdapter);
        mDataListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mDataListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Cursor c = (Cursor) mInstanceDataAdapter.getItem(position);
                long instanceId = c.getLong(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));

                if (mSelectedDataList.contains(instanceId)) {
                    mSelectedDataList.remove(instanceId);
                    mDataListView.setSelected(false);
                }
                else {
                    mSelectedDataList.add(instanceId);
                    mDataListView.setSelection(position);
                    mDataListView.setSelected(true);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.listview_context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelectedInstances();
                        break;
                    default:
                        break;
                }

                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        mDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseSurvey baseSurvey = mDataList.get(position);
                if (Arrays.asList(BaseSurvey.SURVEY_VERSION_NONE).contains(baseSurvey.getSurveyVersion())) {
                    onListItemClick(baseSurvey);
                } else {
                    DataHolder.getInstance().setCurrentSurvey(baseSurvey);
                    DataHolder.getInstance().setCurrentSurveyIndex(position);

                    String surveyVersion = DataHolder.getInstance().getCurrentSurvey().getSurveyVersion();
                    if (Arrays.asList(BaseSurvey.SURVEY_VERSION_CAMEROON).contains(surveyVersion))
                        editSurvey(FormChooserList.this);
                    else
                        startActivity(new Intent(FormChooserList.this, ContentPager.class));
                }
            }
        });
    }

    protected void setSplashView() {
        if (mDataList.isEmpty()) {
            if (mSplashView != null) {
                mSplashView.setVisibility(View.VISIBLE);
            }
            else {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CoordinatorLayout frameLayout = (CoordinatorLayout) findViewById(R.id.contentFrame);

                mSplashView = (LinearLayout) inflater.inflate(R.layout.app_welcome_view, frameLayout, false);
                frameLayout.addView(mSplashView);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DATA_LIST_VIEW_ID) {
            String sortOrder = InstanceProviderAPI.InstanceColumns.STATUS + " DESC, " + InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
            return new CursorLoader(this, InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, null, null, sortOrder);
        }
        else {
            String sortOrder = FormsColumns.DISPLAY_NAME + " ASC, " + FormsColumns.JR_VERSION + " DESC";
            return new CursorLoader(this, FormsColumns.CONTENT_URI, null, null, null, sortOrder);
        }
        /*
        else {
            String selection = InstanceProviderAPI.InstanceColumns.STATUS + "=? or " + InstanceProviderAPI.InstanceColumns.STATUS + "=?";
            String selectionArgs[] = { InstanceProviderAPI.STATUS_COMPLETE, InstanceProviderAPI.STATUS_SUBMISSION_FAILED };
            String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";

            return new CursorLoader(this, InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection, selectionArgs, sortOrder);
        }
        */
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DATA_LIST_VIEW_ID) {
            mDataList.clear();
            mDataList.addAll(DataHolder.getInstance().getSurveys());

            if (data.getCount() > 0) {
                while (data.moveToNext()) {
                    int idIndex = data.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID);
                    int nameIndex = data.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME);
                    int dateIndex = data.getColumnIndex(InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE);
                    int statusIndex = data.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS);

                    BaseSurvey baseSurvey = new BaseSurvey() {
                        @Override
                        public List getFields() {
                            return null;
                        }
                    };

                    long id = data.getLong(idIndex);
                    String name = data.getString(nameIndex);
                    String date = Helper.getDate(data.getLong(dateIndex));
                    String status = data.getString(statusIndex);

                    switch (status) {
                        case InstanceProviderAPI.STATUS_SUBMITTED:
                            baseSurvey.setState(BaseSurvey.SURVEY_STATE_SUBMITTED);
                            break;
                        case InstanceProviderAPI.STATUS_SUBMISSION_FAILED:
                            baseSurvey.setState(BaseSurvey.SURVEY_STATE_SYNC_ERROR);
                            break;
                        case InstanceProviderAPI.STATUS_INCOMPLETE:
                        case InstanceProviderAPI.STATUS_COMPLETE:
                        default:
                            baseSurvey.setState(BaseSurvey.SURVEY_STATE_NEW);
                            break;
                    }

                    baseSurvey.setId(String.valueOf(id));
                    baseSurvey.setFarmerName(name);
                    baseSurvey.setUpdateTime(date);
                    baseSurvey.setSurveyVersion(BaseSurvey.SURVEY_VERSION_NONE[0]);

                    mDataList.add(baseSurvey);
                }
            }

            Collections.sort(mDataList, new Comparator<BaseSurvey>() {
                @Override
                public int compare(BaseSurvey lhs, BaseSurvey rhs) {
                    return rhs.getUpdateTime().compareTo(lhs.getUpdateTime());
                }
            });

            if (mDataAdapter != null)
                mDataAdapter.setData(mDataList);

            if (mDataList.isEmpty()) {
                setSplashView();
            }
        }
        else if (loader.getId() == FORM_LIST_VIEW_ID) {
            if (data.getCount() > 0) {
                mFormList.clear();

                while (data.moveToNext()) {
                    int formIdIndex = data.getColumnIndex("_id");
                    int formNameIndex = data.getColumnIndex("jrFormId");

                    mFormList.put(data.getInt(formIdIndex), data.getString(formNameIndex));
                }
            }
        }
        /*
        else {
            if (data != null && data.getCount() > 0) {
                List<Long> dataIdList = new ArrayList<>(data.getCount());
                data.moveToPosition(-1);
                while (data.moveToNext()) {
                    int index = data.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID);
                    dataIdList.add(data.getLong(index));
                }

                runInstanceUploaderTask(dataIdList.toArray(new Long[dataIdList.size()]));
            }
            else if (mConstructor != null) {
                mConstructor.stopAnimation();
            }
        }
        */
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mInstanceDataAdapter.swapCursor(null);
    }

    /**
     * Called by DiskSyncTask when the task is finished
     */
    @Override
    public void SyncComplete(String result) {
        Log.i(t, "disk sync task complete");
    }

    /**
     * Creates a dialog with the given message. Will exit the activity when the user preses "ok" if
     * shouldExit is set to true.
     *
     * @param errorMsg
     * @param shouldExit
     */
    private void createErrorDialog(String errorMsg, final boolean shouldExit) {
    	Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        DialogConstructor dialogConstructor = new DialogConstructor(this, DialogConstructor.DIALOG_SINGLE_ANSWER);
        dialogConstructor.setButtonText(getString(R.string.ok));
        dialogConstructor.updateDialog(getString(R.string.information_message), errorMsg);
    }

    @Override
    public void onPositiveClick() {

    }

    @Override
    public void onNegativeClick() {
        if (mDownloadFormListTask != null) {
            mDownloadFormListTask.setDownloaderListener(null);
            mDownloadFormListTask.cancel(true);
            mDownloadFormListTask = null;
        }

        if (mDownloadFormsTask != null) {
            mDownloadFormsTask.setDownloaderListener(null);
            mDownloadFormsTask.cancel(true);
            mDownloadFormsTask = null;
        }
    }

    @Override
    public void progressUpdate(int progress, int total) {

    }

    @Override
    public void uploadingComplete(HashMap<String, String> result) {
        /*
        String selection = "";
        Set<String> keys = result.keySet();
        Iterator<String> it = keys.iterator();
        String[] selectionArgs = new String[keys.size()];

        int i = 0;
        while (it.hasNext()) {
            String id = it.next();
            selection += InstanceProviderAPI.InstanceColumns._ID + "=?";
            selectionArgs[i++] = id;

            if (i != keys.size()) {
                selection += " or ";
            }
        }

        String message = "";
        Cursor results = null;
        try {
            results = getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection, selectionArgs, null);
            if (results != null && results.getCount() > 0) {
                results.moveToPosition(-1);

                while (results.moveToNext()) {
                    String name =
                            results.getString(results.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
                    String id = results.getString(results.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));
                    message += name + " - " + result.get(id) + "\n\n";
                }
            } else {
                message = getString(R.string.no_forms_uploaded);
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        */

        mInstanceUploaderTask.cancel(true);
        mInstanceUploaderTask.setUploaderListener(null);
        mInstanceUploaderTask = null;

        mLoadingIndicator.stopAnimation();
        synchronisePreviousSurveys();

        getSupportLoaderManager().restartLoader(DATA_LIST_VIEW_ID, null, this);
    }

    @Override
    public void authRequest(Uri url, HashMap<String, String> doneSoFar) {

    }

    protected void synchronise() {
        String interviewer = DataHolder.getInstance().getInterviewerName();

        if (TextUtils.isEmpty(interviewer)) {
            createMessage(MessageBox.DIALOG_TYPE_MESSAGE,
                    getString(R.string.information_message),
                    getString(R.string.information_message_interviewer),
                    getString(R.string.information_message));

            return;
        }

        mLoadingIndicator.updateDialog(getString(R.string.uploading_data), "Uploading data...");

        List<Long> dataIdList = new ArrayList<>();
        for (BaseSurvey baseSurvey: mDataList) {
            if (Arrays.asList(BaseSurvey.SURVEY_VERSION_NONE).contains(baseSurvey.getSurveyVersion()) &&
                    baseSurvey.getState() != BaseSurvey.SURVEY_STATE_SUBMITTED)
                dataIdList.add(Long.valueOf((baseSurvey.getId())));
        }

        if (!dataIdList.isEmpty()) {
            runInstanceUploaderTask(dataIdList.toArray(new Long[dataIdList.size()]));
        }
        else {
            mLoadingIndicator.stopAnimation();
            synchronisePreviousSurveys();
        }
    }

    protected void synchronisePreviousSurveys() {
        try {
            for (BaseSurvey survey:  mDataList) {
                if (Arrays.asList(BaseSurvey.SURVEY_VERSION_NONE).contains(survey.getSurveyVersion()))
                    continue;

                new SurveySyncTask(survey, this) {
                    @Override
                    protected void onSuccess(Boolean result) {
                        DataUtils.setSurveyList(DataHolder.getInstance().getSurveys());
                        getSupportLoaderManager().destroyLoader(DATA_LIST_VIEW_ID);
                        getSupportLoaderManager().initLoader(DATA_LIST_VIEW_ID, null, FormChooserList.this);
                    }
                }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void runInstanceUploaderTask (Long[] data) {
        if (mInstanceUploaderTask == null || mInstanceUploaderTask.isCancelled()) {
            mInstanceUploaderTask = new InstanceUploaderTask();
            mInstanceUploaderTask.setUploaderListener(this);
            mInstanceUploaderTask.execute(data);
        }
    }

    private void downloadSurveyFormList() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {
            mFormData = new HashMap<>();

            if (mDownloadFormListTask != null &&
                    mDownloadFormListTask.getStatus() != AsyncTask.Status.FINISHED) {
                return;
            }
            else if (mDownloadFormListTask != null) {
                mDownloadFormListTask.setDownloaderListener(null);
                mDownloadFormListTask.cancel(true);
                mDownloadFormListTask = null;
            }


            mLoadingIndicator.stopAnimation();
            mLoadingIndicator.updateDialog(getString(R.string.downloading_data), "Preparing surveys");

            mDownloadFormListTask = new DownloadFormListTask();
            mDownloadFormListTask.setDownloaderListener(this);
            mDownloadFormListTask.execute();
        }
    }

    private void downloadSurveyForms() {
        List<FormDetails> filesToDownload = new ArrayList<>();
        int totalCount;

        for (Map<String, String> entry: mFormDataList) {
            if (entry.get(FORM_ID_KEY).contains("za20160210") ||
                    entry.get(FORM_ID_KEY).contains("sn20160210") ||
                    entry.get(FORM_ID_KEY).contains("cm20160208") ||
                    entry.get(FORM_ID_KEY).contains("gm20160205")) {
                filesToDownload.add(mFormData.get(entry.get(FORM_DETAIL_KEY)));
            }
        }

        totalCount = filesToDownload.size();
        Collect.getInstance().getActivityLogger().logAction(this, "downloadSelectedFiles", Integer.toString(totalCount));

        if (totalCount > 0) {
            mDownloadFormsTask = new DownloadFormsTask();
            mDownloadFormsTask.setDownloaderListener(this);
            mDownloadFormsTask.execute(filesToDownload);
        }
        else {
            mLoadingIndicator.stopAnimation();
            synchronise();
        }
    }

    @Override
    public void progressUpdate(String currentFile, int progress, int total) {
        mLoadingIndicator.updateDialog(getString(R.string.downloading_data), getString(R.string.fetching_file, currentFile, progress, total));
    }

    @Override
    public void formListDownloadingComplete(HashMap<String, FormDetails> value) {
        Log.i(getClass().getSimpleName(), "Downloaded Form List");

        mDownloadFormListTask.setDownloaderListener(null);
        mDownloadFormListTask = null;

        if (value == null) {
            Log.i(getClass().getSimpleName(), "Form List Downloading returned null.  That shouldn't happen");
            return;
        }

        if (value.containsKey(DownloadFormListTask.DL_AUTH_REQUIRED)) {
            Log.i(getClass().getSimpleName(), "Need authorization");
            return;
        }

        if (value.containsKey(DownloadFormListTask.DL_ERROR_MSG)) {
            Log.i(getClass().getSimpleName(), "Download Failed");
            return;
        }

        mFormData = value;
        mFormDataList.clear();

        List<String> ids = new ArrayList<>(mFormData.keySet());
        for (int i = 0; i < value.size(); i++) {
            String formDetailsKey = ids.get(i);
            FormDetails details = mFormData.get(formDetailsKey);

            Map<String, String> item = new HashMap<>();
            item.put(FORM_NAME, details.formName);
            item.put(FORM_ID_DISPLAY, ((details.formVersion == null) ? "" : (getString(R.string.version) + " " + details.formVersion + " ")) + "ID: " + details.formID);
            item.put(FORM_DETAIL_KEY, formDetailsKey);
            item.put(FORM_ID_KEY, details.formID);
            item.put(FORM_VERSION_KEY, details.formVersion);

            // Insert the new form in alphabetical order.
            if (mFormDataList.size() == 0) {
                mFormDataList.add(item);
            } else {
                int j;
                for (j = 0; j < mFormList.size(); j++) {
                    Map<String, String> compareMe = mFormDataList.get(j);
                    String name = compareMe.get(FORM_NAME);
                    if (!name.equals(mFormData.get(ids.get(i)).formName)) {
                        break;
                    }
                }

                mFormDataList.add(j, item);
            }
        }

        downloadSurveyForms();
    }

    @Override
    public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
        Log.i(getClass().getSimpleName(), "Downloaded Forms");

        if (mDownloadFormsTask != null) {
            mDownloadFormsTask.setDownloaderListener(null);
            mDownloadFormsTask.cancel(true);
            mDownloadFormsTask = null;
        }

        Map<String, String> types = DataUtils.getSurveyListType();
        for (HashMap.Entry<FormDetails, String> entry: result.entrySet()) {
            if (entry.getValue().compareToIgnoreCase("success") != 0)
                continue;

            FormDetails formDetails = entry.getKey();
            if (!types.containsKey(formDetails.formID)) {
                types.put(formDetails.formID, formDetails.formName);
            }
        }

        mLoadingIndicator.stopAnimation();
        mDeleteInstancesTask = (DeleteInstancesTask) getLastNonConfigurationInstance();
        DataUtils.setSurveyListType(types);

        getSupportLoaderManager().restartLoader(FORM_LIST_VIEW_ID, null, FormChooserList.this);
        runDiskSynchronizationTask();
        synchronise();
    }

    private void deleteSelectedInstances() {
        if (mDeleteInstancesTask == null) {
            mDeleteInstancesTask = new DeleteInstancesTask();
            mDeleteInstancesTask.setContentResolver(getContentResolver());
            mDeleteInstancesTask.setDeleteListener(this);
            mDeleteInstancesTask.execute(mSelectedDataList.toArray(new Long[mSelectedDataList.size()]));
        } else {
            Toast.makeText(this, getString(R.string.file_delete_in_progress),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteComplete(int deletedInstances) {
        Log.i(t, "Delete instances complete");
        Collect.getInstance().getActivityLogger().logAction(this, "deleteComplete", Integer.toString(deletedInstances));
        if (deletedInstances == mSelectedDataList.size()) {
            Toast.makeText(this, getString(R.string.file_deleted_ok, deletedInstances), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(t, "Failed to delete " + (mSelectedDataList.size() - deletedInstances) + " instances");
            Toast.makeText(this, getString(R.string.file_deleted_error, mSelectedDataList.size() - deletedInstances, mSelectedDataList.size()), Toast.LENGTH_LONG).show();
        }

        mDeleteInstancesTask = null;
        mSelectedDataList.clear();

        getSupportLoaderManager().restartLoader(DATA_LIST_VIEW_ID, null, FormChooserList.this);
    }
}