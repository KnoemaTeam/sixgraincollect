package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.DeleteInstancesListener;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.listeners.FormDownloaderListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.preferences.SettingsActivity;
import org.odk.collect.android.preferences.SettingsFragment;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.DeleteInstancesTask;
import org.odk.collect.android.tasks.DiskSyncTask;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.tasks.InstanceUploaderTask;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for displaying all the valid forms in the forms directory. Stores the path to
 * selected form for use by {@link MainMenuActivity}.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class FormChooserList extends AppCompatActivity implements DiskSyncListener, DeleteInstancesListener, LoaderManager.LoaderCallbacks<Cursor>, DialogConstructor.NotificationListener, InstanceUploaderListener, FormListDownloaderListener, FormDownloaderListener {
    public static final int FORM_LIST_VIEW_ID = 111110;
    public static final int DATA_LIST_VIEW_ID = 111111;
    public static final int INSTANCE_DATA_LIST_ID = 111112;

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

    private DiskSyncTask mDiskSyncTask = null;
    private InstanceUploaderTask mInstanceUploaderTask = null;
    private DeleteInstancesTask mDeleteInstancesTask = null;
    private DownloadFormListTask mDownloadFormListTask = null;
    private DownloadFormsTask mDownloadFormsTask = null;

    private ListView mDataListView = null;
    private SparseArray<String> mFormList = new SparseArray<>();
    private Map<String, FormDetails> mFormData = new HashMap<>();
    private List<Map<String, String>> mFormDataList = new ArrayList<>();
    private List<Long> mSelectedDataList = new ArrayList<>();

    private SimpleCursorAdapter mInstanceDataAdapter = null;
    private DialogConstructor mConstructor = null;

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

        setContentView(R.layout.chooser_list_layout);
        setToolbar();
        setFabButton();
        setFormListView();
        setInstanceListView();

        if (savedInstanceState != null && savedInstanceState.containsKey(syncMsgKey)) {
            TextView tv = (TextView) findViewById(R.id.status_text);
            tv.setText(savedInstanceState.getString(syncMsgKey));
        }

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSettings.getBoolean(SettingsFragment.SURVEY_FORM_DOWNLOADED_KEY, false)) {
            downloadSurveyFormList();
        }
        else {
            mDeleteInstancesTask = (DeleteInstancesTask) getLastNonConfigurationInstance();
            runDiskSynchronizationTask();
        }
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
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView tv = (TextView) findViewById(R.id.status_text);
        outState.putString(syncMsgKey, tv.getText().toString());
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

                Intent preferences = new Intent(this, SettingsActivity.class);
                startActivity(preferences);
                break;

            case R.id.action_synchronize:
                synchronize();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    protected void onListItemClick(int position) {
        Cursor c = (Cursor) mInstanceDataAdapter.getItem(position);
        Uri instanceUri = ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI, c.getLong(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)));

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", instanceUri.toString());
        // the form can be edited if it is incomplete or if, when it was
        // marked as complete, it was determined that it could be edited
        // later.
        String status = c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS));
        String strCanEditWhenComplete = c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

        boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE) || Boolean.parseBoolean(strCanEditWhenComplete);
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
                String type = PreferenceManager.getDefaultSharedPreferences(FormChooserList.this).getString(SettingsFragment.SURVEY_CHOSEN_TYPE_KEY, null);
                if (TextUtils.isEmpty(type))
                    return;

                for (int i = 0; i < mFormList.size(); i++) {
                    int key = mFormList.keyAt(i);
                    String storedType = mFormList.get(key);

                    if (!TextUtils.isEmpty(storedType) && storedType.contains(type)) {
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
            }
        });
    }

    protected void setFormListView() {
        getSupportLoaderManager().initLoader(FORM_LIST_VIEW_ID, null, this);
    }

    protected void setInstanceListView() {
        getSupportLoaderManager().initLoader(DATA_LIST_VIEW_ID, null, this);

        String[] data = new String[] {InstanceProviderAPI.InstanceColumns.DISPLAY_NAME, InstanceProviderAPI.InstanceColumns.DISPLAY_SUBTEXT};
        int[] view = new int[] {R.id.text1, R.id.text2};

        mInstanceDataAdapter = new SimpleCursorAdapter(this, R.layout.two_item, null, data, view, Adapter.NO_SELECTION);
        mDataListView = (ListView) findViewById(R.id.dataListView);
        mDataListView.setAdapter(mInstanceDataAdapter);
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
                onListItemClick(position);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DATA_LIST_VIEW_ID) {
            String sortOrder = InstanceProviderAPI.InstanceColumns.STATUS + " DESC, " + InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
            return new CursorLoader(this, InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, null, null, sortOrder);
        }
        else if (id == FORM_LIST_VIEW_ID) {
            String sortOrder = FormsColumns.DISPLAY_NAME + " ASC, " + FormsColumns.JR_VERSION + " DESC";
            return new CursorLoader(this, FormsColumns.CONTENT_URI, null, null, null, sortOrder);
        }
        else {
            String selection = InstanceProviderAPI.InstanceColumns.STATUS + "=? or " + InstanceProviderAPI.InstanceColumns.STATUS + "=?";
            String selectionArgs[] = { InstanceProviderAPI.STATUS_COMPLETE, InstanceProviderAPI.STATUS_SUBMISSION_FAILED };
            String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";

            return new CursorLoader(this, InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DATA_LIST_VIEW_ID)
            mInstanceDataAdapter.swapCursor(data);
        else if (loader.getId() == FORM_LIST_VIEW_ID) {
            if (data != null) {
                while (data.moveToNext()) {
                    int formIdIndex = data.getColumnIndex("_id");
                    int formNameIndex = data.getColumnIndex("jrFormId");

                    mFormList.put(data.getInt(formIdIndex), data.getString(formNameIndex));
                }
            }
        }
        else {
            if (data != null && data.getCount() > 0) {
                List<Long> dataIdList = new ArrayList<>(data.getCount());
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
        final TextView textView = (TextView) findViewById(R.id.status_text);
        textView.setVisibility(View.VISIBLE);
        textView.setText(result);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.GONE);
            }
        }, 5 * 1000);
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

        mConstructor = new DialogConstructor(this);
        mConstructor.setButtonText(getString(R.string.ok));
        mConstructor.updateDialog("Error", errorMsg);
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

        mConstructor.stopAnimation();
        mConstructor = null;

        mConstructor = new DialogConstructor(FormChooserList.this, DialogConstructor.DIALOG_SINGLE_ANSWER);
        mConstructor.updateDialog(getString(R.string.dialog_title_information), message.trim());
    }

    @Override
    public void authRequest(Uri url, HashMap<String, String> doneSoFar) {

    }

    protected void synchronize() {
        if (mConstructor == null)
            mConstructor = new DialogConstructor(this);

        mConstructor.updateDialog(getString(R.string.uploading_data), "Uploading data...");
        getSupportLoaderManager().initLoader(INSTANCE_DATA_LIST_ID, null, this);
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

            if (mConstructor == null)
                mConstructor = new DialogConstructor(FormChooserList.this);

            mConstructor.stopAnimation();
            mConstructor.updateDialog(getString(R.string.downloading_data), "Preparing surveys");

            mDownloadFormListTask = new DownloadFormListTask();
            mDownloadFormListTask.setDownloaderListener(this);
            mDownloadFormListTask.execute();
        }
    }

    private void downloadSurveyForms() {
        List<FormDetails> filesToDownload = new ArrayList<>();
        int totalCount;

        for (Map<String, String> entry: mFormDataList) {
            if (entry.get(FORM_ID_KEY).contains("ZambiaShort") ||
                    entry.get(FORM_ID_KEY).contains("FieldFormSenegal2")) {
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
            Toast.makeText(getApplicationContext(), R.string.noselect_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void progressUpdate(String currentFile, int progress, int total) {
        if (mConstructor == null)
            mConstructor = new DialogConstructor(FormChooserList.this);

        mConstructor.updateDialog(getString(R.string.downloading_data), getString(R.string.fetching_file, currentFile, progress, total));
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
        }

        if (mConstructor != null)
            mConstructor.stopAnimation();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(SettingsFragment.SURVEY_FORM_DOWNLOADED_KEY, true);
        editor.apply();

        mDeleteInstancesTask = (DeleteInstancesTask) getLastNonConfigurationInstance();
        runDiskSynchronizationTask();
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

        getSupportLoaderManager().restartLoader(DATA_LIST_VIEW_ID, null, this);
    }
}