package com.arsvechkarev.registration.di

import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.registration.presentation.RegistrationViewModel
import com.arsvechkarev.storage.SharedPrefsStorage
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel

object RegistrationModuleInjector {
  
  fun provideViewModel(fragment: RegistrationFragment): RegistrationViewModel {
    val emailSaver = SharedPrefsAuthEmailSaver(fragment.requireContext())
    val timerSaver = SharedPrefsStorage(RegistrationViewModel.TIMER_FILENAME,
      fragment.requireContext())
    return fragment.createViewModel(
      FirebaseAuthenticator,
      emailSaver,
      timerSaver,
      AndroidSchedulers
    )
  }
}