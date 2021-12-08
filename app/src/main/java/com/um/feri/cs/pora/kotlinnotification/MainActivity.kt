package com.um.feri.cs.pora.kotlinnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.um.feri.cs.pora.kotlinnotification.databinding.ActivityMainBinding
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        val CHANNEL_ID = "com.um.feri.cs.pora.kotlinnotification" //my channel id
        val TIME_ID = "TIME_ID"
        val MY_ACTION_FILTER = "com.um.feri.cs.pora.kotlinnotification.open"
        val VOTING_KEY = "com.um.feri.cs.pora.kotlinnotification.vote"
        val VOTING_ANSW_YES = "YES"
        val VOTING_ANSW_NO = "NO"
        private var notificationId = 0
        fun getNotificationUniqueID(): Int {
            return notificationId++;
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) //Init report type
        }
        createNotificationChannel() //init channel
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            var currentDateTime = LocalDateTime.now()
            var time = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            createNotifyWithIntent(
                "My title Notification with Intent",
                "Something is working on ${time.toString()}",
                time.toString(),
                R.drawable.ic_not_stat
            )
        }
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyTestChannel"
            val descriptionText = "Testing notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MainActivity.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun createNotify(title: String, content: String, imageId: Int) {
        val myTimeIntent = Intent(this, MyNotificationReceiver::class.java).apply {
            action = MY_ACTION_FILTER
            putExtra(TIME_ID, "TODO SOME VAR")
        }
        Timber.d("createNotifyWithIntent")
        val myPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                this,
                getNotificationUniqueID(),
                myTimeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(imageId)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(myPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MainActivity.getNotificationUniqueID(), builder.build())
    }

    fun createNotifyWithIntent(title: String, content: String, time: String, imageId: Int) {
        val myTimeIntent = Intent(this, MyNotificationReceiver::class.java).apply {
            action = MY_ACTION_FILTER
            putExtra(TIME_ID, time)
        }
        Timber.d("createNotifyWithIntent")
        val myPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                this,
                getNotificationUniqueID(),
                myTimeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val inYes = Intent(this, MainActivity::class.java)
        inYes.putExtra(VOTING_KEY, VOTING_ANSW_YES)
        inYes.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntentYes =
            PendingIntent.getActivity(
                this,
                getNotificationUniqueID(),
                inYes,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val inNo = Intent(this, MainActivity::class.java)
        inNo.putExtra(VOTING_KEY, VOTING_ANSW_NO)
        inNo.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntentNo =
            PendingIntent.getActivity(
                this,
                getNotificationUniqueID(),
                inNo,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(imageId)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            //.setContentIntent(myPendingIntent)
            .addAction(
                R.drawable.ic_action_alarm, "Open",
                myPendingIntent
            )
            .addAction(
                R.drawable.ic_action_yes, "Yes",
                pendingIntentYes
            )
            .addAction(
                R.drawable.ic_action_no, "No",
                pendingIntentNo
            )

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MainActivity.getNotificationUniqueID(), builder.build())
        Timber.d("Exec new Notification")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(MY_ACTION_FILTER)
        registerReceiver(br, filter)

    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(br)
    }

    val br: BroadcastReceiver = MyNotificationReceiver()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply {
            if (extras?.getString(VOTING_KEY) == VOTING_ANSW_YES) {
                Timber.d("YES")
            } else
                if (extras?.getString(VOTING_KEY) == VOTING_ANSW_NO) {
                    Timber.d("NO")
                }
            cancelAllNotification()
        }


    }

    private fun cancelAllNotification() {
        val ns = NOTIFICATION_SERVICE
        val nMgr = applicationContext.getSystemService(ns) as NotificationManager
        nMgr.cancelAll()
    }

}