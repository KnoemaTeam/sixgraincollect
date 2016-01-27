package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.odk.collect.android.R;

public class DialogConstructor {
    public static final int DIALOG_SPINNER = 1;
    public static final int DIALOG_MESSAGE = 2;

    private Dialog mDialog = null;
    private TextView mTitleView = null;
    private TextView mMessageView = null;

    public DialogConstructor(Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.alert_spinner_dialog, null, false);

        mTitleView = (TextView) contentView.findViewById(R.id.title);
        mMessageView = (TextView) contentView.findViewById(R.id.message);

        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(contentView);
    }

    public void startAnimation() {
        if (!mDialog.isShowing())
            mDialog.show();
    }

    public void stopAnimation() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    public void updateDialog (String title, String message) {
        startAnimation();

        mTitleView.setText(title);
        mMessageView.setText(message);
    }
}
