package com.arsvechkarev.countriesrequestmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import core.log.Loggable
import core.log.debug

object CountriesRequestManager : Loggable {
  
  override val tag = "RequestManager"
  
  private lateinit var context: Context
  private lateinit var sharedPrefs: SharedPreferences
  
  fun init(context: Context) {
    debug { "initialized" }
    this.context = context
    sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    val notScheduled = sharedPrefs.getBoolean(ALARM_NOT_SCHEDULED, true)
    debug { "not scheduled = $notScheduled" }
    if (notScheduled) {
      scheduleAlarm()
      sharedPrefs.edit().putBoolean(ALARM_NOT_SCHEDULED, false).apply()
    }
  }
  
  fun isRequestAllowed(): Boolean {
    return sharedPrefs.getBoolean(REQUEST_ALLOWED, true)
  }
  
  fun disallowRequest() {
    sharedPrefs.edit().putBoolean(REQUEST_ALLOWED, false).apply()
  }
  
  private fun scheduleAlarm() {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AllowRequestReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
      AlarmManager.INTERVAL_HOUR, pendingIntent)
  }
}