package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.CountriesDataSource
import com.arsvechkarev.common.repository.CountriesMetaInfoRepository

interface CommonFeaturesModule {
  
  val countriesDataSource: CountriesDataSource
  
  val countriesMetaInfoRepository: CountriesMetaInfoRepository
}