package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.stats.domain.ListFilterer
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromCache
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromNetwork
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Loggable
import core.MIN_NETWORK_DELAY
import core.NetworkConnection
import core.SavedData
import core.concurrency.AsyncOperations
import core.concurrency.Threader
import core.log
import core.model.Country
import core.model.GeneralInfo
import core.model.OptionType
import core.model.OptionType.CONFIRMED
import core.releasable.ReleasableViewModel
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.toReason
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf

class StatsViewModel(
  private val threader: Threader,
  private val connection: NetworkConnection,
  private val repository: CommonRepository,
  private val filterer: ListFilterer
) : ReleasableViewModel(repository), Loggable {
  
  override val logTag = "Base_Stats_ViewModel"
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val savedData = SavedData()
  
  private val _state = MutableLiveData<StateHandle<BaseScreenState>>(StateHandle())
  val state: LiveData<StateHandle<BaseScreenState>>
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
      repository.loadGeneralInfo {
        onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
        onFailure { log(it) { "Failure loading general info: ${it.message}" } }
      }
      repository.loadCountriesInfo {
        onSuccess {
          networkOperations.addValue(KEY_COUNTRIES, it)
        }
        onFailure {
          _state.update(Failure(it.toReason()))
        }
      }
      networkOperations.onDoneAll { map ->
        threader.onBackground {
          val generalInfo = map[KEY_GENERAL_INFO] as GeneralInfo
          val countriesAndTime = map[KEY_COUNTRIES] as List<Country>
          savedData.add(countriesAndTime)
          savedData.add(generalInfo)
          filterer.filter(countriesAndTime, generalInfo, CONFIRMED) {
            onSuccess { _state.update(LoadedFromNetwork(it)) }
          }
        }
      }
    }
  }
  
  fun filterList(optionType: OptionType) {
    when (_state.currentValue) {
      is LoadedFromCache -> performFiltering(optionType)
      is LoadedFromNetwork -> performFiltering(optionType)
      is FilteredCountries -> performFiltering(optionType)
    }
  }
  
  private fun tryUpdateFromCache() {
    repository.tryGetGeneralInfoFromCache {
      onSuccess { cacheOperations.addValue(KEY_GENERAL_INFO, it) }
      onNothing {
        cacheOperations.countDown()
        updateFromNetwork(notifyLoading = false)
      }
    }
    repository.tryGetCountriesInfoFromCache {
      onSuccess { cacheOperations.addValue(KEY_COUNTRIES_AND_TIME, it) }
      onNothing { cacheOperations.countDown() }
    }
    cacheOperations.onDoneAll { map ->
      threader.onBackground {
        val generalInfo = map[KEY_GENERAL_INFO] as? GeneralInfo
        val countriesAndTime = map[KEY_COUNTRIES_AND_TIME] as? List<Country>
        if (generalInfo == null || countriesAndTime == null) {
          return@onBackground
        }
        savedData.add(countriesAndTime)
        savedData.add(generalInfo)
        filterer.filter(countriesAndTime, generalInfo, CONFIRMED) {
          onSuccess {
            _state.update(LoadedFromCache(it))
          }
        }
      }
    }
  }
  
  private fun performFiltering(optionType: OptionType) {
    filterer.filter(savedData.get(), savedData.get(), optionType) {
      onSuccess { _state.update(FilteredCountries(it)) }
    }
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES = "countries"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}