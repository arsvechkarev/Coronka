package com.arsvechkarev.coronka

import com.arsvechkarev.coronka.DataProvider.allCountriesData
import com.arsvechkarev.coronka.DataProvider.generalInfoData
import com.arsvechkarev.coronka.DataProvider.newsData
import com.arsvechkarev.coronka.DataProvider.worldCasesData
import core.WebApi
import io.reactivex.Observable
import java.net.UnknownHostException
import core.datasources.GeneralInfoDataSourceImpl.Companion.URL as GENERAL_INFO_URL
import core.datasources.TotalInfoDataSourceImpl.Companion.URL as ALL_COUNTIES_URL
import core.datasources.WorldCasesInfoDataSource.Companion.URL as WORLD_CASES_URL

object FakeWebApi : WebApi, WebApi.Factory {
  
  override fun create(): WebApi = FakeWebApi
  
  override fun request(url: String): Observable<String> = when {
    url == ALL_COUNTIES_URL -> Observable.just(allCountriesData)
    url == WORLD_CASES_URL -> Observable.just(worldCasesData)
    url == GENERAL_INFO_URL -> Observable.just(generalInfoData)
    url.startsWith("https://api.nytimes.com") -> Observable.just(newsData)
    else -> throw IllegalStateException()
  }
}

class RetryCountWebApi(private val retryCount: Int) : WebApi {
  
  private var errors = 0
  
  override fun request(url: String): Observable<String> {
    if (errors < retryCount) {
      errors++
      return Observable.error(UnknownHostException())
    }
    return FakeWebApi.request(url)
  }
}

class RetryCountWebApiFactory(private val retryCount: Int) : WebApi.Factory {
  override fun create(): WebApi = RetryCountWebApi(retryCount)
}