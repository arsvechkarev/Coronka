package com.arsvechkarev.stats.di

import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSourceImpl
import core.WebApi
import core.di.Module
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface StatsModule : Module {
  
  val generalInfoDataSource: GeneralInfoDataSource
  
  val worldCasesInfoDataSource: WorldCasesInfoDataSource
}

class DefaultStatsModule(
  okHttpClient: OkHttpClient, webApi: WebApi,
) : StatsModule {
  
  override val generalInfoDataSource: GeneralInfoDataSource by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(GeneralInfoDataSource.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeneralInfoDataSource::class.java)
  }
  override val worldCasesInfoDataSource by lazy {
    WorldCasesInfoDataSourceImpl(webApi)
  }
}
