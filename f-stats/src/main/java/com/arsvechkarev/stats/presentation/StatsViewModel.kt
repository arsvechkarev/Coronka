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
import core.model.DailyCase
import core.model.GeneralInfo
import core.model.WorldCasesInfo
import core.transformers.WorldCasesInfoTransformer.toNewDailyCases
import io.reactivex.Observable
import io.reactivex.Single

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
      Single.zip(
        generalInfoDataSource.requestGeneralInfo()
            .subscribeOn(schedulers.io())
            .map(Result.Companion::success)
            .onErrorReturn(Result.Companion::failure),
        worldCasesInfoDataSource.requestWorldDailyCases()
            .subscribeOn(schedulers.io())
            .map(Result.Companion::success)
            .onErrorReturn(Result.Companion::failure),
        { info, cases -> mapToScreenState(info, cases) }
      ).toObservable()
          .withNetworkDelay(schedulers)
          .flatMap { item ->
            when (item) {
              is Failure -> Observable.error(item.throwable)
              else -> Observable.just(item)
            }
          }
          .withRetry()
          .withRequestTimeout(schedulers)
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  @Suppress("UNCHECKED_CAST")
  private fun mapToScreenState(
    resultInfo: Result<GeneralInfo>,
    resultCases: Result<List<DailyCase>>
  ): BaseScreenState {
    val generalInfo = resultInfo.getOrElse { return Failure(it) }
    val cases = resultCases.getOrElse { return Failure(it) }
    val worldCasesInfo = WorldCasesInfo(generalInfo, cases, toNewDailyCases(cases))
    return LoadedWorldCasesInfo(worldCasesInfo)
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}