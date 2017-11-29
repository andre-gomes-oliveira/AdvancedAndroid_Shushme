package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(GeofenceBroadcastReceiver.class.getSimpleName(), "Received... something");
    }
}
