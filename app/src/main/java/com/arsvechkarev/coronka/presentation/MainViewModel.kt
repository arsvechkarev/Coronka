package com.arsvechkarev.coronka.presentation

import android.content.Intent
import android.os.Bundle
import core.RxViewModel
import core.auth.AuthEmailSaver
import core.auth.Authenticator
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import timber.log.Timber

class MainViewModel(
  private val authenticator: Authenticator,
  private val emailSaver: AuthEmailSaver,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  fun figureOutScreenToGo(intent: Intent, savedInstanceState: Bundle?) {
    val emailLink = intent.data.toString()
    if (authenticator.isSignInWithEmailLink(emailLink)) {
      handleSignInWithEmailLink(emailLink)
    } else {
      if (savedInstanceState == null) {
        if (authenticator.isUserLoggedIn()) {
          _state.value = GoToMainScreen
        } else {
          _state.value = GoToRegistrationScreen
        }
      }
    }
  }
  
  private fun handleSignInWithEmailLink(emailLink: String) {
    val email = emailSaver.getEmail()
    _state.value = ShowEmailLinkLoading
    rxCall {
      authenticator.signInWithEmailLink(email, emailLink)
          .subscribeOn(schedulers.io())
          .observeOn(schedulers.mainThread())
          .subscribe { result ->
            val receivedEmail = result.user!!.email
            val message = "Successfully signed in as: $receivedEmail"
            Timber.tag("Registration").d(message)
            _state.value = SuccessfullySignedId
          }
    }
  }
}
