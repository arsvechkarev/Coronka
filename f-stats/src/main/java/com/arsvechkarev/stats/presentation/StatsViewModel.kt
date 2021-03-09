package com.arsvechkarev.stats.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.RxViewModel
import core.Schedulers
import core.datasources.GeneralInfoDataSource
import core.datasources.WorldCasesInfoDataSource
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry
import core.model.WorldCasesInfo
import core.transformers.WorldCasesInfoTransformer.toNewDailyCases
import io.reactivex.Observable

class StatsViewModel(
  private val generalInfoDataSource: GeneralInfoDataSource,
  private val worldCasesInfoDataSource: WorldCasesInfoDataSource,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      Observable.zip(
        generalInfoDataSource.requestGeneralInfo()
            .subscribeOn(schedulers.io()),
        worldCasesInfoDataSource.requestWorldDailyCases()
            .map { totalCases -> Pair(totalCases, toNewDailyCases(totalCases)) }
            .subscribeOn(schedulers.io()),
        { info, cases -> WorldCasesInfo(info, cases.first, cases.second) }
      ).withNetworkDelay(schedulers)
          .withRequestTimeout()
          .map<BaseScreenState> { info -> LoadedWorldCasesInfo(info) }
          .withRetry()
          .onErrorReturn(::Failure)
          .startWith(Loading())
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}