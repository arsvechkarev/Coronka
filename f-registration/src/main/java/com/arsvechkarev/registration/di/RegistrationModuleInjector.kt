package com.arsvechkarev.registration.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.registration.presentation.RegistrationViewModel
import core.auth.AuthEmailSaver
import core.auth.Authenticator
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver

object RegistrationModuleInjector {
  
  fun provideViewModel(fragment: RegistrationFragment): RegistrationViewModel {
    val saver = SharedPrefsAuthEmailSaver(fragment.requireContext())
    val factory = registrationViewModelFactory(FirebaseAuthenticator, saver)
    return ViewModelProviders.of(fragment, factory).get(RegistrationViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun registrationViewModelFactory(
    authenticator: Authenticator,
    saver: AuthEmailSaver
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = RegistrationViewModel(authenticator, saver)
      return viewModel as T
    }
  }
}