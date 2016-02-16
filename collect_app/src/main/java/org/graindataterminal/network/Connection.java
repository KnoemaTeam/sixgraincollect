package org.graindataterminal.network;

import android.content.Context;
import android.net.ConnectivityManager;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

public class Connection {
    public final static String SECRET_KEY = "secret=_onUw2zamg1Yx0lma8OrDzzmb5d06s4BL1sjJ4euWm3UUE";
    public final static String BASE_URL = "http://6grain.com/api/farmSurvey";
    public final static String CHANNELS_URL = "http://6grain.s3.amazonaws.com/dataterminal/channels.txt";
    public final static String VERSION_URL = "http://6grain.s3.amazonaws.com/dataterminal/version.txt";
    public final static String UPDATE_URL = "http://6grain.s3.amazonaws.com/dataterminal/com.sixgrain.dataterminal.apk";

    private static Connection instance = null;
    private ConnectivityManager cm = null;

    public Connection() {
        cm = (ConnectivityManager) Collect.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static Connection getInstance() {
        if (instance == null)
            instance = new Connection();

        return instance;
    }

    public boolean isConnectionAvailable() {
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public String getNetworkType() {
        Context context = Collect.getInstance().getContext();
        int type = cm.getActiveNetworkInfo().getType();

        if (type == ConnectivityManager.TYPE_MOBILE) {
            context.getString(R.string.network_type_mobile);
        }
        else if (type == ConnectivityManager.TYPE_ETHERNET) {
            context.getString(R.string.network_type_ethernet);
        }
        else if (type == ConnectivityManager.TYPE_WIFI) {
            context.getString(R.string.network_type_wifi);
        }
        else if (type == ConnectivityManager.TYPE_WIMAX) {
            context.getString(R.string.network_type_wimax);
        }

        return context.getString(R.string.network_type_undefined);
    }
}
