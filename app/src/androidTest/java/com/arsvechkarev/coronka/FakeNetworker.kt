package com.arsvechkarev.coronka

import com.arsvechkarev.coronka.DataProvider.allCountriesData
import com.arsvechkarev.coronka.DataProvider.generalInfoData
import com.arsvechkarev.coronka.DataProvider.newsData
import com.arsvechkarev.coronka.DataProvider.worldCasesData
import core.Networker
import io.reactivex.Observable
import com.arsvechkarev.common.AllCountriesDataSource.Companion.URL as ALL_COUNTIES_URL
import com.arsvechkarev.common.GeneralInfoDataSource.Companion.URL as GENERAL_INFO_URL
import com.arsvechkarev.common.WorldCasesInfoRepository.Companion.URL as WORLD_CASES_URL

object FakeNetworker : Networker {
  
  override fun request(url: String): Observable<String> = when {
    url == ALL_COUNTIES_URL -> Observable.just(allCountriesData)
    url == WORLD_CASES_URL -> Observable.just(worldCasesData)
    url == GENERAL_INFO_URL -> Observable.just(generalInfoData)
    url.startsWith("https://api.nytimes.com") -> Observable.just(newsData)
    else -> throw IllegalStateException()
  }
}