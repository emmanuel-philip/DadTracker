package com.emmanuelphilip.dadtracker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SmsService extends IntentService {

    public SmsService() {
        super("SmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate for 400 milliseconds
                v.vibrate(10000);
                final String action = intent.getAction();
                String message = intent.getStringExtra("smsData");
                Toast.makeText(this,message, Toast.LENGTH_LONG).show();
                String recipient = intent.getStringExtra("smsRecipient");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("phoneNumber", null, message, null, null);
                String test = "";

            }
            catch (Exception ex){

            }
        }
    }

}
