package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CountriesAndTime
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedAll
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountries
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import com.arsvechkarev.map.presentation.MapScreenState.LoadingGeneralInfo
import core.Application
import core.Loggable
import core.NetworkConnection
import core.StateHandle
import core.async.AsyncOperations
import core.doIfContains
import core.model.GeneralInfo
import core.releasable.ReleasableViewModel
import core.remove
import core.update
import datetime.PATTERN_STANDARD

class MapViewModel(
  private val connection: NetworkConnection,
  private val threader: Application.Threader,
  private val interactor: CountriesInfoInteractor,
  private val generalInfoExecutor: GeneralInfoExecutor
) : ReleasableViewModel(interactor, generalInfoExecutor), Loggable {
  
  override val logTag = "Map_MapViewModel"
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val _state = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _state
  
  init {
    addReleasable(cacheOperations, networkOperations)
  }
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) return
    _state.update(LoadingGeneralInfo)
    _state.update(LoadingCountries)
    tryUpdateFromCache()
    updateFromNetwork(notifyLoading = false)
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (notifyLoading) {
      _state.update(LoadingGeneralInfo)
      _state.update(LoadingCountries)
    }
    generalInfoExecutor.getGeneralInfo {
      onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
      onFailure {}
    }
    interactor.loadCountriesInfo {
      onSuccess { pair ->
        networkOperations.addValue(KEY_COUNTRIES_AND_TIME,
          CountriesAndTime(pair.first, pair.second))
      }
      onFailure {}
    }
    networkOperations.onDoneAll {
      threader.backgroundWorker.submit { notifyAboutLoadedData(false, it) }
    }
  }
  
  private fun tryUpdateFromCache() {
    generalInfoExecutor.tryUpdateFromCache {
      onSuccess { cacheOperations.addValue(KEY_GENERAL_INFO, it) }
    }
    interactor.tryUpdateFromCache {
      onSuccess {
        cacheOperations.addValue(KEY_COUNTRIES_AND_TIME, CountriesAndTime(it.first, it.second))
      }
    }
    cacheOperations.onDoneAll {
      threader.backgroundWorker.submit { notifyAboutLoadedData(true, it) }
    }
  }
  
  private fun notifyAboutLoadedData(isFromCache: Boolean, map: Map<String, Any>) {
    val generalInfo = map[KEY_GENERAL_INFO] as GeneralInfo
    val countriesAndTime = map[KEY_COUNTRIES_AND_TIME] as CountriesAndTime
    val lastUpdateTime = countriesAndTime.second.formatted(PATTERN_STANDARD)
    val state = LoadedAll(countriesAndTime.first, generalInfo, isFromCache, lastUpdateTime)
    threader.mainThreadWorker.submit { _state.update(state) }
  }
  
  fun findCountryByCode(countryCode: String) {
    _state.doIfContains(LoadedAll::class) {
      _state.update(LoadingCountryInfo)
      threader.backgroundWorker.submit {
        val country = countriesList.find { it.countryCode == countryCode } ?: return@submit
        _state.remove(LoadingCountryInfo::class)
        threader.mainThreadWorker.submit { _state.update(FoundCountry(country)) }
      }
    }
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}