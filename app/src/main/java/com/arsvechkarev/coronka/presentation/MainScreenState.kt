package com.arsvechkarev.coronka.presentation

import core.BaseScreenState
import core.Failure

object GoToRegistrationScreen : BaseScreenState()

object GoToMainScreen : BaseScreenState()

object SuccessfullySignedId : BaseScreenState()

class VerificationLinkExpired(val email: String) : BaseScreenState()

class EmailVerificationFailure(e: Throwable) : Failure(e)

object NoEmailSaved : BaseScreenState()