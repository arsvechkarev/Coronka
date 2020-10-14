package core.extenstions

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import core.Application

val isOrientationPortrait: Boolean
  get() = Application.applicationContext.resources.configuration.orientation ==
      Configuration.ORIENTATION_PORTRAIT

val Context.connectivityManager: ConnectivityManager
  get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager