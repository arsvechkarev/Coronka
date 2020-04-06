package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.common.TimedData
import com.arsvechkarev.stats.domain.ListFilterer
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromCache
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromNetwork
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Application
import core.Loggable
import core.NetworkConnection
import core.SavedData
import core.async.AsyncOperations
import core.log
import core.model.Country
import core.model.GeneralInfo
import core.model.OptionType
import core.model.OptionType.CONFIRMED
import core.releasable.ReleasableViewModel
import datetime.PATTERN_STANDARD

class StatsViewModel(
  private val connection: NetworkConnection,
  private val threader: Application.Threader,
  private val repository: CommonRepository,
  private val filterer: ListFilterer
) : ReleasableViewModel(repository), Loggable {
  
  override val logTag = "Base_Stats_ViewModel"
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val savedData = SavedData()
  
  private val _state = MutableLiveData<StatsScreenState>()
  val state: LiveData<StatsScreenState>
    get() = _state
  
  init {
    addReleasable(cacheOperations, networkOperations)
  }
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      return
    }
    _state.value = Loading
    tryUpdateFromCache()
    updateFromNetwork(notifyLoading = false)
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (notifyLoading) {
      _state.value = Loading
    }
    repository.loadGeneralInfo {
      onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
      onFailure {
        log(it) { "fail stats general" }
      }
    }
    repository.loadCountriesInfo {
      onSuccess {
        networkOperations.addValue(KEY_COUNTRIES, it)
      }
      onFailure {
        log(it) { "fail stats countries" }
      }
    }
    networkOperations.onDoneAll { map ->
      threader.backgroundWorker.submit {
        val generalInfo = map[KEY_GENERAL_INFO] as TimedData<GeneralInfo>
        val countriesAndTime = map[KEY_COUNTRIES] as TimedData<List<Country>>
        savedData.add(countriesAndTime.data)
        savedData.add(generalInfo.data)
        filterer.filter(countriesAndTime.data, generalInfo.data, CONFIRMED) {
          onSuccess {
            _state.value = LoadedFromNetwork(it)
          }
        }
      }
    }
  }
  
  fun filterList(optionType: OptionType) {
    when (_state.value) {
      is LoadedFromNetwork -> performFiltering(optionType)
      is FilteredCountries -> performFiltering(optionType)
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
    cacheOperations.onDoneAll { map ->
      threader.backgroundWorker.submit {
        val generalInfo = map[KEY_GENERAL_INFO] as? TimedData<GeneralInfo>
        val countriesAndTime = map[KEY_COUNTRIES_AND_TIME] as? TimedData<List<Country>>
        if (generalInfo == null || countriesAndTime == null) {
          return@submit
        }
        savedData.add(countriesAndTime.data)
        savedData.add(generalInfo.data)
        filterer.filter(countriesAndTime.data, generalInfo.data, CONFIRMED) {
          onSuccess {
            _state.value = LoadedFromCache(it, countriesAndTime.lastUpdateTime.formatted(
              PATTERN_STANDARD))
          }
        }
      }
    }
  }
  
  private fun performFiltering(optionType: OptionType) {
    filterer.filter(savedData.get(), savedData.get(), optionType) {
      onSuccess { _state.value = FilteredCountries(it) }
    }
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES = "countries"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}