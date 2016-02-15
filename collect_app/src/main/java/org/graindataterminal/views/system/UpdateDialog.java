package org.graindataterminal.views.system;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.odk.collect.android.R;

public class UpdateDialog extends DialogFragment {
    private static final String DIALOG_TITLE_KEY = "DIALOG_TITLE";
    private ProgressBar progressBar = null;
    private TextView progressValue = null;

    public static UpdateDialog getInstance (String title) {
        UpdateDialog dialog = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_TITLE_KEY, title);

        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            String title = bundle.getString(DIALOG_TITLE_KEY);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.base_update_dialog, null);

            TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(title);

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.setProgress(0);

            progressValue = (TextView) view.findViewById(R.id.progressValue);
            progressValue.setText("0%");

            builder.setView(view);
            return builder.create();
        }

        return super.onCreateDialog(savedInstanceState);
    }

    public void updateValue(int value) {
        if (progressBar != null)
            progressBar.setProgress(value);

        if (progressValue != null) {
            String percent = String.format(getString(R.string.update_message_progress_value), value);
            progressValue.setText(percent);
        }
    }
}
