package com.arsvechkarev.news.di

import com.arsvechkarev.news.BuildConfig
import com.arsvechkarev.news.domain.DefaultNewsUseCase
import com.arsvechkarev.news.domain.NewsApi
import com.arsvechkarev.news.domain.NewsUseCase
import core.di.CoreComponent.dateTimeFormatter
import core.di.CoreComponent.gsonConverterFactory
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
        .baseUrl("https://api.nytimes.com/")
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(NewsApi::class.java)
  }
  
  override val newsUseCase = DefaultNewsUseCase(newYorkTimesApi, BuildConfig.NYT_API_KEY,
    NewsItemsMapper(dateTimeFormatter))
}