package org.graindataterminal.views.system;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.odk.collect.android.R;

public class SpinnerIndicator extends Dialog {

    private SpinnerIndicator(Context context) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        this.setContentView(R.layout.base_spinner_indicator);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.color_spinner_background)));
    }

    public static SpinnerIndicator getInstance (Context context) {
        return new SpinnerIndicator(context);
    }
}
