package com.arsvechkarev.coronka.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.coronka.MainNavigator
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.presentation.MainViewModel
import core.auth.AuthEmailSaver
import core.auth.Authenticator
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver

object MainModuleInjector {
  
  fun provideNavigator(activity: MainActivity): MainNavigator {
    return MainNavigator(activity.supportFragmentManager,
      activity.findViewById(R.id.drawerLayout))
  }
  
  fun provideViewModel(activity: MainActivity): MainViewModel {
    val saver = SharedPrefsAuthEmailSaver(activity)
    val factory = mainViewModelFactory(FirebaseAuthenticator, saver)
    return ViewModelProviders.of(activity, factory).get(MainViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mainViewModelFactory(
    authenticator: Authenticator,
    saver: AuthEmailSaver
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MainViewModel(authenticator, saver)
      return viewModel as T
    }
  }
}