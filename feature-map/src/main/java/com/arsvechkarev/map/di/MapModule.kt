package com.arsvechkarev.map.di

import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesDataSource
import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesMetaInfoRepository
import com.arsvechkarev.map.domain.DefaultMapInteractor
import com.arsvechkarev.map.domain.MapInteractor
import core.di.CoreComponent.schedulers
import core.di.Module
import core.model.mappers.CountryEntitiesToCountriesMapper

interface MapModule : Module {
  
  val mapInteractor: MapInteractor
}

object DefaultMapModule : MapModule {
  
  override val mapInteractor: DefaultMapInteractor
    get() = DefaultMapInteractor(
      countriesDataSource, countriesMetaInfoRepository,
      CountryEntitiesToCountriesMapper(), schedulers
    )
}