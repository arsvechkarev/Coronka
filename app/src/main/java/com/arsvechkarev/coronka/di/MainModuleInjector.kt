package com.arsvechkarev.coronka.di

import androidx.fragment.app.Fragment
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainNavigator

object MainModuleInjector {
  
  fun provideNavigator(
    activity: MainActivity,
    onGoToMainFragment: () -> Unit,
    onFragmentAppeared: (Fragment) -> Unit,
  ): MainNavigator {
    val mainNavigator = MainNavigator(
      activity.supportFragmentManager,
      onGoToMainFragment,
      onFragmentAppeared
    )
    activity.lifecycle.addObserver(mainNavigator)
    return mainNavigator
  }
}