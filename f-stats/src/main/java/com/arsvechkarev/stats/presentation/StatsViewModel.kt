package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.CountriesAndTime
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.stats.list.OptionType
import com.arsvechkarev.stats.list.OptionType.CONFIRMED
import com.arsvechkarev.stats.list.OptionType.DEATHS
import com.arsvechkarev.stats.list.OptionType.RECOVERED
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedAll
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Application
import core.NetworkConnection
import core.SavedData
import core.StateHandle
import core.assertContains
import core.async.AsyncOperations
import core.model.Country
import core.model.DisplayableCountry
import core.model.GeneralInfo
import core.recycler.DisplayableItem
import core.releasable.ReleasableViewModel
import core.update
import core.updateAll
import datetime.PATTERN_STANDARD

class StatsViewModel(
  private val connection: NetworkConnection,
  private val threader: Application.Threader,
  private val countriesInfoInteractor: CountriesInfoInteractor,
  private val generalInfoExecutor: GeneralInfoExecutor
) : ReleasableViewModel(countriesInfoInteractor, generalInfoExecutor) {
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val savedData = SavedData()
  
  private val _state = MutableLiveData<StateHandle<StatsScreenState>>(StateHandle())
  val state: LiveData<StateHandle<StatsScreenState>>
    get() = _state
  
  init {
    addReleasable(cacheOperations, networkOperations)
  }
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateAll()
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
    generalInfoExecutor.getGeneralInfo {
      onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
      onFailure { }
    }
    countriesInfoInteractor.loadCountriesInfo {
      onSuccess {
        networkOperations.addValue(KEY_COUNTRIES_AND_TIME, CountriesAndTime(it.first, it.second))
      }
      onFailure { }
    }
    networkOperations.onDoneAll {
      threader.backgroundWorker.submit { notifyAboutLoadedData(false, it) }
    }
  }
  
  fun filterList(optionType: OptionType) {
    _state.assertContains(LoadedAll::class) {
      val list = savedData.get<List<Country>>().toDisplayableItems(optionType)
      _state.update(FilteredCountries(list))
    }
  }
  
  private fun tryUpdateFromCache() {
    generalInfoExecutor.tryUpdateFromCache {
      onSuccess { cacheOperations.addValue(KEY_GENERAL_INFO, it) }
    }
    countriesInfoInteractor.tryUpdateFromCache {
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
    savedData.add(countriesAndTime.first)
    val displayableCountries = countriesAndTime.first.toDisplayableItems(CONFIRMED)
    val lastUpdateTime = countriesAndTime.second.formatted(PATTERN_STANDARD)
    val list = ArrayList<DisplayableItem>()
    list.add(generalInfo)
    list.addAll(displayableCountries)
    val state = LoadedAll(list, isFromCache, lastUpdateTime)
    threader.mainThreadWorker.submit { _state.update(state, remove = Loading::class) }
  }
  
  private fun List<Country>.toDisplayableItems(type: OptionType): MutableList<DisplayableCountry> {
    val countries = ArrayList<DisplayableCountry>()
    for (i in this.indices) {
      val it = this[i]
      val number = determineNumber(type, it)
      countries.add(DisplayableCountry(it.countryName, number))
    }
    countries.sortDescending()
    for (i in countries.indices) {
      countries[i].number = i + 1
    }
    return countries
  }
  
  private fun determineNumber(optionType: OptionType, country: Country) = when (optionType) {
    CONFIRMED -> country.confirmed
    DEATHS -> country.deaths
    RECOVERED -> country.recovered
    else -> TODO()
  }
  
  override fun onCleared() {
    countriesInfoInteractor.removeListener()
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}