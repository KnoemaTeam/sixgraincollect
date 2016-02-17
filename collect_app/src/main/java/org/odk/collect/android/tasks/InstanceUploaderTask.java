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

package org.odk.collect.android.tasks;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.io.Files;

import org.graindataterminal.models.base.DataHolder;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.constants.Constants;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.logic.PropertyManager;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.preferences.SettingsFragment;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI.InstanceColumns;
import org.odk.collect.android.utilities.WebUtils;
import org.odk.collect.android.utilities.XMLUtils;
import org.opendatakit.httpclientandroidlib.Header;
import org.opendatakit.httpclientandroidlib.HttpResponse;
import org.opendatakit.httpclientandroidlib.HttpStatus;
import org.opendatakit.httpclientandroidlib.client.ClientProtocolException;
import org.opendatakit.httpclientandroidlib.client.HttpClient;
import org.opendatakit.httpclientandroidlib.client.methods.HttpHead;
import org.opendatakit.httpclientandroidlib.client.methods.HttpPost;
import org.opendatakit.httpclientandroidlib.conn.ConnectTimeoutException;
import org.opendatakit.httpclientandroidlib.conn.HttpHostConnectException;
import org.opendatakit.httpclientandroidlib.entity.ContentType;
import org.opendatakit.httpclientandroidlib.entity.FileEntity;
import org.opendatakit.httpclientandroidlib.entity.StringEntity;
import org.opendatakit.httpclientandroidlib.protocol.HttpContext;
import org.opendatakit.httpclientandroidlib.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Background task for uploading completed forms.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class InstanceUploaderTask extends AsyncTask<Long, Integer, InstanceUploaderTask.Outcome> {

    private static final String t = "InstanceUploaderTask";
    // it can take up to 27 seconds to spin up Aggregate
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final String fail = "Error: ";

    private InstanceUploaderListener mStateListener;

    public static class Outcome {
        public Uri mAuthRequestingServer = null;
        public HashMap<String, String> mResults = new HashMap<String,String>();
    }

    /**
     * Uploads to urlString the submission identified by id with filepath of instance
     * @param urlString destination URL
     * @param id
     * @param instanceFilePath
     * @param toUpdate - Instance URL for recording status update.
     * @param httpclient - client connection
     * @param localContext - context (e.g., credentials, cookies) for client connection
     * @param uriRemap - mapping of Uris to avoid redirects on subsequent invocations
     * @return false if credentials are required and we should terminate immediately.
     */
    private boolean uploadOneSubmission(String urlString, String id, String formId, String instanceFilePath,
                                        Uri toUpdate, HttpContext localContext, Map<Uri, Uri> uriRemap, Outcome outcome) {

        Collect.getInstance().getActivityLogger().logAction(this, urlString, instanceFilePath);

        File instanceFile = new File(instanceFilePath);
        ContentValues cv = new ContentValues();
        Uri u = Uri.parse(urlString);
        HttpClient httpclient = WebUtils.createHttpClient(CONNECTION_TIMEOUT);

        boolean openRosaServer = false;
        if (uriRemap.containsKey(u)) {
            // we already issued a head request and got a response,
            // so we know the proper URL to send the submission to
            // and the proper scheme. We also know that it was an
            // OpenRosa compliant server.
            openRosaServer = true;
            u = uriRemap.get(u);

            // if https then enable preemptive basic auth...
            if ( u.getScheme().equals("https") ) {
                WebUtils.enablePreemptiveBasicAuth(localContext, u.getHost());
            }

            Log.i(t, "Using Uri remap for submission " + id + ". Now: " + u.toString());
        } else {

            // if https then enable preemptive basic auth...
            if ( u.getScheme() != null && u.getScheme().equals("https") ) {
                WebUtils.enablePreemptiveBasicAuth(localContext, u.getHost());
            }

            // we need to issue a head request
            HttpHead httpHead = WebUtils.createOpenRosaHttpHead(u);

            // prepare response
            HttpResponse response = null;
            try {
                Log.i(t, "Issuing HEAD request for " + id + " to: " + u.toString());

                response = httpclient.execute(httpHead, localContext);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    // clear the cookies -- should not be necessary?
                    Collect.getInstance().getCookieStore().clear();

                    WebUtils.discardEntityBytes(response);
                    // we need authentication, so stop and return what we've
                    // done so far.
                    outcome.mAuthRequestingServer = u;
                    return false;
                } else if (statusCode == 204) {
                    Header[] locations = response.getHeaders("Location");
                    WebUtils.discardEntityBytes(response);
                    if (locations != null && locations.length == 1) {
                        try {
                            Uri uNew = Uri.parse(URLDecoder.decode(locations[0].getValue(), "utf-8"));
                            if (u.getHost().equalsIgnoreCase(uNew.getHost())) {
                                openRosaServer = true;
                                // trust the server to tell us a new location
                                // ... and possibly to use https instead.
                                uriRemap.put(u, uNew);
                                u = uNew;
                            } else {
                                // Don't follow a redirection attempt to a different host.
                                // We can't tell if this is a spoof or not.
                                outcome.mResults.put(
                                        id,
                                        fail
                                                + "Unexpected redirection attempt to a different host: "
                                                + uNew.toString());
                                cv.put(InstanceColumns.STATUS,
                                        InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                                Collect.getInstance().getContentResolver()
                                        .update(toUpdate, cv, null, null);
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            outcome.mResults.put(id, fail + urlString + " " + e.toString());
                            cv.put(InstanceColumns.STATUS,
                                    InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                            Collect.getInstance().getContentResolver()
                                    .update(toUpdate, cv, null, null);
                            return true;
                        }
                    }
                } else {
                    // may be a server that does not handle
                    WebUtils.discardEntityBytes(response);

                    Log.w(t, "Status code on Head request: " + statusCode);
                    if (statusCode > HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
                        outcome.mResults.put(
                                id,
                                fail
                                        + "Invalid status code on Head request.  If you have a web proxy, you may need to login to your network. ");
                        cv.put(InstanceColumns.STATUS,
                                InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                        Collect.getInstance().getContentResolver()
                                .update(toUpdate, cv, null, null);
                        return true;
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                outcome.mResults.put(id, fail + "Client Protocol Exception");
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                outcome.mResults.put(id, fail + "Connection Timeout");
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                outcome.mResults.put(id, fail + e.toString() + " :: Network Connection Failed");
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                outcome.mResults.put(id, fail + "Connection Timeout");
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            } catch (HttpHostConnectException e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                outcome.mResults.put(id, fail + "Network Connection Refused");
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(t, e.toString());
                WebUtils.clearHttpConnectionManager();
                String msg = e.getMessage();
                if (msg == null) {
                    msg = e.toString();
                }
                outcome.mResults.put(id, fail + "Generic Exception: " + msg);
                cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
                Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
                return true;
            }
        }

        // At this point, we may have updated the uri to use https.
        // This occurs only if the Location header keeps the host name
        // the same. If it specifies a different host name, we error
        // out.
        //
        // And we may have set authentication cookies in our
        // cookiestore (referenced by localContext) that will enable
        // authenticated publication to the server.
        //
        // get instance file

        // Under normal operations, we upload the instanceFile to
        // the server.  However, during the save, there is a failure
        // window that may mark the submission as complete but leave
        // the file-to-be-uploaded with the name "submission.xml" and
        // the plaintext submission files on disk.  In this case,
        // upload the submission.xml and all the files in the directory.
        // This means the plaintext files and the encrypted files
        // will be sent to the server and the server will have to
        // figure out what to do with them.
        File submissionFile = new File(instanceFile.getParentFile(), "submission.xml");
        if ( submissionFile.exists() ) {
            Log.w(t, "submission.xml will be uploaded instead of " + instanceFile.getAbsolutePath());
        } else {
            submissionFile = instanceFile;
        }

        if (!instanceFile.exists() && !submissionFile.exists()) {
            outcome.mResults.put(id, fail + "instance XML file does not exist!");
            cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
            Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
            return true;
        }

        // find all files in parent directory
        File[] allFiles = instanceFile.getParentFile().listFiles();

        // add media files
        List<File> files = new ArrayList<File>();
        for (File f : allFiles) {
            String fileName = f.getName();

            int dotIndex = fileName.lastIndexOf(".");
            String extension = "";
            if (dotIndex != -1) {
                extension = fileName.substring(dotIndex + 1);
            }

            if (fileName.startsWith(".")) {
                // ignore invisible files
                continue;
            }
            if (fileName.equals(instanceFile.getName())) {
                continue; // the xml file has already been added
            } else if (fileName.equals(submissionFile.getName())) {
                continue; // the xml file has already been added
            } else if (openRosaServer) {
                files.add(f);
            } else if (extension.equals("jpg")) { // legacy 0.9x
                files.add(f);
            } else if (extension.equals("3gpp")) { // legacy 0.9x
                files.add(f);
            } else if (extension.equals("3gp")) { // legacy 0.9x
                files.add(f);
            } else if (extension.equals("mp4")) { // legacy 0.9x
                files.add(f);
            } else if (extension.equals("osm")) { // legacy 0.9x
                files.add(f);
            } else {
                Log.w(t, "unrecognized file type " + f.getName());
            }
        }

        try {
            if (!uploadFiles(submissionFile, files,  urlString, id, formId, toUpdate, cv, httpclient, localContext, outcome))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(t, e.toString());
            WebUtils.clearHttpConnectionManager();
            String msg = e.getMessage();
            if (msg == null) {
                msg = e.toString();
            }
            outcome.mResults.put(id, fail + "Generic Exception: " + msg);
            cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
            Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
            return true;
        }

        // if it got here, it must have worked
        outcome.mResults.put(id, Collect.getInstance().getString(R.string.success));
        cv.put(InstanceColumns.STATUS, InstanceProviderAPI.STATUS_SUBMITTED);
        Collect.getInstance().getContentResolver().update(toUpdate, cv, null, null);
        return true;
    }

    // TODO: This method is like 350 lines long, down from 400.
    // still. ridiculous. make it smaller.
    protected Outcome doInBackground(Long... values) {
        Outcome outcome = new Outcome();

        String selection = InstanceColumns._ID + "=?";
        String[] selectionArgs = new String[(values == null) ? 0 : values.length];
        if ( values != null ) {
            for (int i = 0; i < values.length; i++) {
                if (i != values.length - 1) {
                    selection += " or " + InstanceColumns._ID + "=?";
                }
                selectionArgs[i] = values[i].toString();
            }
        }

        String deviceId = new PropertyManager(Collect.getInstance().getApplicationContext())
                .getSingularProperty(PropertyManager.OR_DEVICE_ID_PROPERTY);

        // get shared HttpContext so that authentication and cookies are retained.
        HttpContext localContext = Collect.getInstance().getHttpContext();

        Map<Uri, Uri> uriRemap = new HashMap<Uri, Uri>();

        Cursor c = null;
        try {
            c = Collect.getInstance().getContentResolver()
                    .query(InstanceColumns.CONTENT_URI, null, selection, selectionArgs, null);

            if (c.getCount() > 0) {
                c.moveToPosition(-1);
                while (c.moveToNext()) {
                    if (isCancelled()) {
                        return outcome;
                    }
                    publishProgress(c.getPosition() + 1, c.getCount());
                    String instance = c.getString(c.getColumnIndex(InstanceColumns.INSTANCE_FILE_PATH));
                    String id = c.getString(c.getColumnIndex(InstanceColumns._ID));
                    String formId = c.getString(c.getColumnIndex(InstanceColumns.JR_FORM_ID));
                    Uri toUpdate = Uri.withAppendedPath(InstanceColumns.CONTENT_URI, id);

                    int subIdx = c.getColumnIndex(InstanceColumns.SUBMISSION_URI);
                    //String urlString = c.isNull(subIdx) ? null : c.getString(subIdx);
                    //if (urlString == null) {
                        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Collect.getInstance());
                        //urlString = PreferenceManager.getDefaultSharedPreferences(Collect.getInstance().getContext()).getString(SettingsFragment.SURVEY_UPLOAD_URL_KEY, Collect.getInstance().getString(R.string.default_server_url_for_survey));
                    //}

                    if ( !uploadOneSubmission(Constants.SURVEY_UPLOAD_URL, id, formId, instance, toUpdate, localContext, uriRemap, outcome) ) {
                        return outcome; // get credentials...
                    }
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return outcome;
    }


    @Override
    protected void onPostExecute(Outcome outcome) {
        synchronized (this) {
            if (mStateListener != null) {
                if (outcome.mAuthRequestingServer != null) {
                    mStateListener.authRequest(outcome.mAuthRequestingServer, outcome.mResults);
                } else {
                    mStateListener.uploadingComplete(outcome.mResults);

                    StringBuilder selection = new StringBuilder();
                    Set<String> keys = outcome.mResults.keySet();
                    Iterator<String> it = keys.iterator();

                    String[] selectionArgs = new String[keys.size()+1];
                    int i = 0;
                    selection.append("(");
                    while (it.hasNext()) {
                        String id = it.next();
                        selection.append(InstanceColumns._ID + "=?");
                        selectionArgs[i++] = id;
                        if (i != keys.size()) {
                            selection.append(" or ");
                        }
                    }
                    selection.append(") and status=?");
                    selectionArgs[i] = InstanceProviderAPI.STATUS_SUBMITTED;

                    Cursor results = null;
                    try {
                        results = Collect
                                .getInstance()
                                .getContentResolver()
                                .query(InstanceColumns.CONTENT_URI, null, selection.toString(),
                                        selectionArgs, null);
                        if (results.getCount() > 0) {
                            Long[] toDelete = new Long[results.getCount()];
                            results.moveToPosition(-1);

                            int cnt = 0;
                            while (results.moveToNext()) {
                                toDelete[cnt] = results.getLong(results
                                        .getColumnIndex(InstanceColumns._ID));
                                cnt++;
                            }

                            boolean deleteFlag = PreferenceManager.getDefaultSharedPreferences(
                                    Collect.getInstance().getApplicationContext()).getBoolean(
                                    PreferencesActivity.KEY_DELETE_AFTER_SEND, false);
                            if (deleteFlag) {
                                DeleteInstancesTask dit = new DeleteInstancesTask();
                                dit.setContentResolver(Collect.getInstance().getContentResolver());
                                dit.execute(toDelete);
                            }

                        }
                    } finally {
                        if (results != null) {
                            results.close();
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        synchronized (this) {
            if (mStateListener != null) {
                // update progress and total
                mStateListener.progressUpdate(values[0].intValue(), values[1].intValue());
            }
        }
    }


    public void setUploaderListener(InstanceUploaderListener sl) {
        synchronized (this) {
            mStateListener = sl;
        }
    }


    public static void copyToBytes(InputStream input, OutputStream output,
                                   int bufferSize) throws IOException {
        byte[] buf = new byte[bufferSize];
        int bytesRead = input.read(buf);
        while (bytesRead != -1) {
            output.write(buf, 0, bytesRead);
            bytesRead = input.read(buf);
        }
        output.flush();
    }

    private boolean uploadFiles(File submissionFile, List<File> files, String urlString, String id, String formId, Uri toUpdate, ContentValues cv, HttpClient httpclient, HttpContext localContext, Outcome outcome) throws Exception {
        Integer key = postSurvey(submissionFile, urlString, id, formId, toUpdate, cv, httpclient, localContext, outcome);
        if (key == null)
            return false;

        for (File file : files) {
            if (!postFile(file, key, urlString, id, toUpdate, cv, httpclient, localContext, outcome))
                return false;
        }

        return true;
    }

    private Integer postSurvey(File file, String urlString, String id, String formId, Uri toUpdate, ContentValues cv, HttpClient httpclient, HttpContext localContext, Outcome outcome) throws Exception {
        urlString += Collect.getInstance().getString(R.string.default_url_for_survey);
        Uri uri = Uri.parse(urlString);

        String formFilePath = getFormFilePath(formId);
        String formXml = Files.toString(new File(formFilePath), Charset.forName("UTF-8"));
        String[] arrayProperties = XMLUtils.getArrayProperties(formXml);

        String xml = Files.toString(file, Charset.forName("UTF-8"));
        JSONObject jsonObject = XMLUtils.toJSONObject(xml, arrayProperties);
        jsonObject = jsonObject.getJSONObject((String)jsonObject.names().get(0));
        jsonObject.put("app_version", Collect.getInstance().getAppVersionName());
        jsonObject.put("interviewer", DataHolder.getInstance().getInterviewerName());
        String jsonSurvey = jsonObject.toString();

        HttpPost httppost = WebUtils.createOpenRosaHttpPost(uri);

        StringEntity entity = new StringEntity(jsonSurvey);

        httppost.setEntity(entity);
        httppost.setHeader("Content-type", "application/json");

        HttpResponse response = httpclient.execute(httppost, localContext);
        Integer key = Integer.parseInt(EntityUtils.toString(response.getEntity()));

        if (checkFail(response, urlString, id, toUpdate, cv, outcome))
            return null;

        return key;
    }

    private boolean postFile(File file, Integer key, String urlString, String id, Uri toUpdate, ContentValues cv, HttpClient httpclient, HttpContext localContext, Outcome outcome) throws Exception {
        urlString += String.format(Collect.getInstance().getString(R.string.default_url_for_file), key, file.getName());
        Uri uri = Uri.parse(urlString);

        HttpPost httppost = WebUtils.createOpenRosaHttpPost(uri);

        FileEntity entity = new FileEntity(file, ContentType.create("image/jpeg"));

        httppost.setEntity(entity);
        httppost.setHeader("Accept", "application/json; charset=utf-8");

        HttpResponse response = httpclient.execute(httppost, localContext);

        WebUtils.discardEntityBytes(response);

        if (checkFail(response, urlString, id, toUpdate, cv, outcome))
            return false;

        return true;
    }

    private boolean checkFail(HttpResponse response, String urlString, String id, Uri toUpdate, ContentValues cv, Outcome outcome) {
        int responseCode = response.getStatusLine().getStatusCode();

        Log.i(t, "Response code:" + responseCode);
        if (responseCode != HttpStatus.SC_OK) {
            if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
                // clear the cookies -- should not be necessary?
                Collect.getInstance().getCookieStore().clear();
                outcome.mResults.put(id, fail + response.getStatusLine().getReasonPhrase()
                        + " (" + responseCode + ") at " + urlString);
            } else {
                outcome.mResults.put(id, fail + response.getStatusLine().getReasonPhrase()
                        + " (" + responseCode + ") at " + urlString);
            }
            cv.put(InstanceColumns.STATUS,
                    InstanceProviderAPI.STATUS_SUBMISSION_FAILED);
            Collect.getInstance().getContentResolver()
                    .update(toUpdate, cv, null, null);
            return true;
        }

        return false;
    }

    private String getFormFilePath(String formId) throws Exception {
        String[] selectionArgs = { formId };
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] fields = { FormsProviderAPI.FormsColumns.FORM_FILE_PATH };

        Cursor formCursor = null;
        try {
            formCursor = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, fields, selection, selectionArgs, null);
            if ( formCursor.getCount() == 0 )
                throw new Exception("No form with id = " + formId);

            formCursor.moveToFirst();
            int idxFormFilePath = formCursor.getColumnIndex(fields[0]);
            if ( formCursor.isNull(idxFormFilePath) )
                throw new Exception("Form file path in null for form with id = " + formId);

            return formCursor.getString(idxFormFilePath);
        } finally {
            if (formCursor != null) {
                formCursor.close();
            }
        }
    }
}
