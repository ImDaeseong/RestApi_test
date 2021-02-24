package com.daeseong.gameservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class RestartService : BroadcastReceiver() {

    private val tag = RestartService::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(tag, "onReceive:" + intent.action.toString())

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val service = Intent(context, GameService::class.java)
                context.startForegroundService(service)
            } else {
                val service = Intent(context, GameService::class.java)
                context.startService(service)
            }

        } catch (ex: Exception) {
            Log.e(tag, ex.message.toString())
        }
    }

}