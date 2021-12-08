package com.um.feri.cs.pora.kotlinnotification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d(" WELCOME in MyNotificationReceiver")
        intent.action?.let { act ->
            Timber.d(" MyNotificationReceiver $act ")
            val ns = AppCompatActivity.NOTIFICATION_SERVICE
            val nMgr = context.getSystemService(ns) as NotificationManager
            nMgr.cancelAll()
        }
    }
}
