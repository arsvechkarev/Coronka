package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.common.TimedData
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.Companion.toReason
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import core.Application.Threader
import core.Loggable
import core.NetworkConnection
import core.async.AsyncOperations
import core.log
import core.model.Country
import core.model.GeneralInfo
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
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val _state = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _state
  
  init {
    addReleasable(cacheOperations, networkOperations)
  }
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateSelf(isRecreated = true)
      return
    }
    _state.update(Loading)
    //    tryUpdateFromCache()
    updateFromNetwork(notifyLoading = false)
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (notifyLoading) {
      _state.update(Loading)
    }
    repository.loadGeneralInfo {
      onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
      onFailure {
        log(it) { "fail map general" }
        notifyFailureIfNeeded(it)
      }
    }
    repository.loadCountriesInfo {
      onSuccess { networkOperations.addValue(KEY_COUNTRIES_AND_TIME, it) }
      onFailure {
        log(it) { "fail map countries + ${it.message}" }
      }
    }
    networkOperations.onDoneAll { map ->
      threader.backgroundWorker.submit {
        val generalInfo = map[KEY_GENERAL_INFO] as TimedData<GeneralInfo>
        val countries = map[KEY_COUNTRIES_AND_TIME] as TimedData<List<Country>>
        threader.mainThreadWorker.submit {
          _state.update(LoadedFromNetwork(countries.data, generalInfo.data))
        }
      }
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    when (val currentState = _state.currentValue) {
      is LoadedFromCache -> performCountrySearch(currentState.countries,
        currentState.generalInfo, countryCode)
      is LoadedFromNetwork -> performCountrySearch(currentState.countries,
        currentState.generalInfo, countryCode)
      is FoundCountry -> performCountrySearch(currentState.countries,
        currentState.generalInfo, countryCode)
    }
  }
  
  private fun tryUpdateFromCache() {
    repository.tryGetGeneralInfoFromCache {
      onSuccess { cacheOperations.addValue(KEY_GENERAL_INFO, it) }
      onNothing { cacheOperations.countDown() }
    }
    repository.tryGetCountriesInfoFromCache {
      onSuccess { cacheOperations.addValue(KEY_COUNTRIES_AND_TIME, it) }
      onNothing { cacheOperations.countDown() }
    }
    cacheOperations.onDoneAll {
      threader.backgroundWorker.submit {
        val generalInfo = it[KEY_GENERAL_INFO] as? TimedData<GeneralInfo>
        val countriesAndTime = it[KEY_COUNTRIES_AND_TIME] as? TimedData<List<Country>>
        if (countriesAndTime == null || generalInfo == null) {
          return@submit
        }
        val lastUpdateTime = countriesAndTime.lastUpdateTime.formatted(PATTERN_STANDARD)
        threader.mainThreadWorker.submit {
          _state.update(LoadedFromCache(countriesAndTime.data, generalInfo.data, lastUpdateTime))
        }
      }
    }
  }
  
  private fun performCountrySearch(
    countries: List<Country>,
    generalInfo: GeneralInfo,
    countryCode: String
  ) {
    val country = countries.find { it.iso2 == countryCode } ?: return
    threader.mainThreadWorker.submit {
      _state.update(FoundCountry(countries, generalInfo, country))
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