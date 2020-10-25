package com.arsvechkarev.coronka.di

import com.arsvechkarev.coronka.MainNavigator
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainViewModel
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel

object MainModuleInjector {
  
  fun provideNavigator(activity: MainActivity, drawerLayoutTag: String): MainNavigator {
    return MainNavigator(activity.supportFragmentManager,
      activity.window.decorView.findViewWithTag(drawerLayoutTag))
  }
  
  fun provideViewModel(activity: MainActivity): MainViewModel {
    return activity.createViewModel(
      FirebaseAuthenticator,
      SharedPrefsAuthEmailSaver(activity),
      AndroidSchedulers
    )
  }
}