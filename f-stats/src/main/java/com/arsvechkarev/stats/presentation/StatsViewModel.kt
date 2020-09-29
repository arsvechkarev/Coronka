package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromNetwork
import core.Loggable
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.model.TotalData
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.exceptions.OnErrorNotImplementedException

class StatsViewModel(
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : RxViewModel(), Loggable {
  
  override val logTag = "Base_Stats_ViewModel"
  
  private val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  fun startInitialLoading() {
    updateFromNetwork()
  }
  
  fun updateFromNetwork() {
    rxCall {
      allCountriesRepository.getData()
          .subscribeOn(schedulersProvider.io())
          .map(::loadedFromNetwork)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .subscribe(_state::setValue) {
            throw OnErrorNotImplementedException(it)
          }
    }
  }
  
  private fun loadedFromNetwork(it: TotalData): BaseScreenState = LoadedFromNetwork(it)
}