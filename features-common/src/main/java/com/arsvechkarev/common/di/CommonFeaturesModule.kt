package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.CountriesDataSource
import core.Database

interface CommonFeaturesModule {
  
  val countriesInformationDatabase: Database
  
  val countriesDataSource: CountriesDataSource
}