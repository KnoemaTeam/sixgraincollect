package org.graindataterminal.controllers;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

public class DeviceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        setContentList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_synchronize).setVisible(false);

        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_device;
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_app_logo);
        }
    }

    protected void setContentList () {
        Context context = Collect.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        TextView appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText(MyApp.getAppVersionName());

        TextView deviceId = (TextView) findViewById(R.id.deviceId);
        deviceId.setText(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        TextView imei = (TextView) findViewById(R.id.imei);
        imei.setText(telephonyManager.getDeviceId());

        TextView subscriberId = (TextView) findViewById(R.id.subscriberId);
        subscriberId.setText(telephonyManager.getSubscriberId());

        TextView simSerialNumber = (TextView) findViewById(R.id.simSerialNumber);
        simSerialNumber.setText(telephonyManager.getSimSerialNumber());

        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        phoneNumber.setText(telephonyManager.getLine1Number());
    }
}
