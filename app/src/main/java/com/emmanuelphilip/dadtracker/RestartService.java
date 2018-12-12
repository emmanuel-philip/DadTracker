package com.emmanuelphilip.dadtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class RestartService extends BroadcastReceiver {

    private static final String TAG = MainActivity.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        SharedPreferences pref = context.getSharedPreferences("DadTrackerPref", 0); // 0 - for private mode
        boolean preventRestart = pref.getBoolean("preventRestart",  false);
        if(!preventRestart){
            context.startService(new Intent(context, LocationService.class));
        }
        else{
            Log.e(TAG, "Restart Prevented");
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("preventRestart");
        }
    }


}
