package com.arsvechkarev.coronka.fakeapi

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.coronka.readAssetsFile
import core.Networker
import io.reactivex.Observable
import com.arsvechkarev.common.AllCountriesRepository.Companion.URL as ALL_COUNTIES_URL
import com.arsvechkarev.common.GeneralInfoRepository.Companion.URL as GENERAL_INFO_URL
import com.arsvechkarev.common.WorldCasesInfoRepository.Companion.URL as WORLD_CASES_URL

object FakeAlwaysSuccessNetworker : Networker {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  private val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  private val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  private val newsData by lazy { context.readAssetsFile("news_data.json") }
  private val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  override fun request(url: String): Observable<String> = when {
    url == ALL_COUNTIES_URL -> Observable.just(allCountriesData)
    url == WORLD_CASES_URL -> Observable.just(worldCasesData)
    url == GENERAL_INFO_URL -> Observable.just(generalInfoData)
    url.startsWith("https://api.nytimes.com") -> Observable.just(newsData)
    else -> throw IllegalStateException()
  }
  
  fun getStringByUrl(url: String) = when {
    url == ALL_COUNTIES_URL -> allCountriesData
    url == WORLD_CASES_URL -> worldCasesData
    url == GENERAL_INFO_URL -> generalInfoData
    url.startsWith("https://api.nytimes.com") -> newsData
    else -> throw IllegalStateException()
  }
}