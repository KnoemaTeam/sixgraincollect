package org.graindataterminal.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.odk.collect.android.R;

public class LoadingIndicator {
    private TextView title = null;
    private TextView message = null;

    protected Dialog indicator = null;
    protected Activity activity = null;
    protected LayoutInflater inflater = null;

    protected boolean isVisible = false;

    public LoadingIndicator (Activity activity) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.base_loading_indicator, null);
        this.title = (TextView) view.findViewById(R.id.indicatorTitle);
        this.message = (TextView) view.findViewById(R.id.indicatorMessage);

        this.indicator = new Dialog(activity);
        this.indicator.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.indicator.setCanceledOnTouchOutside(false);
        this.indicator.setContentView(view);
    }

    public void startAnimation () {
        if (isVisible)
            return;

        isVisible = true;
        indicator.show();
    }

    public void updateDialog (String title, String message) {
        if (!isVisible) {
            startAnimation();
        }

        this.title.setText(title);
        this.message.setText(message);
    }

    public void stopAnimation () {
        if (isVisible) {
            isVisible = false;

            indicator.dismiss();
        }
    }
}
