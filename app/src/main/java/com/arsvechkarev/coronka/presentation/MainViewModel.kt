package com.arsvechkarev.coronka.presentation

import android.content.Intent
import com.google.firebase.auth.FirebaseAuthActionCodeException
import core.BaseScreenState
import core.Loading
import core.RxViewModel
import core.auth.AuthEmailSaver
import core.auth.Authenticator
import core.concurrency.Schedulers
import core.extenstions.withDelayMillis
import core.extenstions.withRequestTimeout

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
    rxCall {
      authenticator.signInWithEmailLink(email, emailLink)
          .subscribeOn(schedulers.io())
          .withDelayMillis(DELAY_MILLIS, schedulers)
          .withRequestTimeout()
          .observeOn(schedulers.mainThread())
          .map<BaseScreenState> { SuccessfullySignedId }
          .onErrorReturn { e ->
            if (e is FirebaseAuthActionCodeException) {
              VerificationLinkExpired(email)
            } else {
              EmailVerificationFailure(e)
            }
          }
          .startWith(Loading())
          .smartSubscribe(_state::setValue)
    }
  }
  
  private companion object {
    
    const val DELAY_MILLIS = 2000L
  }
}
