package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import com.arsvechkarev.coronka.MainNavigator
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainViewModel
import core.ConnectivityObserver
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver
import core.concurrency.AndroidSchedulers
import core.extenstions.connectivityManager
import core.extenstions.createViewModel
import core.navigation.Navigator

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
  
  fun provideViewModel(activity: MainActivity): MainViewModel {
    val saver = SharedPrefsAuthEmailSaver(activity)
    return activity.createViewModel(FirebaseAuthenticator, saver, AndroidSchedulers)
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