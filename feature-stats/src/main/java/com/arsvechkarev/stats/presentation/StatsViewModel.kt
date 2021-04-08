package com.arsvechkarev.stats.presentation

import base.RxViewModel
import com.arsvechkarev.stats.domain.StatsUseCase
import core.BaseScreenState
import core.Failure
import core.Loading
import core.rx.Schedulers

class StatsViewModel(
  private val statsUseCase: StatsUseCase,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  fun startLoadingData() {
    if (state.value != null) return
    performLoadingData()
  }
  
  fun retryLoadingData() {
    if (state.value !is Failure) return
    performLoadingData()
  }
  
  fun onNetworkAvailable() {
    if (_state.value is Failure) retryLoadingData()
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
}