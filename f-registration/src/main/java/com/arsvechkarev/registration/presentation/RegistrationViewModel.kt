package com.arsvechkarev.registration.presentation

import core.Failure
import core.Loading
import core.RxViewModel
import core.auth.AuthEmailSaver
import core.auth.AuthSettings
import core.auth.Authenticator
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers

class RegistrationViewModel(
  private val authenticator: Authenticator,
  private val emailSaver: AuthEmailSaver,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  fun sendEmailLink(email: String) {
    _state.value = Loading()
    rxCall {
      authenticator.sendSignInLinkToEmail(email, AuthSettings)
          .subscribeOn(schedulers.io())
          .observeOn(schedulers.mainThread())
          .subscribe({
            emailSaver.saveEmail(email)
            _state.value = EmailLinkSent
          },
            { e -> _state.value = Failure(e) })
    }
  }
}