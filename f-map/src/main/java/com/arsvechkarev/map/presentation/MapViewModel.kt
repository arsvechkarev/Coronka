package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
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
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.toReason
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf

class MapViewModel(
  private val threader: Threader,
  private val connection: NetworkConnection,
  private val repository: CommonRepository,
  private val minDelay: Long = 400L
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
    threader.backgroundWorker.submit {
      Thread.sleep(minDelay)
      //      if (false) {
      //        threader.mainThreadWorker.submit { _state.update(Failure(NO_CONNECTION)) }
      //        return@submit
      //      }
      repository.loadCountriesInfo {
        onSuccess { _state.update(LoadedFromNetwork(it)) }
        onFailure {
          _state.update(Failure(it.toReason()))
          log(it) { "Failing loading countries + ${it.message}" }
        }
      }
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
        _state.update(LoadedFromCache(it))
      }
      onNothing {
        updateFromNetwork(notifyLoading = false)
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
}