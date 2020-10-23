package com.arsvechkarev.registration.presentation

import com.arsvechkarev.registration.R

object EmailChecker {
  
  fun validateEmail(email: String) = when {
    email.isBlank() -> EmailState.Incorrect(R.string.error_email_is_empty)
    !email.contains("@") -> EmailState.Incorrect(R.string.error_email_is_incorrect)
    else -> EmailState.Correct
  }
}