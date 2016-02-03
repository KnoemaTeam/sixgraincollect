package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferencesActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int mSplashTimeout = 2000; // milliseconds
    private static final boolean EXIT = true;

    private int mImageMaxWidth;
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // must be at the beginning of any activity that can be called from an external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), EXIT);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, FormChooserList.class);
                startActivity(intent);
                finish();
            }
        }, 2 * 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void endSplashScreen() {
        // launch new activity and close splash screen
        // Maybe need implement on another way
        startActivity(new Intent(SplashScreenActivity.this, MainMenuActivity.class));
        finish();
    }


    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int scale = 1;
            if (o.outHeight > mImageMaxWidth || o.outWidth > mImageMaxWidth) {
                scale =
                    (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(mImageMaxWidth
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;
    }


    private void startSplashScreen(String path) {
        // add items to the splash screen here. makes things less distracting.
        ImageView iv = (ImageView) findViewById(R.id.splash);
        LinearLayout ll = (LinearLayout) findViewById(R.id.splash_default);

        File f = new File(path);
        if (f.exists()) {
            iv.setImageBitmap(decodeFile(f));
            ll.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);
        }

        // create a thread that counts up to the timeout
        Thread t = new Thread() {
            int count = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    while (count < mSplashTimeout) {
                        sleep(100);
                        count += 100;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endSplashScreen();
                }
            }
        };
        t.start();
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
                	    Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "OK");
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

    @Override
    protected void onStart() {
    	super.onStart();
		Collect.getInstance().getActivityLogger().logOnStart(this);
    }

    @Override
    protected void onStop() {
		Collect.getInstance().getActivityLogger().logOnStop(this);
    	super.onStop();
    }
}
