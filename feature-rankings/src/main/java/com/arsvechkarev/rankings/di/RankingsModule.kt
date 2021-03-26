package com.arsvechkarev.rankings.di

import com.arsvechkarev.featurescommon.di.CommonFeaturesComponent.countriesDataSource
import com.arsvechkarev.featurescommon.di.CommonFeaturesComponent.countriesInformationDatabase
import com.arsvechkarev.rankings.domain.CountriesFilterer
import com.arsvechkarev.rankings.domain.DatabaseCountriesMetaInfoDataSource
import com.arsvechkarev.rankings.domain.RankingsInteractor
import core.di.CoreComponent
import core.di.Module
import core.model.mappers.CountryEntitiesToCountriesMapper

interface RankingsModule : Module {
  
  val rankingsInteractor: RankingsInteractor
}

object DefaultRankingsModule : RankingsModule {
  
  override val rankingsInteractor: RankingsInteractor
    get() = RankingsInteractor(
      countriesDataSource, DatabaseCountriesMetaInfoDataSource(countriesInformationDatabase),
      CountriesFilterer(), CountryEntitiesToCountriesMapper(),
      CoreComponent.schedulers
    )
}