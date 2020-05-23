package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import core.Loggable
import core.MIN_NETWORK_DELAY
import core.NetworkConnection
import core.concurrency.Threader
import core.log
import core.model.Country
import core.releasable.ReleasableViewModel
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.toReason
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf

class MapViewModel(
  private val threader: Threader,
  private val connection: NetworkConnection,
  private val repository: CommonRepository
) : ReleasableViewModel(repository), Loggable {
  
  override val logTag = "Base_Map_ViewModel"
  
  private val _state = MutableLiveData<StateHandle<BaseScreenState>>(StateHandle())
  val state: LiveData<StateHandle<BaseScreenState>>
    get() = _state
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateSelf(isRecreated = true)
      return
    }
    _state.update(Loading)
    tryUpdateFromCache()
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (notifyLoading) {
      _state.update(Loading)
    }
    threader.onIoThread {
      Thread.sleep(MIN_NETWORK_DELAY)
      if (connection.isNotConnected) {
        threader.onMainThread { _state.update(Failure(NO_CONNECTION)) }
        return@onIoThread
      }
      repository.loadCountriesInfo {
        onSuccess { _state.update(LoadedFromNetwork(it)) }
        onFailure {
          _state.update(Failure(it.toReason()))
          log(it) { "Failing loading countries + ${it.message}" }
        }
      }
    }
  }
  
  fun showCountryInfo(country: Country) {
    when (val currentState = _state.currentValue) {
      is LoadedFromCache -> notifyFoundCountry(currentState.countries, country)
      is LoadedFromNetwork -> notifyFoundCountry(currentState.countries, country)
      is FoundCountry -> notifyFoundCountry(currentState.countries, country)
    }
  }
  
  private fun tryUpdateFromCache() {
    repository.tryGetCountriesInfoFromCache {
      onSuccess {
        _state.update(LoadedFromCache(it))
      }
      onNothing {
        updateFromNetwork(notifyLoading = false)
      }
    }
  }
  
  private fun notifyFoundCountry(
    countries: List<Country>, foundCountry: Country
  ) {
    threader.onMainThread {
      _state.update(FoundCountry(countries, foundCountry))
    }
  }
}