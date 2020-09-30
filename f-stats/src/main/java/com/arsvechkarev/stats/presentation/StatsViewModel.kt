package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.common.WorldCasesInfoRepository
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedWorldCasesInfo
import core.Loggable
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.extenstions.assertThat
import core.model.DailyCase
import core.model.WorldCasesInfo
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.Observable

class StatsViewModel(
  private val connection: NetworkConnection,
  private val generalInfoRepository: GeneralInfoRepository,
  private val worldCasesInfoRepository: WorldCasesInfoRepository,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : RxViewModel(), Loggable {
  
  override val logTag = "Base_Stats_ViewModel"
  
  private val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  fun startInitialLoading() {
    performNetworkRequest()
  }
  
  fun performNetworkRequest() {
    rxCall {
      Observable.zip(
        generalInfoRepository.getGeneralInfo()
            .subscribeOn(schedulersProvider.io()),
        worldCasesInfoRepository.getWorldDailyTotalCases()
            .map { totalCases -> Pair(totalCases, totalCases.toNewDailyCases()) }
            .subscribeOn(schedulersProvider.io()),
        { info, cases -> WorldCasesInfo(info, cases.first, cases.second) }
      )
          .subscribeOn(schedulersProvider.io())
          .observeOn(schedulersProvider.mainThread())
          .map(::mapToWorldCasesInfo)
          .onErrorReturn { e -> Failure(e.asFailureReason()) }
          .startWith(Loading)
          .subscribe(_state::setValue)
    }
  }
  
  private fun mapToWorldCasesInfo(it: WorldCasesInfo): BaseScreenState = LoadedWorldCasesInfo(it)
  
  private fun List<DailyCase>.toNewDailyCases(): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    dailyCases.add(DailyCase(this[0].cases / 2, this[0].date))
    for (i in 1 until this.size) {
      val curr = this[i]
      val prev = this[i - 1]
      val diff = curr.cases - prev.cases
      dailyCases.add(DailyCase(diff, curr.date))
    }
    assertThat(this.size == dailyCases.size)
    return dailyCases
  }
}
