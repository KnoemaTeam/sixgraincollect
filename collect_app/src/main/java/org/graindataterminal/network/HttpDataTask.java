package org.graindataterminal.network;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.odk.collect.android.R;
import org.graindataterminal.controllers.MyApp;
import org.graindataterminal.helpers.Helper;
import org.graindataterminal.views.system.UpdateDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpDataTask extends HttpBaseTask {
    private final static int BUFFER_SIZE = 1024;
    private UpdateDialog updateDialog = null;
    private Activity activity = null;

    public HttpDataTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        updateDialog = UpdateDialog.getInstance(activity.getString(R.string.update_message_progress_title));
        updateDialog.show(activity.getFragmentManager(), activity.getString(R.string.update_message));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        updateDialog.updateValue(values[0]);
    }

    @Override
    protected String doInBackground(String... params) {
        String filePath = null;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = getConnection(url);

            try {
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.connect();

                responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String disposition = connection.getHeaderField("Content-Disposition");
                    String contentType = connection.getContentType();
                    int contentLength = connection.getContentLength();

                    if (disposition != null) {
                        int index = disposition.indexOf("filename=");
                        if (index > 0) {
                            String fileName = disposition.substring(index + 10, disposition.length() - 1);
                            filePath = Helper.getUpdateMediaFilePath(fileName);
                        }
                    }
                    else {
                        filePath = Helper.getUpdateMediaFilePath(params[1]);
                    }

                    System.out.println("Content-Type = " + contentType);
                    System.out.println("Content-Disposition = " + disposition);
                    System.out.println("Content-Length = " + contentLength);
                    System.out.println("fileName = " + filePath);

                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);

                        if (file.exists()) {
                            if (file.delete()) {
                                System.out.println("Deleted file: " + file.getAbsolutePath());
                            }
                        }

                        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                        OutputStream outputStream = new FileOutputStream(filePath);

                        byte[] buffer = new byte[BUFFER_SIZE];
                        int totalLength = 0;
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            totalLength += bytesRead;
                            outputStream.write(buffer, 0, bytesRead);

                            System.out.println("Downloaded: " + (double) ((totalLength * 100) / contentLength));
                            publishProgress(((totalLength * 100) / contentLength));
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();
                    }
                }
                else {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                connection.disconnect();

                if (updateDialog != null)
                    updateDialog.dismiss();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return filePath;
    }
}
