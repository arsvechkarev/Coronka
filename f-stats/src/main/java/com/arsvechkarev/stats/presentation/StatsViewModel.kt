package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.network.AsyncOperations
import com.arsvechkarev.stats.list.InfoType
import com.arsvechkarev.stats.list.InfoType.CONFIRMED
import com.arsvechkarev.stats.list.InfoType.DEATHS
import com.arsvechkarev.stats.list.InfoType.RECOVERED
import com.arsvechkarev.stats.list.OptionType
import com.arsvechkarev.stats.presentation.StatsScreenState.CountriesLoaded
import com.arsvechkarev.stats.presentation.StatsScreenState.GeneralInfoLoaded
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedAll
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingCountriesInfo
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingGeneralInfo
import core.ApplicationConfig
import core.NetworkConnection
import core.StateHandle
import core.doIfContains
import core.model.Country
import core.model.DisplayableCountry
import core.model.GeneralInfo
import core.model.OptionsItem
import core.recycler.DisplayableItem
import core.update
import datetime.DateTime
import datetime.PATTERN_STANDARD

class StatsViewModel(
  private val connection: NetworkConnection,
  private val threader: ApplicationConfig.Threader,
  private val countriesInfoInteractor: CountriesInfoInteractor,
  private val generalInfoExecutor: GeneralInfoExecutor
) : ViewModel() {
  
  private val cacheOperations = AsyncOperations(2)
  private val networkOperations = AsyncOperations(2)
  
  private val _state = MutableLiveData<StateHandle<StatsScreenState>>(StateHandle())
  val state: LiveData<StateHandle<StatsScreenState>>
    get() = _state
  
  fun startInitialLoading() {
    _state.update(LoadingGeneralInfo)
    _state.update(LoadingCountriesInfo)
    tryUpdateFromCache()
    updateFromNetwork()
  }
  
  fun updateFromNetwork() {
    generalInfoExecutor.getGeneralInfo(onSuccess = {
      networkOperations.addValue(KEY_GENERAL_INFO, it)
      _state.update(GeneralInfoLoaded(it))
    }, onFailure = {})
    countriesInfoInteractor.loadCountriesInfo(onSuccess = { list, dateTime ->
      networkOperations.addValue(KEY_COUNTRIES_AND_TIME, CountriesAndTime(list, dateTime))
      _state.update(CountriesLoaded(list))
    }, onFailure = {})
    networkOperations.onDoneAll {
      threader.backgroundWorker.submit { notifyAboutLoadedData(false, it) }
    }
  }
  
  fun filterList(optionType: OptionType) {
    _state.doIfContains(LoadedAll::class) {
    }
  }
  
  private fun tryUpdateFromCache() {
    generalInfoExecutor.tryUpdateFromCache(onSuccess = {
      cacheOperations.addValue(KEY_GENERAL_INFO, it)
      _state.update(GeneralInfoLoaded(it))
    })
    countriesInfoInteractor.tryUpdateFromCache(onSuccess = { list, dateTime ->
      cacheOperations.addValue(KEY_COUNTRIES_AND_TIME, CountriesAndTime(list, dateTime))
      _state.update(CountriesLoaded(list))
    })
    cacheOperations.onDoneAll {
      threader.backgroundWorker.submit { notifyAboutLoadedData(true, it) }
    }
  }
  
  private fun notifyAboutLoadedData(isFromCache: Boolean, map: Map<String, Any>) {
    val generalInfo = map[KEY_GENERAL_INFO] as GeneralInfo
    val countries = map[KEY_COUNTRIES_AND_TIME] as CountriesAndTime
    val displayableCountries = countries.first.toDisplayableItems(CONFIRMED)
    displayableCountries.sortDescending()
    for (i in displayableCountries.indices) {
      displayableCountries[i].number = i + 1
    }
    val list = ArrayList<DisplayableItem>()
    list.add(generalInfo)
    list.add(OptionsItem)
    list.addAll(displayableCountries)
    val lastUpdateTime = countries.second.formatted(PATTERN_STANDARD)
    val state = LoadedAll(CONFIRMED, list, isFromCache, lastUpdateTime)
    threader.mainThreadWorker.submit { _state.update(state) }
  }
  
  override fun onCleared() {
    countriesInfoInteractor.removeListener()
  }
  
  private fun List<Country>.toDisplayableItems(type: InfoType): MutableList<DisplayableCountry> {
    val countries = ArrayList<DisplayableCountry>()
    for (i in this.indices) {
      val it = this[i]
      val number = determineNumber(type, it)
      countries.add(DisplayableCountry(it.countryName, number))
    }
    return countries
  }
  
  private fun determineNumber(infoType: InfoType, country: Country) = when (infoType) {
    CONFIRMED -> country.confirmed.toInt()
    DEATHS -> country.deaths.toInt()
    RECOVERED -> country.recovered.toInt()
  }
  
  companion object {
    private const val KEY_GENERAL_INFO = "generalInfo"
    private const val KEY_COUNTRIES_AND_TIME = "countriesAndTime"
  }
}

typealias CountriesAndTime = Pair<List<Country>, DateTime>
