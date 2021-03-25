package com.arsvechkarev.rankings.di

import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesDataSource
import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesMetaInfoRepository
import com.arsvechkarev.rankings.domain.CountriesFiltererImpl
import com.arsvechkarev.rankings.domain.DefaultRankingsInteractor
import com.arsvechkarev.rankings.domain.RankingsInteractor
import core.di.CoreComponent
import core.di.Module
import core.model.mappers.CountryEntitiesToCountriesMapper

interface RankingsModule : Module {
  
  val rankingsInteractor: RankingsInteractor
}

object DefaultRankingsModule : RankingsModule {
  
  override val rankingsInteractor = DefaultRankingsInteractor(
    countriesDataSource, countriesMetaInfoRepository,
    CountriesFiltererImpl(), CountryEntitiesToCountriesMapper(),
    CoreComponent.schedulers
  )
}