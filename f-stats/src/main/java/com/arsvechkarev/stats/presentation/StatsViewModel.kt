package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.Repository
import com.arsvechkarev.common.TimedResult
import com.arsvechkarev.stats.list.OptionType
import com.arsvechkarev.stats.list.OptionType.CONFIRMED
import com.arsvechkarev.stats.list.OptionType.DEATHS
import com.arsvechkarev.stats.list.OptionType.DEATH_RATE
import com.arsvechkarev.stats.list.OptionType.RECOVERED
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromCache
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
import core.remove
import core.update
import core.updateAll
import datetime.PATTERN_STANDARD

class StatsViewModel(
  private val connection: NetworkConnection,
  private val threader: Application.Threader,
  private val repository: Repository
) : ReleasableViewModel(repository) {
  
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
    repository.loadGeneralInfo {
      onSuccess { networkOperations.addValue(KEY_GENERAL_INFO, it) }
      onFailure { }
    }
    repository.loadCountriesInfo {
      onSuccess {
        networkOperations.addValue(KEY_COUNTRIES, it)
      }
      onFailure { }
    }
    networkOperations.onDoneAll { map ->
      threader.backgroundWorker.submit {
        val generalInfo = map[KEY_GENERAL_INFO] as? GeneralInfo
        val countries = map[KEY_COUNTRIES] as? List<Country>
        if (generalInfo == null || countries == null) {
          return@submit
        }
        savedData.add(countries)
        val displayableCountries = countries.toDisplayableItems(CONFIRMED)
        val list = ArrayList<DisplayableItem>()
        list.add(generalInfo)
        list.addAll(displayableCountries)
        val state = StatsScreenState.LoadedFromNetwork(list)
        threader.mainThreadWorker.submit {
          _state.remove(LoadedFromCache::class)
          _state.remove(Loading::class)
          _state.update(state)
        }
      }
    }
  }
  
  fun filterList(optionType: OptionType) {
    _state.assertContains(LoadedFromCache::class) {
      val list = savedData.get<List<Country>>().toDisplayableItems(optionType)
      _state.update(FilteredCountries(optionType, list))
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
        val generalInfo = map[KEY_GENERAL_INFO] as? GeneralInfo
        val countriesAndTime = map[KEY_COUNTRIES_AND_TIME] as? TimedResult<List<Country>>
        if (generalInfo == null || countriesAndTime == null) {
          return@submit
        }
        savedData.add(countriesAndTime.result)
        val displayableCountries = countriesAndTime.result.toDisplayableItems(CONFIRMED)
        val lastUpdateTime = countriesAndTime.lastUpdateTime.formatted(PATTERN_STANDARD)
        val list = ArrayList<DisplayableItem>()
        list.add(generalInfo)
        list.addAll(displayableCountries)
        val state = LoadedFromCache(list, lastUpdateTime)
        threader.mainThreadWorker.submit { _state.update(state, remove = Loading::class) }
      }
    }
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
  
  private fun determineNumber(type: OptionType, country: Country): Number = when (type) {
    CONFIRMED -> country.confirmed
    DEATHS -> country.deaths
    RECOVERED -> country.recovered
    DEATH_RATE -> country.deaths.toFloat() / country.confirmed.toFloat()
    else -> TODO()
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES = "countries"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}