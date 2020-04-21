package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.Companion.toReason
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import core.Application.Threader
import core.Loggable
import core.NetworkConnection
import core.log
import core.model.Country
import core.releasable.ReleasableViewModel
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf
import datetime.PATTERN_STANDARD

class MapViewModel(
  private val threader: Threader,
  private val connection: NetworkConnection,
  private val repository: CommonRepository
) : ReleasableViewModel(repository), Loggable {
  
  override val logTag = "Base_Map_ViewModel"
  
  private val _state = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _state
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateSelf(isRecreated = true)
      return
    }
    _state.update(Loading)
    tryUpdateFromCache()
    updateFromNetwork(notifyLoading = false)
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (notifyLoading) {
      _state.update(Loading)
    }
    repository.loadCountriesInfo {
      onSuccess { _state.update(LoadedFromNetwork(it.data)) }
      onFailure { log(it) { "Failing loading countries + ${it.message}" } }
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    when (val currentState = _state.currentValue) {
      is LoadedFromCache -> performCountrySearch(currentState.countries, countryCode)
      is LoadedFromNetwork -> performCountrySearch(currentState.countries, countryCode)
      is FoundCountry -> performCountrySearch(currentState.countries, countryCode)
    }
  }
  
  private fun tryUpdateFromCache() {
    repository.tryGetCountriesInfoFromCache {
      onSuccess {
        _state.update(LoadedFromCache(it.data, it.lastUpdateTime.formatted(PATTERN_STANDARD)))
      }
    }
  }
  
  private fun performCountrySearch(
    countries: List<Country>,
    countryCode: String
  ) {
    val country = countries.find { it.iso2 == countryCode } ?: return
    threader.mainThreadWorker.submit {
      _state.update(FoundCountry(countries, country))
    }
  }
  
  private fun notifyFailureIfNeeded(error: Throwable) {
    val currentValue = _state.value?.currentValue
    val reason = error.toReason()
    if (currentValue != null
        && currentValue is Failure
        && currentValue.reason == reason) {
      return
    }
    _state.update(Failure(reason))
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}