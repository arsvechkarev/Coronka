package com.arsvechkarev.rankings.presentation

import base.RxViewModel
import base.extensions.f
import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.common.domain.CountriesMetaInfoRepository
import com.arsvechkarev.common.domain.TotalInfoDataSource
import com.arsvechkarev.rankings.domain.CountriesFilterer
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.Schedulers
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.TotalInfo
import core.model.WorldRegion
import io.reactivex.Observable

class RankingsViewModel(
  private val totalInfoDataSource: TotalInfoDataSource,
  private val countriesMetaInfoRepository: CountriesMetaInfoRepository,
  private val countriesFilterer: CountriesFilterer,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  private val countriesMetaInfo: Map<String, CountryMetaInfo> by lazy {
    countriesMetaInfoRepository.getCountriesMetaInfoSync()
  }
  
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
          .toObservable()
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map(::transformToScreenState)
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun filter(worldRegion: WorldRegion, optionType: OptionType) {
    rxCall {
      Observable.fromCallable {
        countriesFilterer.filter(worldRegion, optionType)
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
    val data = countriesFilterer.filterInitial(
      totalInfo.countries,
      countriesMetaInfo,
      DefaultWorldRegion,
      DefaultOptionType
    )
    return LoadedCountries(data)
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
  
  companion object {
    
    val DefaultWorldRegion = WorldRegion.WORLDWIDE
    val DefaultOptionType = OptionType.CONFIRMED
  }
}