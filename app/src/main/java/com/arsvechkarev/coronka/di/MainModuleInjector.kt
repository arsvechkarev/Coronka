package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import base.Navigator
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainNavigator

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
}