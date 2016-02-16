package org.graindataterminal.helpers;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.R;
import org.graindataterminal.adapters.NoticeDialogListener;
import org.odk.collect.android.application.Collect;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler, NoticeDialogListener {
    private final FragmentActivity context;
    private Throwable exception = null;

    public ExceptionHandler(FragmentActivity context) {
        this.context = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        this.exception = exception;
        this.exception.printStackTrace();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private static String getDetails(Throwable throwable, Integer errorCode) {
        Context context = Collect.getInstance().getContext();
        String errorMessage = "APP_ID=" + BuildConfig.APPLICATION_ID + "\n";
        errorMessage += "APP_NAME=" + context.getString(context.getApplicationInfo().labelRes) + "\n";
        errorMessage += "APP_VERSION_CODE=" + BuildConfig.VERSION_CODE + "\n";
        errorMessage += "APP_VERSION_NAME=" + BuildConfig.VERSION_NAME + "\n";
        errorMessage += "ANDROID_SDK=" + Build.VERSION.SDK_INT + "\n";
        errorMessage += "ANDROID_VERSION=" + Build.VERSION.RELEASE + "\n";
        errorMessage += "DEVICE_BRAND=" + Build.BRAND + "\n";
        errorMessage += "DEVICE_MODEL=" + Build.MODEL + "\n";
        errorMessage += "ERROR_CODE=" + (errorCode != null ? errorCode : "") + "\n";
        errorMessage += "STACK_TRACE=" + getStackTrace(throwable) + "\n";

        return errorMessage;
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

    @Override
    public void onDialogPositiveClick(String tag) {
        Helper.sendEmail(context,
                Collect.getInstance().getContext().getString(R.string.action_send_mail_title_error),
                Collect.getInstance().getContext().getString(R.string.action_send_mail_message),
                getDetails(exception, null),
                Helper.MAIL_TYPE_ERROR_INFO);
    }

    @Override
    public void onDialogNegativeClick(String tag) {

    }
}
