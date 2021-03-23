package com.arsvechkarev.news.di

import com.arsvechkarev.news.domain.NewYorkTimesNewsDataRepositoryImpl
import com.arsvechkarev.news.domain.NewYorkTimesNewsRepository
import core.DateTimeFormatter
import core.WebApi
import core.di.Module

interface NewsModule : Module {
  
  val newYorkTimesNewsRepository: NewYorkTimesNewsRepository
}

class DefaultNewsModule(
  private val webApi: WebApi,
  private val dateTimeFormatter: DateTimeFormatter,
  private val newYorkTimesApi: String
) : NewsModule {
  
  override val newYorkTimesNewsRepository: NewYorkTimesNewsRepository
    get() = NewYorkTimesNewsDataRepositoryImpl(webApi, dateTimeFormatter, newYorkTimesApi)
}