package com.daeseong.gameservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartService extends BroadcastReceiver {

    private static final String TAG = RestartService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive:" + intent.getAction().toString());

        if(intent.getAction().equals("ACTION.RestartService")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent service = new Intent(context, ForegroundService.class);
                context.startForegroundService(service);
            } else {
                Intent service = new Intent(context, GameService.class);
                context.startService(service);
            }

        } else  if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent service = new Intent(context, ForegroundService.class);
                context.startForegroundService(service);
            } else {
                Intent service = new Intent(context, GameService.class);
                context.startService(service);
            }
        }
    }

}