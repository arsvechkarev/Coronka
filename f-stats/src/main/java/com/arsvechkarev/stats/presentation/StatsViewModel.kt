package com.arsvechkarev.stats.presentation

import com.arsvechkarev.stats.presentation.SuccessOrError.Error
import com.arsvechkarev.stats.presentation.SuccessOrError.Success
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.RxViewModel
import core.Schedulers
import core.datasources.GeneralInfoDataSource
import core.datasources.WorldCasesInfoDataSource
import core.extenstions.assertThat
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry
import core.model.DailyCase
import core.model.GeneralInfo
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
            .map<SuccessOrError>(SuccessOrError::Success)
            .onErrorReturn(SuccessOrError::Error)
            .subscribeOn(schedulers.io()),
        worldCasesInfoDataSource.requestWorldDailyCases()
            .map<SuccessOrError> { dailyCases ->
              Success(Pair(dailyCases, toNewDailyCases(dailyCases)))
            }
            .onErrorReturn(SuccessOrError::Error)
            .subscribeOn(schedulers.io()),
        { info, cases -> mapToScreenState(info, cases) }
      ).withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  @Suppress("UNCHECKED_CAST")
  private fun mapToScreenState(info: SuccessOrError, cases: SuccessOrError): BaseScreenState {
    if (info is Error) return Failure(info.throwable)
    if (cases is Error) return Failure(cases.throwable)
    assertThat(info is Success<*>)
    assertThat(cases is Success<*>)
    val generalInfo = info.value as GeneralInfo
    val casesPair = cases.value as Pair<List<DailyCase>, List<DailyCase>>
    val worldCasesInfo = WorldCasesInfo(generalInfo, casesPair.first, casesPair.second)
    return LoadedWorldCasesInfo(worldCasesInfo)
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}