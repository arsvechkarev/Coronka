package com.arsvechkarev.coronka.presentation

import android.content.Intent
import com.google.firebase.auth.FirebaseAuthActionCodeException
import core.RxViewModel
import core.auth.AuthEmailSaver
import core.auth.Authenticator
import core.concurrency.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainViewModel(
  private val authenticator: Authenticator,
  private val emailSaver: AuthEmailSaver,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  fun figureOutScreenToGo(intent: Intent) {
    val emailLink = intent.data.toString()
    when {
      authenticator.isUserLoggedIn() -> _state.value = GoToMainScreen
      authenticator.isSignInWithEmailLink(emailLink) -> handleSignInWithEmailLink(emailLink)
      else -> _state.value = GoToRegistrationScreen
    }
  }
  
  private fun handleSignInWithEmailLink(emailLink: String) {
    val email = emailSaver.getEmail()
    if (email == null) {
      _state.value = NoEmailSaved
      return
    }
    _state.value = ShowEmailLinkLoading
    rxCall {
      authenticator.signInWithEmailLink(email, emailLink)
          .subscribeOn(schedulers.io())
          .delay(2, TimeUnit.SECONDS, schedulers.computation(), true)
          .observeOn(schedulers.mainThread())
          .subscribe({ result ->
            val receivedEmail = result.user!!.email
            val message = "Successfully signed in as: $receivedEmail"
            Timber.tag("Registration").d(message)
            _state.value = SuccessfullySignedId
          }, { e ->
            if (e is FirebaseAuthActionCodeException) {
              _state.value = VerificationLinkExpired(email)
            } else {
              _state.value = EmailVerificationFailure(e)
            }
          })
    }
  }
}
