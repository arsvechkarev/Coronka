package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainNavigator

object MainModuleInjector {
  
  fun provideNavigator(
    activity: MainActivity,
    drawerLayoutTag: String,
    onGoToMainFragment: () -> Unit,
    onFragmentAppeared: (Fragment) -> Unit,
  ): MainNavigator {
    val mainNavigator = MainNavigator(
      activity.supportFragmentManager,
      activity.window.decorView.findViewWithTag(drawerLayoutTag),
      onGoToMainFragment,
      onFragmentAppeared
    )
    activity.lifecycle.addObserver(mainNavigator)
    return mainNavigator
  }
}