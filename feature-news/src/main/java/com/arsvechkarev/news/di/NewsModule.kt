package com.arsvechkarev.news.di

import com.arsvechkarev.news.BuildConfig
import com.arsvechkarev.news.domain.DefaultNewsUseCase
import com.arsvechkarev.news.domain.NewDataSource
import com.arsvechkarev.news.domain.NewsJsonConverter
import com.arsvechkarev.news.domain.NewsRetrofitConverterFactory
import com.arsvechkarev.news.domain.NewsUseCase
import core.di.CoreComponent.dateTimeFormatter
import core.di.CoreComponent.okHttpClient
import core.di.CoreComponent.rxJava2CallAdapterFactory
import core.di.Module
import core.model.mappers.NewsItemsMapper
import retrofit2.Retrofit

interface NewsModule : Module {
  
  val newsUseCase: NewsUseCase
}

object DefaultNewsModule : NewsModule {
  
  private val newYorkTimesApi by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(NewDataSource.BASE_URL)
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(NewsRetrofitConverterFactory(NewsJsonConverter()))
        .build()
        .create(NewDataSource::class.java)
  }
  
  override val newsUseCase = DefaultNewsUseCase(newYorkTimesApi, BuildConfig.NYT_API_KEY,
    NewsItemsMapper(dateTimeFormatter))
}