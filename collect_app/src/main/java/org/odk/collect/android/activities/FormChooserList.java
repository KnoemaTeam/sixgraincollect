package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.DiskSyncListener;
import org.odk.collect.android.preferences.SettingsActivity;
import org.odk.collect.android.preferences.SettingsFragment;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.FormsProviderAPI.FormsColumns;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.DiskSyncTask;
import org.odk.collect.android.utilities.VersionHidingCursorAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for displaying all the valid forms in the forms directory. Stores the path to
 * selected form for use by {@link MainMenuActivity}.
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class FormChooserList extends AppCompatActivity implements DiskSyncListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int FORM_LIST_VIEW_ID = 111110;
    public static final int DATA_LIST_VIEW_ID = 111111;

    private static final boolean EXIT = true;
    private static final boolean DO_NOT_EXIT = false;

    private static final String t = "FormChooserList";
    private static final String syncMsgKey = "syncmsgkey";

    private ListView mDataListView = null;
    private SparseArray<String> mFormList = new SparseArray<>();
    private SimpleCursorAdapter mInstanceDataAdapter = null;
    private DiskSyncTask mDiskSyncTask;
    private AlertDialog mAlertDialog;

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

        /*
        String sortOrder = FormsColumns.DISPLAY_NAME + " ASC, " + FormsColumns.JR_VERSION + " DESC";
        Cursor c = managedQuery(FormsColumns.CONTENT_URI, null, null, null, sortOrder);

        String[] data = new String[] {
                FormsColumns.DISPLAY_NAME, FormsColumns.DISPLAY_SUBTEXT, FormsColumns.JR_VERSION
        };
        int[] view = new int[] {
                R.id.text1, R.id.text2, R.id.text3
        };

        // render total instance view
        mFormDataAdapter = new VersionHidingCursorAdapter(FormsColumns.JR_VERSION, this, R.layout.two_item, c, data, view);
        mDataListView = (ListView) findViewById(R.id.dataListView);
        mDataListView.setAdapter(mFormDataAdapter);
        mDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });
        */

        if (savedInstanceState != null && savedInstanceState.containsKey(syncMsgKey)) {
            TextView tv = (TextView) findViewById(R.id.status_text);
            tv.setText(savedInstanceState.getString(syncMsgKey));
        }

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

    @Override
    protected void onResume() {
        mDiskSyncTask.setDiskSyncListener(this);
        super.onResume();

        if (mDiskSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            SyncComplete(mDiskSyncTask.getStatusMessage());
        }
    }

    @Override
    protected void onPause() {
        mDiskSyncTask.setDiskSyncListener(null);
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

    /*
    @Override
    public Object onRetainNonConfigurationInstance() {
        // pass the thread on restart
        return mDiskSyncTask;
    }
    */

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
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(int position) {
        /*
        // get uri to form
    	long idFormsTable = ((SimpleCursorAdapter) mDataListAdapter).getItemId(position);
        Uri formUri = ContentUris.withAppendedId(FormsColumns.CONTENT_URI, idFormsTable);

		Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", formUri.toString());

        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(formUri));
        } else {
            // caller wants to view/edit a form, so launch formentryactivity
            startActivity(new Intent(Intent.ACTION_EDIT, formUri));
        }
        */
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
            //
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
            //String selection = InstanceProviderAPI.InstanceColumns.STATUS + " != ?";
            //String[] selectionArgs = {InstanceProviderAPI.STATUS_SUBMITTED};
            String sortOrder = InstanceProviderAPI.InstanceColumns.STATUS + " DESC, " + InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";

            return new CursorLoader(this, InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, null, null, sortOrder);
        }
        else {
            String sortOrder = FormsColumns.DISPLAY_NAME + " ASC, " + FormsColumns.JR_VERSION + " DESC";
            return new CursorLoader(this, FormsColumns.CONTENT_URI, null, null, null, sortOrder);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DATA_LIST_VIEW_ID)
            mInstanceDataAdapter.swapCursor(data);
        else {
            if (data != null) {
                while (data.moveToNext()) {
                    int formIdIndex = data.getColumnIndex("_id");
                    int formNameIndex = data.getColumnIndex("jrFormId");

                    mFormList.put(data.getInt(formIdIndex), data.getString(formNameIndex));
                }
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
}