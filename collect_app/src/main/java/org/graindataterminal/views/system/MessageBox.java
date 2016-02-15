package org.graindataterminal.views.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.graindataterminal.adapters.NoticeDialogListener;

public class MessageBox extends DialogFragment {
    public static final String DIALOG_TYPE_KEY = "DIALOG_TYPE";
    public static final String DIALOG_TITLE_KEY = "DIALOG_TITLE";
    public static final String DIALOG_MESSAGE_KEY = "DIALOG_MESSAGE";
    public static final String DIALOG_TAG_KEY = "DIALOG_TAG";

    public static final int DIALOG_TYPE_MESSAGE = 1;
    public static final int DIALOG_TYPE_CHOICE = 2;

    protected NoticeDialogListener noticeDialogListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            noticeDialogListener = (NoticeDialogListener) getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.base_message_dialog, null, false);

            int type = bundle.getInt(DIALOG_TYPE_KEY, DIALOG_TYPE_MESSAGE);
            String title = bundle.getString(DIALOG_TITLE_KEY);
            String message = bundle.getString(DIALOG_MESSAGE_KEY);
            final String tag = bundle.getString(DIALOG_TAG_KEY);

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView messageView = (TextView) view.findViewById(R.id.message);

            titleView.setText(title);
            messageView.setText(message);

            if (type == DIALOG_TYPE_MESSAGE) {
                Button noAnswer = (Button) view.findViewById(R.id.no);
                noAnswer.setVisibility(View.GONE);

                Button yesAnswer = (Button) view.findViewById(R.id.yes);
                yesAnswer.setText("OK");
                yesAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (noticeDialogListener != null)
                            noticeDialogListener.onDialogPositiveClick(tag);

                        dismiss();
                    }
                });
            } else {
                Button noAnswer = (Button) view.findViewById(R.id.no);
                noAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (noticeDialogListener != null)
                            noticeDialogListener.onDialogNegativeClick(tag);

                        dismiss();
                    }
                });

                Button yesAnswer = (Button) view.findViewById(R.id.yes);
                yesAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (noticeDialogListener != null)
                            noticeDialogListener.onDialogPositiveClick(tag);

                        dismiss();
                    }
                });
            }

            builder.setView(view);
            return builder.create();
        }

        return super.onCreateDialog(savedInstanceState);
    }
}
