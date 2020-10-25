package com.arsvechkarev.registration.presentation

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import core.Failure
import core.KeyValueStorage
import core.Loading
import core.RxViewModel
import core.auth.AuthEmailSaver
import core.auth.AuthSettings
import core.auth.Authenticator
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import timerx.TimeFormatter
import timerx.Timer
import timerx.TimerBuilder
import java.util.concurrent.TimeUnit

class RegistrationViewModel(
  private val authenticator: Authenticator,
  private val emailSaver: AuthEmailSaver,
  private val timerSaver: KeyValueStorage,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  private val _emailState = MutableLiveData<EmailState>()
  val emailState: LiveData<EmailState>
    get() = _emailState
  
  private val _timerState = MutableLiveData<TimerState>()
  val timerState: LiveData<TimerState>
    get() = _timerState
  
  private lateinit var timer: Timer
  
  private var timerTickingState = TimerState.TimeIsTicking(
    TimeFormatter.format(TIMER_FORMAT, TIMER_TIME_MILLIS),
    TIMER_TIME_MILLIS
  )
  
  fun initializeTimer(resetAndStartTimer: Boolean) {
    val pair = createTimer(resetAndStartTimer)
    val builder = pair.first
    val startTimer = pair.second
    timer = builder.build()
    if (startTimer) {
      timer.start()
      _state.value = EmailLinkSent(emailSaver.getEmail())
    } else {
      _state.value = InitialState
    }
  }
  
  fun sendEmailLink(email: String, resetTimer: Boolean = false) {
    _emailState.value = EmailChecker.validateEmail(email)
    if (_emailState.value is EmailState.Incorrect) {
      return
    }
    if (resetTimer) {
      timer.reset()
      timer.start()
    }
    _state.value = Loading()
    rxCall {
      authenticator.sendSignInLinkToEmail(email, AuthSettings)
          .subscribeOn(schedulers.io())
          .withRequestTimeout()
          .withNetworkDelay(schedulers)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(onComplete = {
            emailSaver.saveEmail(email)
            _state.value = EmailLinkSent(email)
            initTimer()
          }, onError = { e ->
            _state.value = Failure(e)
          })
    }
  }
  
  override fun onCleared() {
    super.onCleared()
    timer.release()
  }
  
  private fun initTimer() {
    val timeInFuture = SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(1)
    timerSaver.execute { putLong(TIMER_KEY, timeInFuture) }
    timer.start()
  }
  
  private fun onTimerTick(time: CharSequence, millis: Long) {
    timerTickingState.time = time
    timerTickingState.millis = millis
    _timerState.value = timerTickingState
  }
  
  private fun onTimerFinish() {
    _timerState.value = TimerState.TimerHasFinished
    timerSaver.execute { clear() }
  }
  
  private fun createTimer(reset: Boolean): Pair<TimerBuilder, Boolean> {
    val builder = TimerBuilder()
        .startFormat(TIMER_FORMAT)
        .onTick { time, millis -> onTimerTick(time, millis) }
        .onFinish { onTimerFinish() }
    var startTimer = false
    if (reset) {
      builder.startTime(TIMER_TIME_MILLIS, TimeUnit.MILLISECONDS)
      return Pair(builder, true)
    }
    if (timerSaver.hasLong(TIMER_KEY)) {
      val timeLeft = timerSaver.getLong(TIMER_KEY) - SystemClock.elapsedRealtime()
      if (timeLeft > 0) {
        builder.startTime(timeLeft, TimeUnit.MILLISECONDS)
        startTimer = true
      } else {
        builder.startTime(TIMER_TIME_MILLIS, TimeUnit.MILLISECONDS)
      }
    } else {
      builder.startTime(TIMER_TIME_MILLIS, TimeUnit.MILLISECONDS)
    }
    return Pair(builder, startTimer)
  }
  
  companion object {
    
    const val TIMER_TIME_MILLIS = 60 * 1000L
    const val TIMER_FORMAT = "MM:SS"
    const val TIMER_KEY = "TimeLeftForWaiting"
    const val TIMER_FILENAME = "ResendEmailTimerSaver"
  }
}