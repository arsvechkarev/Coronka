package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.common.TimedData
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import core.Application.Threader
import core.Loggable
import core.NetworkConnection
import core.StateHandle
import core.async.AsyncOperations
import core.doIfContains
import core.log
import core.model.Country
import core.model.GeneralInfo
import core.releasable.ReleasableViewModel
import core.remove
import core.update
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
    if (isRecreated) return
    _state.update(Loading)
    tryUpdateFromCache()
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
      }
    }
    repository.loadCountriesInfo {
      onSuccess { networkOperations.addValue(KEY_COUNTRIES_AND_TIME, it) }
      onFailure {
        log(it) { "fail map countries" }
      }
    }
    networkOperations.onDoneAll { map ->
      threader.backgroundWorker.submit {
        val generalInfo = map[KEY_GENERAL_INFO] as TimedData<GeneralInfo>
        val countries = map[KEY_COUNTRIES_AND_TIME] as TimedData<List<Country>>
        val state = LoadedFromNetwork(countries.data, generalInfo.data)
        threader.mainThreadWorker.submit { _state.update(state) }
      }
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
        val state = LoadedFromCache(countriesAndTime.data, generalInfo.data, lastUpdateTime)
        threader.mainThreadWorker.submit { _state.update(state) }
      }
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    _state.doIfContains(LoadedFromCache::class) {
      _state.update(LoadingCountryInfo)
      threader.backgroundWorker.submit {
        val country = countriesList.find { it.iso2 == countryCode } ?: return@submit
        _state.remove(LoadingCountryInfo::class)
        threader.mainThreadWorker.submit {
          _state.remove(LoadedFromCache::class)
          _state.remove(Loading::class)
          _state.update(FoundCountry(country))
        }
      }
    }
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}