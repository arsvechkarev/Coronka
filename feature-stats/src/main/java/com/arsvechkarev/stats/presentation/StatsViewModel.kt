package com.arsvechkarev.stats.presentation

import base.RxViewModel
import com.arsvechkarev.stats.domain.StatsUseCase
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.rx.Schedulers

class StatsViewModel(
  private val statsUseCase: StatsUseCase,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::retryLoadingData)
    }
  }
  
  fun startLoadingData() {
    if (state.value != null) return
    performLoadingData()
  }
  
  fun retryLoadingData() {
    if (state.value !is Failure) return
    performLoadingData()
  }
  
  private fun performLoadingData() {
    rxCall {
      statsUseCase.getMainStatistics()
          .subscribeOn(schedulers.io())
          .map<BaseScreenState>(::LoadedMainStatistics)
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}