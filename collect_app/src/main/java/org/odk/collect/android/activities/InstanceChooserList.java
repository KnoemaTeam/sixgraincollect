/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.FormDownloaderListener;
import org.odk.collect.android.listeners.FormListDownloaderListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.preferences.SettingsActivity;
import org.odk.collect.android.preferences.SettingsFragment;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for displaying all the valid instances in the instance directory.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class InstanceChooserList extends AppCompatActivity implements FormListDownloaderListener, FormDownloaderListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DATA_LIST_VIEW_ID = 111111;

    private static final int PROGRESS_DIALOG = 1;
    private static final int OPEN_PREFERENCES_CODE = 200;

    private static final int CREATE_SURVEY_CODE = 100;
    private static final int EDIT_SURVEY_CODE = 101;

    // Tasks
    private DownloadFormListTask mDownloadFormListTask;
    private DownloadFormsTask mDownloadFormsTask;

    // menu options
    private static final int MENU_PREFERENCES = Menu.FIRST;
    private static final int MENU_ADMIN = Menu.FIRST + 1;

    private static final boolean EXIT = true;
    private static final boolean DO_NOT_EXIT = false;

    private static final String FORMNAME = "formname";
    private static final String FORMDETAIL_KEY = "formdetailkey";
    private static final String FORMID_DISPLAY = "formiddisplay";

    private static final String FORM_ID_KEY = "formid";
    private static final String FORM_VERSION_KEY = "formversion";

    private Map<String, FormDetails> mFormNamesAndURLs = new HashMap<>();
    private List<Map<String, String>> mFormList = new ArrayList<>();

    private SharedPreferences mSettings;
    private boolean mIsFormDownloaded = false;

    private ListView mDataListView = null;
    private SimpleCursorAdapter mCursorAdapter = null;
    //private ProgressDialog mProgressDialog;

    private AlertDialog mAlertDialog;
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

        TextView tv = (TextView) findViewById(R.id.status_text);
        tv.setVisibility(View.GONE);

        setToolbar();
        setFabButton();
        setContentList();
        setUserSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(this);
        getSupportLoaderManager().restartLoader(DATA_LIST_VIEW_ID, null, this);
    }

    @Override
    protected void onStop() {
        Collect.getInstance().getActivityLogger().logOnStop(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(DATA_LIST_VIEW_ID);
        super.onDestroy();
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
                createChosenSurvey();
            }
        });
    }

    protected void setContentList() {
        getSupportLoaderManager().initLoader(DATA_LIST_VIEW_ID, null, this);

        String[] data = new String[] {InstanceColumns.DISPLAY_NAME, InstanceColumns.DISPLAY_SUBTEXT};
        int[] view = new int[] {R.id.text1, R.id.text2};

        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.two_item, null, data, view, Adapter.NO_SELECTION);
        mDataListView = (ListView) findViewById(R.id.dataListView);
        mDataListView.setAdapter(mCursorAdapter);
        mDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
    }

    protected void setUserSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mIsFormDownloaded = mSettings.getBoolean(SettingsFragment.SURVEY_FORM_DOWNLOADED_KEY, false);

        if (!mIsFormDownloaded)
            downloadSurveyFormList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = InstanceColumns.STATUS + " != ?";
        String[] selectionArgs = {InstanceProviderAPI.STATUS_SUBMITTED};
        String sortOrder = InstanceColumns.STATUS + " DESC, " + InstanceColumns.DISPLAY_NAME + " ASC";

        return new CursorLoader(this, InstanceColumns.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
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

                Intent preferences = new Intent(InstanceChooserList.this, SettingsActivity.class);
                startActivity(preferences);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Stores the path of selected instance in the parent class and finishes.
     */
    protected void onListItemClick(int position) {
        Cursor c = (Cursor) mCursorAdapter.getItem(position);
        Uri instanceUri = ContentUris.withAppendedId(InstanceColumns.CONTENT_URI, c.getLong(c.getColumnIndex(InstanceColumns._ID)));

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", instanceUri.toString());
        // the form can be edited if it is incomplete or if, when it was
        // marked as complete, it was determined that it could be edited
        // later.
        String status = c.getString(c.getColumnIndex(InstanceColumns.STATUS));
        String strCanEditWhenComplete = c.getString(c.getColumnIndex(InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

        boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE) || Boolean.parseBoolean(strCanEditWhenComplete);
        if (!canEdit) {
            createErrorDialog(getString(R.string.cannot_edit_completed_form), DO_NOT_EXIT);
        }
        else {
            //
            startActivity(new Intent(Intent.ACTION_EDIT, instanceUri));
        }
    }

    private void createErrorDialog(String errorMsg, final boolean shouldExit) {
        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog",
                        		shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }

    protected void createProgressDialog(String message) {
        mConstructor = new DialogConstructor(this);
        mConstructor.updateDialog(getString(R.string.downloading_data), message);

        /*
        mProgressDialog = new ProgressDialog(this);
        DialogInterface.OnClickListener loadingButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Collect.getInstance().getActivityLogger().logAction(this, "onCreateDialog.PROGRESS_DIALOG", "OK");
                        dialog.dismiss();
                        // we use the same progress dialog for both
                        // so whatever isn't null is running
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
                };
        mProgressDialog.setTitle(getString(R.string.downloading_data));
        mProgressDialog.setMessage(message);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(getString(R.string.cancel), loadingButtonListener);
        mProgressDialog.show();
        */
    }

    private void downloadSurveyFormList() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {

            mFormNamesAndURLs = new HashMap<>();

            if (mDownloadFormListTask != null &&
                    mDownloadFormListTask.getStatus() != AsyncTask.Status.FINISHED) {
                return;
            }
            else if (mDownloadFormListTask != null) {
                mDownloadFormListTask.setDownloaderListener(null);
                mDownloadFormListTask.cancel(true);
                mDownloadFormListTask = null;
            }

            createProgressDialog("Preparing surveys");
            mDownloadFormListTask = new DownloadFormListTask();
            mDownloadFormListTask.setDownloaderListener(this);
            mDownloadFormListTask.execute();
        }
    }

    private void downloadSurveyForms() {
        ArrayList<FormDetails> filesToDownload = new ArrayList<>();
        int totalCount;

        for (Map<String, String> entry: mFormList) {
            if (entry.get(FORM_ID_KEY).contains("ZambiaShort") ||
                    entry.get(FORM_ID_KEY).contains("Senegal")) {
                filesToDownload.add(mFormNamesAndURLs.get(entry.get(FORMDETAIL_KEY)));
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
            Toast.makeText(getApplicationContext(), R.string.noselect_error, Toast.LENGTH_SHORT)
                    .show();
        }
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

        mFormNamesAndURLs = value;
        mFormList.clear();

        ArrayList<String> ids = new ArrayList<>(mFormNamesAndURLs.keySet());
        for (int i = 0; i < value.size(); i++) {
            String formDetailsKey = ids.get(i);
            FormDetails details = mFormNamesAndURLs.get(formDetailsKey);
            Map<String, String> item = new HashMap<>();
            item.put(FORMNAME, details.formName);
            item.put(FORMID_DISPLAY,
                    ((details.formVersion == null) ? "" : (getString(R.string.version) + " " + details.formVersion + " ")) +
                            "ID: " + details.formID);
            item.put(FORMDETAIL_KEY, formDetailsKey);
            item.put(FORM_ID_KEY, details.formID);
            item.put(FORM_VERSION_KEY, details.formVersion);

            // Insert the new form in alphabetical order.
            if (mFormList.size() == 0) {
                mFormList.add(item);
            } else {
                int j;
                for (j = 0; j < mFormList.size(); j++) {
                    Map<String, String> compareMe = mFormList.get(j);
                    String name = compareMe.get(FORMNAME);
                    if (name.compareTo(mFormNamesAndURLs.get(ids.get(i)).formName) > 0) {
                        break;
                    }
                }
                mFormList.add(j, item);
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

        /*
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        */

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(SettingsFragment.SURVEY_FORM_DOWNLOADED_KEY, true);
        editor.apply();
    }

    @Override
    public void progressUpdate(String currentFile, int progress, int total) {
        //mProgressDialog.setMessage(getString(R.string.fetching_file, currentFile, progress, total));
        mConstructor.updateDialog(
                getString(R.string.downloading_data),
                getString(R.string.fetching_file, currentFile, progress, total));
    }

    protected void createChosenSurvey() {
        Cursor cursor = null;
        Map<String, String> surveyMetaData = new HashMap<>();

        String chosenSurveyName = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsFragment.SURVEY_CHOSEN_TYPE_KEY, null);

        try {
            cursor = managedQuery(FormsProviderAPI.FormsColumns.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                int formIdIndex = cursor.getColumnIndex("_id");
                int formNameIndex = cursor.getColumnIndex("jrFormId");

                surveyMetaData.put(cursor.getString(formNameIndex), cursor.getString(formIdIndex));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        if (surveyMetaData.isEmpty()) {
            createErrorDialog("No data about survey", false);
            return;
        }

        // Look for chosen survey and open new activity for this survey
        for (Map.Entry<String, String> entry: surveyMetaData.entrySet()) {
            if (!TextUtils.isEmpty(chosenSurveyName) && entry.getKey().contains(chosenSurveyName)) {
                Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, Integer.valueOf(entry.getValue()));

                startActivity(new Intent(Intent.ACTION_EDIT, formUri));
                break;
            }
        }
    }
}
