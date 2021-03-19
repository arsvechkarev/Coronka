package com.arsvechkarev.coronka

import com.arsvechkarev.coronka.DataProvider.allCountriesData
import com.arsvechkarev.coronka.DataProvider.generalInfoData
import com.arsvechkarev.coronka.DataProvider.newsData
import com.arsvechkarev.coronka.DataProvider.worldCasesData
import core.WebApi
import io.reactivex.Single
import java.net.UnknownHostException
import core.datasources.GeneralInfoDataSource.Companion.URL as GENERAL_INFO_URL
import core.datasources.TotalInfoDataSource.Companion.URL as ALL_COUNTIES_URL
import core.datasources.WorldCasesInfoDataSource.Companion.URL as WORLD_CASES_URL

object FakeWebApi : WebApi, WebApi.Factory {
  
  override fun create(): WebApi = FakeWebApi
  
  override fun request(url: String): Single<String> = when {
    url == ALL_COUNTIES_URL -> Single.just(allCountriesData)
    url == WORLD_CASES_URL -> Single.just(worldCasesData)
    url == GENERAL_INFO_URL -> Single.just(generalInfoData)
    url.startsWith("https://api.nytimes.com") -> Single.just(newsData)
    else -> throw IllegalStateException()
  }
}

class RetryCountWebApi(private val retryCount: Int) : WebApi {
  
  private var errors = 0
  
  override fun request(url: String): Single<String> {
    if (errors < retryCount) {
      errors++
      return Single.error(UnknownHostException())
    }
    return FakeWebApi.request(url)
  }
}

class RetryCountWebApiFactory(private val retryCount: Int) : WebApi.Factory {
  override fun create(): WebApi = RetryCountWebApi(retryCount)
}