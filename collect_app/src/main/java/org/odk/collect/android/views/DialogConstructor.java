package org.odk.collect.android.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.odk.collect.android.R;

public class DialogConstructor {
    public static final int DIALOG_SINGLE_ANSWER = 1;
    public static final int DIALOG_MULTI_ANSWER = 2;

    private Dialog mDialog = null;
    private TextView mTitleView = null;
    private TextView mMessageView = null;
    private Button mEventButton = null;
    private NotificationListener mListener = null;

    public interface NotificationListener {
        void onPositiveClick();
        void onNegativeClick();
    }

    public DialogConstructor(Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.alert_spinner_dialog, null, false);

        mTitleView = (TextView) contentView.findViewById(R.id.title);
        mMessageView = (TextView) contentView.findViewById(R.id.message);
        mListener = (NotificationListener) activity;

        mEventButton = (Button) contentView.findViewById(R.id.cancel);
        mEventButton.setText(activity.getString(R.string.cancel));
        mEventButton.setVisibility(View.GONE);
        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onNegativeClick();

                stopAnimation();
            }
        });

        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(contentView);
    }

    public DialogConstructor(Activity activity, int type) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.alert_message_dialog, null, false);

        mTitleView = (TextView) contentView.findViewById(R.id.title);
        mMessageView = (TextView) contentView.findViewById(R.id.message);
        mListener = (NotificationListener) activity;

        Button ok = (Button) contentView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onPositiveClick();

                stopAnimation();
            }
        });

        Button no = (Button) contentView.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onNegativeClick();

                stopAnimation();
            }
        });

        if (type == DIALOG_SINGLE_ANSWER)
            no.setVisibility(View.GONE);

        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(contentView);
    }

    public void setButtonText(String text) {
        if (mEventButton != null)
            mEventButton.setText(text);
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
