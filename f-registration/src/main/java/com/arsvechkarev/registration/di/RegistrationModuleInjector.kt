package com.arsvechkarev.registration.di

import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.registration.presentation.RegistrationViewModel
import com.arsvechkarev.registration.presentation.RegistrationViewModel.Companion.TIMER_FILENAME
import com.arsvechkarev.storage.SharedPrefsStorage
import core.auth.FirebaseAuthenticator
import core.auth.SharedPrefsAuthEmailSaver
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel

object RegistrationModuleInjector {
  
  fun provideViewModel(fragment: RegistrationFragment): RegistrationViewModel {
    val context = fragment.requireContext()
    val emailSaver = SharedPrefsAuthEmailSaver(context)
    val timerSaver = SharedPrefsStorage(TIMER_FILENAME, context)
    return fragment.createViewModel(
      FirebaseAuthenticator,
      emailSaver,
      timerSaver,
      AndroidSchedulers
    )
  }
}