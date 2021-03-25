package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.CountriesDataSource
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl.Companion.DATABASE_NAME
import com.arsvechkarev.common.repository.CountriesMetaInfoRepositoryImpl.Companion.DATABASE_VERSION
import core.di.CoreModule
import retrofit2.Retrofit

class CommonFeaturesModuleImpl(
  coreModule: CoreModule
) : CommonFeaturesModule {
  
  override val countriesDataSource: CountriesDataSource by lazy {
    Retrofit.Builder()
        .client(coreModule.okHttpClient)
        .baseUrl(CountriesDataSource.BASE_URL)
        .addConverterFactory(coreModule.gsonConverterFactory)
        .addCallAdapterFactory(coreModule.rxJava2CallAdapterFactory)
        .build()
        .create(CountriesDataSource::class.java)
  }
  
  override val countriesMetaInfoRepository by lazy {
    val database = coreModule.databaseCreator.provideDatabase(DATABASE_NAME, DATABASE_VERSION)
    CountriesMetaInfoRepositoryImpl(database)
  }
}