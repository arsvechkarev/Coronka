package com.arsvechkarev.rankings.domain

import api.recycler.DifferentiableItem
import base.extensions.f
import com.arsvechkarev.featurescommon.domain.CountriesDataSource
import core.ResultHolder
import core.fold
import core.model.OptionType
import core.model.WorldRegion
import core.model.data.CountryMetaInfo
import core.model.domain.Country
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.CountryFullInfo
import core.model.ui.DisplayableCountry
import core.rx.Schedulers
import io.reactivex.Observable
import io.reactivex.Single

class RankingsInteractor(
  private val countriesDataSource: CountriesDataSource,
  private val countriesMetaInfoDataSource: CountriesMetaInfoDataSource,
  private val countriesFilterer: CountriesFilterer,
  private val countriesMapper: CountryEntitiesToCountriesMapper,
  private val schedulers: Schedulers,
) {
  
  private var countriesMetaInfo: Map<String, CountryMetaInfo>? = null
  
  fun requestCountries(
    initialWorldRegion: WorldRegion,
    initialOptionType: OptionType
  ) = Single.zip(
    countriesDataSource.requestCountries()
        .subscribeOn(schedulers.io())
        .map { ResultHolder.success(countriesMapper.map(it.countries)) }
        .onErrorReturn(ResultHolder.Companion::failure),
    countriesMetaInfoDataSource.getCountriesMetaInfo()
        .subscribeOn(schedulers.io())
        .doOnSuccess { countriesMetaInfo = it },
    { countriesResult, countriesMetaInfo ->
      mapToCountriesListResult(
        countriesResult, countriesMetaInfo, initialWorldRegion, initialOptionType
      )
    }
  ).flatMapObservable { resultHolder ->
    resultHolder.fold(onSuccess = { Observable.just(it) }, onFailure = { Observable.error(it) })
  }
  
  fun filterCountries(
    worldRegion: WorldRegion,
    optionType: OptionType
  ) = Observable.fromCallable {
    val countriesMetaInfo = countriesMetaInfo
    require(countriesMetaInfo != null) { "Countries meta info wasn't initialized" }
    countriesFilterer.filter(worldRegion, optionType)
  }
  
  fun getCountryFullInfo(country: DisplayableCountry) = Single.fromCallable {
    val countriesMetaInfo = countriesMetaInfo
    require(countriesMetaInfo != null) { "Countries meta info wasn't initialized" }
    val population = countriesMetaInfo.getValue(country.country.iso2).population
    val confirmed = country.country.confirmed.f
    val deathRate = country.country.deaths.f / country.country.confirmed
    val percentInCountry = confirmed / population * 100f
    CountryFullInfo(country.country, deathRate, percentInCountry)
  }
  
  private fun mapToCountriesListResult(
    countriesResult: ResultHolder<List<Country>>,
    countriesMetaInfo: Map<String, CountryMetaInfo>,
    initialWorldRegion: WorldRegion,
    initialOptionType: OptionType
  ): ResultHolder<List<DifferentiableItem>> {
    if (countriesResult.isFailure) return ResultHolder.failure(countriesResult.exception)
    val countries = countriesResult.getOrThrow()
    countriesFilterer.prepare(countries, countriesMetaInfo)
    val list = countriesFilterer.filter(initialWorldRegion, initialOptionType)
    return ResultHolder.success(list)
  }
}