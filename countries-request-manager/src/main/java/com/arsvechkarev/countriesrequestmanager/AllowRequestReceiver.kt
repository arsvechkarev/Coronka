package com.arsvechkarev.countriesrequestmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.SystemClock
import core.log.Loggable
import core.log.debug

class AllowRequestReceiver : BroadcastReceiver(), Loggable {
  
  override val tag = "RequestReceiver"
  
  override fun onReceive(context: Context, intent: Intent) {
    debug { "received: time = ${SystemClock.elapsedRealtime()}" }
    val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    val requestAllowed = sharedPrefs.getBoolean(REQUEST_ALLOWED, true)
    debug { "received: requestAllowed = $requestAllowed" }
    if (!requestAllowed) {
      sharedPrefs.edit().putBoolean(REQUEST_ALLOWED, true).apply()
    }
  }
  
}