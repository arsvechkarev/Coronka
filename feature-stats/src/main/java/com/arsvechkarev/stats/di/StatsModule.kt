package com.arsvechkarev.stats.di

import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSource
import com.arsvechkarev.stats.domain.DefaultStatsUseCase
import com.arsvechkarev.stats.domain.StatsUseCase
import com.arsvechkarev.stats.domain.WorldInfoJsonConverter
import com.arsvechkarev.stats.domain.WorldsCasesRetrofitConverterFactory
import core.di.CoreComponent.gsonConverterFactory
import core.di.CoreComponent.okHttpClient
import core.di.CoreComponent.rxJava2CallAdapterFactory
import core.di.CoreComponent.schedulers
import core.di.Module
import retrofit2.Retrofit

interface StatsModule : Module {
  
  val statsUseCase: StatsUseCase
}

object DefaultStatsModule : StatsModule {
  
  private val generalInfoDataSource: GeneralInfoDataSource by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(GeneralInfoDataSource.BASE_URL)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(GeneralInfoDataSource::class.java)
  }
  
  private val worldCasesInfoDataSource: WorldCasesInfoDataSource by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(WorldCasesInfoDataSource.BASE_URL)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(WorldsCasesRetrofitConverterFactory(WorldInfoJsonConverter()))
        .build()
        .create(WorldCasesInfoDataSource::class.java)
  }
  
  override val statsUseCase by lazy {
    DefaultStatsUseCase(generalInfoDataSource, worldCasesInfoDataSource, schedulers)
  }
}