package com.arsvechkarev.registration.presentation

import core.BaseScreenState

object InitialState : BaseScreenState()

class EmailLinkSent(val email: String?) : BaseScreenState()

sealed class EmailState : BaseScreenState() {
  
  object Correct : EmailState()
  
  class Incorrect(val errorMsgResId: Int) : EmailState()
}

sealed class TimerState : BaseScreenState() {
  
  class TimeIsTicking(var time: CharSequence) : TimerState()
  
  object TimerHasFinished : TimerState()
}