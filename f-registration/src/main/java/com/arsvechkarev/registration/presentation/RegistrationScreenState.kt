package com.arsvechkarev.registration.presentation

import core.BaseScreenState

object EmailLinkSent : BaseScreenState()

sealed class EmailState : BaseScreenState() {
  
  object Correct : EmailState()
  
  class Incorrect(val errorMsgResId: Int) : EmailState()
}

sealed class TimerState : BaseScreenState() {
  
  class TimeIsTicking(var time: CharSequence, var millis: Long) : TimerState()
  
  object TimerHasFinished : TimerState()
}