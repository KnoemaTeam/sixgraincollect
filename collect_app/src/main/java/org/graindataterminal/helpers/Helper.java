package org.graindataterminal.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.graindataterminal.controllers.MyApp;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

public class Helper {
    public final static String SELECTED_SURVEY_PAGE = "SELECTED_SURVEY_PAGE";

    public static final String PHOTO_TYPE_FARMER = "FARMER";
    public static final String PHOTO_TYPE_FIELD = "FIELD";
    public static final String PHOTO_TYPE_CROP = "CROP";

    public static final int MAIL_TYPE_SURVEY_INFO = 1;
    public static final int MAIL_TYPE_ERROR_INFO = 2;

    public static final int FARMER_PHOTO_REQUEST_CODE = 1;
    public static final int FIELD_PHOTO_REQUEST_CODE = 2;
    public static final int CROP_PHOTO_REQUEST_CODE = 3;

    public static final String DELETE_MESSAGE = "DELETE_MESSAGE";
    public static final String CLOSE_MESSAGE = "CLOSE_MESSAGE";

    public static final int ADD_FARMER_REQUEST_CODE = 10;
    public static final int EDIT_FARMER_REQUEST_CODE = 11;

    public static final int ADD_FIELD_REQUEST_CODE = 20;
    public static final int EDIT_FIELD_REQUEST_CODE = 21;

    public static final int ADD_ITEM_REQUEST_CODE = 30;
    public static final int EDIT_ITEM_REQUEST_CODE = 31;

    private static File zipFile = null;

    public static File getOutputMediaFile (String id, String prefix, String extension, String type) throws IOException{
        File mediaStorageDir;
        File mediaFile;

        String dirName = "6G Data Terminal";
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dirName);
        }
        else {
            ContextWrapper cw = new ContextWrapper(Collect.getContext());
            mediaStorageDir = cw.getDir(dirName ,Context.MODE_PRIVATE);
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        if (TextUtils.isEmpty(id))
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + prefix + "_" + type + extension);
        else
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + prefix + "_" + id + "_" + type + extension);

        return mediaFile;
    }

    public static String getUpdateMediaFilePath(String fileName) throws IOException {
        File mediaStorageDir;

        String dirName = "6G Data Terminal" + File.separator + "updates";
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dirName);
        }
        else {
            ContextWrapper cw = new ContextWrapper(Collect.getContext());
            mediaStorageDir =  new File(cw.getFilesDir(), dirName);
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return mediaStorageDir.getPath() +  File.separator + fileName;
    }

    public static String readFromFile(String filePath) {
        String dataString = null;
        String dataLine;

        try {
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);

            while ((dataLine = bufferedReader.readLine()) != null)
                dataString += dataLine;

            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return dataString;
    }

    public static void setImage(ImageView imageView, String photoPath) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        if (targetW == 0) {
            targetW = targetH = 120;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public static String getDate() {
        return getDate("yyyy-MM-dd'T'HH:mm:ssZZ");
    }

    public static String getDate (String dateFormat) {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(currentDate);

        Date time = calendar.getTime();
        SimpleDateFormat timeWithFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        timeWithFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return timeWithFormat.format(time);
    }

    public static String getDate (String dateFormat, String dateString) {
        try {
            SimpleDateFormat timeSourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.getDefault());
            timeSourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            SimpleDateFormat timeRequiredFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
            timeRequiredFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date = timeSourceFormat.parse(dateString);

            return timeRequiredFormat.format(date);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static String getDate(String sourceFormat, String requiredFormat, String dateString) {
        try {
            SimpleDateFormat timeSourceFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
            timeSourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            SimpleDateFormat timeRequiredFormat = new SimpleDateFormat(requiredFormat, Locale.getDefault());
            timeRequiredFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date = timeSourceFormat.parse(dateString);

            return timeRequiredFormat.format(date);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static String compareDate (String date) {
        try {
            Date currentDate = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.setTime(currentDate);

            SimpleDateFormat timeWithFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            timeWithFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date currentTime = calendar.getTime();
            Date compareDate = timeWithFormat.parse(date);

            if (currentTime.before(compareDate)) {
                return timeWithFormat.format(currentTime);
            }
            else {
                return timeWithFormat.format(compareDate);
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void showView(final View view) {
        if (view.getVisibility() == View.VISIBLE)
            return;

        view.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(Collect.getContext(), R.anim.show_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public static void fadeView(final View view) {
        if (view.getVisibility() == View.GONE)
            return;

        Animation animation = AnimationUtils.loadAnimation(Collect.getContext(), R.anim.fade_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                view.setEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public static void sendEmail(Activity activity, String subject, String message, String body, int type) {
        String[] TO = {"svyatkin@knoema.com"};
        String[] CC = {""};

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("vnd.android.cursor.dir/email");
            intent.putExtra(Intent.EXTRA_EMAIL, TO);
            intent.putExtra(Intent.EXTRA_CC, CC);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);

            if (body != null) {
                createZipFile(body, type);

                if (type == MAIL_TYPE_SURVEY_INFO)
                    zipFile = getOutputMediaFile(null, "SURVEY", ".gzip", "REPORT");
                else
                    zipFile = getOutputMediaFile(null, "ERROR", ".gzip", "REPORT");

                if (zipFile != null)
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipFile));
            }

            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.action_send_mail)));
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(activity, activity.getString(R.string.action_send_mail_exception), Toast.LENGTH_SHORT).show();
        }
    }

    protected static void createZipFile(String source, int type) {
        try {
            File fileOutput;

            if (type == MAIL_TYPE_SURVEY_INFO)
                fileOutput = getOutputMediaFile(null, "SURVEY", ".gzip", "REPORT");
            else
                fileOutput = getOutputMediaFile(null, "ERROR", ".gzip", "REPORT");

            if (fileOutput != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(fileOutput.getAbsolutePath());
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);

                gzipOutputStream.write(source.getBytes());
                gzipOutputStream.close();

                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
