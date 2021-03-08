package com.arsvechkarev.rankings.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.RxViewModel
import core.Schedulers
import core.datasources.CountriesMetaInfoDataSource
import core.datasources.TotalInfoDataSource
import core.extenstions.f
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.TotalInfo
import core.model.WorldRegion
import io.reactivex.Observable

class RankingsViewModel(
  private val totalInfoDataSource: TotalInfoDataSource,
  private val countriesMetaInfoDataSource: CountriesMetaInfoDataSource,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  private lateinit var countriesFilterer: CountriesFilterer
  private lateinit var countriesMetaInfo: Map<String, CountryMetaInfo>
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      totalInfoDataSource.requestTotalInfo()
          .subscribeOn(schedulers.io())
          .withRetry()
          .withNetworkDelay(schedulers)
          .withRequestTimeout()
          .map(::transformToScreenState)
          .onErrorReturn(::Failure)
          .startWith(Loading())
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun filter(optionType: OptionType, worldRegion: WorldRegion) {
    rxCall {
      Observable.fromCallable {
        countriesFilterer.filter(optionType, worldRegion)
      }.subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .smartSubscribe { list -> _state.value = FilteredCountries(list) }
    }
  }
  
  fun onCountryClicked(country: DisplayableCountry) {
    val population = countriesMetaInfo.getValue(country.derivedCountry.iso2).population
    val confirmed = country.derivedCountry.confirmed.f
    val deathRate = country.derivedCountry.deaths.f / country.derivedCountry.confirmed
    val percentInCountry = confirmed / population * 100f
    _state.value = ShowCountryInfo(
      country.derivedCountry, deathRate, percentInCountry
    )
  }
  
  private fun transformToScreenState(totalInfo: TotalInfo): BaseScreenState {
    countriesMetaInfo = countriesMetaInfoDataSource.getCountriesMetaInfoSync()
    countriesFilterer = CountriesFilterer(totalInfo.countries, countriesMetaInfo)
    val worldRegion = WorldRegion.WORLDWIDE
    val optionType = OptionType.CONFIRMED
    val data = countriesFilterer.filter(optionType, worldRegion)
    return LoadedCountries(data)
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}