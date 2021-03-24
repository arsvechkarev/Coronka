package com.arsvechkarev.news.di

import com.arsvechkarev.news.domain.DefaultNewsUseCase
import com.arsvechkarev.news.domain.NewsApi
import com.arsvechkarev.news.domain.NewsRetrofitConverterFactory
import com.arsvechkarev.news.domain.NewsUseCase
import core.di.Module
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit

interface NewsModule : Module {
  
  val newsUseCase: NewsUseCase
}

class DefaultNewsModule(
  okHttpClient: OkHttpClient,
  rxJava2CallAdapterFactory: CallAdapter.Factory,
  converterFactory: NewsRetrofitConverterFactory,
  newYorkTimesApiKey: String
) : NewsModule {
  
  private val newYorkTimesApi by lazy {
    Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.nytimes.com/")
        .addCallAdapterFactory(rxJava2CallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build()
        .create(NewsApi::class.java)
  }
  
  override val newsUseCase = DefaultNewsUseCase(newYorkTimesApi, newYorkTimesApiKey)
}