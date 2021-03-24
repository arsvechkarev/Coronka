package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.TotalInfoDataSource
import com.arsvechkarev.common.domain.TotalInfoDataSourceImpl
import com.arsvechkarev.common.repository.CountriesMetaInfoRepository
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl.Companion.DATABASE_NAME
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl.Companion.DATABASE_VERSION
import core.di.CoreModule

class CommonFeaturesModuleImpl(
  coreModule: CoreModule
) : CommonFeaturesModule {
  
  override val totalInfoDataSource: TotalInfoDataSource =
      TotalInfoDataSourceImpl(coreModule.webApi)
  
  override val countriesMetaInfoRepository: CountriesMetaInfoRepository by lazy {
    val database = coreModule.databaseCreator.provideDatabase(DATABASE_NAME, DATABASE_VERSION)
    CountriesMetaInfoRepositoryImpl(database)
  }
}