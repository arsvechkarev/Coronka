package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import com.arsvechkarev.coronka.MainNavigator
import com.arsvechkarev.coronka.presentation.MainActivity
import core.ConnectivityObserver
import core.Navigator
import core.extenstions.connectivityManager

object MainModuleInjector {
  
  fun provideNavigator(
    activity: MainActivity,
    drawerLayoutTag: String,
    onFragmentAppeared: (Fragment) -> Unit,
  ): Navigator {
    return MainNavigator(
      activity.supportFragmentManager,
      activity.window.decorView.findViewWithTag(drawerLayoutTag),
      onFragmentAppeared
    )
  }
  
  fun provideConnectivityObserver(
    activity: MainActivity,
    navigator: Navigator,
  ): ConnectivityObserver {
    return ConnectivityObserver(
      activity.connectivityManager,
      onNetworkAvailable = {
        navigator.currentFragment?.onNetworkAvailable()
      }
    )
  }
}