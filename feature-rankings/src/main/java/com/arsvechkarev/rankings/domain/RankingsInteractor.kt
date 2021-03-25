package com.arsvechkarev.rankings.domain

import api.recycler.DifferentiableItem
import base.extensions.f
import com.arsvechkarev.common.domain.CountriesDataSource
import com.arsvechkarev.common.repository.CountriesMetaInfoRepository
import core.ResultHolder
import core.Schedulers
import core.fold
import core.model.OptionType
import core.model.WorldRegion
import core.model.data.CountryMetaInfo
import core.model.domain.Country
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.CountryFullInfo
import core.model.ui.DisplayableCountry
import io.reactivex.Observable
import io.reactivex.Single

interface RankingsInteractor {
  
  fun requestCountries(
    initialWorldRegion: WorldRegion,
    initialOptionType: OptionType
  ): Observable<List<DifferentiableItem>>
  
  fun filterCountries(
    worldRegion: WorldRegion,
    optionType: OptionType
  ): Observable<List<DifferentiableItem>>
  
  fun getCountryFullInfo(country: DisplayableCountry): Single<CountryFullInfo>
}

class DefaultRankingsInteractor(
  private val countriesDataSource: CountriesDataSource,
  private val countriesMetaInfoRepository: CountriesMetaInfoRepository,
  private val countriesFilterer: CountriesFilterer,
  private val countriesMapper: CountryEntitiesToCountriesMapper,
  private val schedulers: Schedulers,
) : RankingsInteractor {
  
  private var countriesMetaInfo: Map<String, CountryMetaInfo>? = null
  
  override fun requestCountries(
    initialWorldRegion: WorldRegion,
    initialOptionType: OptionType
  ): Observable<List<DifferentiableItem>> {
    return Single.zip(
      countriesDataSource.requestCountries()
          .subscribeOn(schedulers.io())
          .doOnSuccess { println(it) }
          .map { ResultHolder.success(countriesMapper.map(it.countries)) }
          .onErrorReturn(ResultHolder.Companion::failure),
      countriesMetaInfoRepository.getCountriesMetaInfo()
          .subscribeOn(schedulers.io())
          .doOnSuccess { countriesMetaInfo = it },
      { countriesResult, countriesMetaInfo ->
        mapToCountriesListResult(
          countriesResult, countriesMetaInfo, initialWorldRegion, initialOptionType
        )
      }
    ).flatMapObservable {
      it.fold(onSuccess = { Observable.just(it) }, onFailure = { Observable.error(it) })
    }
  }
  
  private fun mapToCountriesListResult(
    countriesResult: ResultHolder<List<Country>>,
    countriesMetaInfo: Map<String, CountryMetaInfo>,
    initialWorldRegion: WorldRegion,
    initialOptionType: OptionType
  ): ResultHolder<List<DifferentiableItem>> {
    if (countriesResult.isFailure) return ResultHolder.failure(countriesResult.exception)
    val countries = countriesResult.getOrThrow()
    return ResultHolder.success(countriesFilterer.filterInitial(countries, countriesMetaInfo,
      initialWorldRegion, initialOptionType))
  }
  
  override fun filterCountries(
    worldRegion: WorldRegion,
    optionType: OptionType
  ) = Observable.fromCallable {
    val countriesMetaInfo = countriesMetaInfo
    require(countriesMetaInfo != null) { "Countries meta info wasn't initialized" }
    countriesFilterer.filter(worldRegion, optionType)
  }
  
  override fun getCountryFullInfo(country: DisplayableCountry) = Single.fromCallable {
    val countriesMetaInfo = countriesMetaInfo
    require(countriesMetaInfo != null) { "Countries meta info wasn't initialized" }
    val population = countriesMetaInfo.getValue(country.country.iso2).population
    val confirmed = country.country.confirmed.f
    val deathRate = country.country.deaths.f / country.country.confirmed
    val percentInCountry = confirmed / population * 100f
    CountryFullInfo(country.country, deathRate, percentInCountry)
  }
}