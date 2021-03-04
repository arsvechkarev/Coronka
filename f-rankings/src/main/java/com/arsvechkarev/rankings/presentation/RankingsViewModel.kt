package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.common.AllCountriesDataSource
import com.arsvechkarev.common.CountriesMetaInfoRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.RxViewModel
import core.concurrency.Schedulers
import core.extenstions.f
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.TotalData
import core.model.WorldRegion
import io.reactivex.Observable

class RankingsViewModel(
  private val allCountriesDataSource: AllCountriesDataSource,
  private val metaInfoRepository: CountriesMetaInfoRepository,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  private lateinit var countriesFilterer: CountriesFilterer
  private lateinit var countriesMetaInfo: Map<String, CountryMetaInfo>
  
  fun startLoadingData() {
    rxCall {
      allCountriesDataSource.getTotalData()
          .subscribeOn(schedulers.io())
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
  
  private fun transformToScreenState(totalData: TotalData): BaseScreenState {
    countriesMetaInfo = metaInfoRepository.getCountriesMetaInfoSync()
    countriesFilterer = CountriesFilterer(totalData.countries, countriesMetaInfo)
    val worldRegion = WorldRegion.WORLDWIDE
    val optionType = OptionType.CONFIRMED
    val data = countriesFilterer.filter(optionType, worldRegion)
    return LoadedCountries(data)
  }
}