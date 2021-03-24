package com.arsvechkarev.stats.di

import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSource
import com.arsvechkarev.stats.domain.DefaultStatsUseCase
import com.arsvechkarev.stats.domain.StatsUseCase
import com.arsvechkarev.stats.domain.WorldInfoJsonConverter
import com.arsvechkarev.stats.domain.WorldsCasesRetrofitConverterFactory
import core.Schedulers
import core.di.Module
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface StatsModule : Module {
  
  val statsUseCase: StatsUseCase
}

class DefaultStatsModule(
  rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
  gsonConverterFactory: GsonConverterFactory,
  okHttpClient: OkHttpClient,
  schedulers: Schedulers
) : StatsModule {
  
  private val generalInfoDataSource: GeneralInfoDataSource by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(GeneralInfoDataSource.BASE_URL)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(GeneralInfoDataSource::class.java)
  }
  
  private val worldCasesInfoDataSource by lazy {
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