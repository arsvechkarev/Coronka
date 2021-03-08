package com.arsvechkarev.stats.presentation

import android.net.ConnectivityManager
import com.arsvechkarev.common.GeneralInfoDataSource
import com.arsvechkarev.common.WorldCasesInfoRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.RxViewModel
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry
import core.model.DailyCase
import core.model.WorldCasesInfo
import io.reactivex.Observable

class StatsViewModel(
  private val generalInfoDataSource: GeneralInfoDataSource,
  private val worldCasesInfoRepository: WorldCasesInfoRepository,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), ConnectivityManager.OnNetworkActiveListener {
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkActive() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      Observable.zip(
        generalInfoDataSource.getGeneralInfo()
            .subscribeOn(schedulers.io()),
        worldCasesInfoRepository.getWorldDailyTotalCases()
            .map { totalCases -> Pair(totalCases, totalCases.toNewDailyCases()) }
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
  
  private fun List<DailyCase>.toNewDailyCases(): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    for (i in 1 until this.size) {
      val curr = this[i]
      val prev = this[i - 1]
      val diff = curr.cases - prev.cases
      dailyCases.add(DailyCase(diff, curr.date))
    }
    return dailyCases
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}