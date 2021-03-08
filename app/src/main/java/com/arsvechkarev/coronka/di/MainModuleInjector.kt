package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainNavigator
import core.Navigator

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