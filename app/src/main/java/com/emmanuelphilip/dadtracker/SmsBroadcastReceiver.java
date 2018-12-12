package com.emmanuelphilip.dadtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = SmsBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        Bundle intentExtras = intent.getExtras();
        if(intentExtras!=null){
            Object[] sms = (Object[]) intentExtras.get("pdus");
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
                if(smsBody.equalsIgnoreCase("start trace")){
                    Toast.makeText(context, "Trace Started", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(context, LocationService.class);
                    intent1.putExtra("PhoneNumber", address);
                    context.startService(intent1);
                }
            }

        }
    }
}
