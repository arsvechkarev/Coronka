package com.arsvechkarev.coronka.tests.rankings

import com.arsvechkarev.common.di.CommonFeaturesComponent
import com.arsvechkarev.common.domain.CountriesDataSource
import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.rankings.di.RankingsModule
import com.arsvechkarev.rankings.domain.CountriesFilterer
import com.arsvechkarev.rankings.domain.DatabaseCountriesMetaInfoDataSource
import com.arsvechkarev.rankings.domain.RankingsInteractor
import core.model.mappers.CountryEntitiesToCountriesMapper
import coreimpl.AndroidSchedulers
import io.reactivex.Single

object FakeRankingsModule : RankingsModule {
  
  private val countriesDataSource = CountriesDataSource {
    Single.just(DataProvider.getCountriesWrapper())
  }
  
  private val countriesMetaInfoDataSource = DatabaseCountriesMetaInfoDataSource(
    CommonFeaturesComponent.countriesInformationDatabase)
  
  override val rankingsInteractor: RankingsInteractor = RankingsInteractor(
    countriesDataSource,
    countriesMetaInfoDataSource, CountriesFilterer(),
    CountryEntitiesToCountriesMapper(), AndroidSchedulers
  )
}
