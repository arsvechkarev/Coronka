package com.arsvechkarev.coronka

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.news.domain.NewsJsonConverter
import com.arsvechkarev.stats.domain.WorldInfoJsonConverter
import com.google.gson.Gson
import core.model.data.CountriesWrapper
import core.model.data.GeneralInfo
import core.model.data.NewsItem
import core.model.data.WorldCasesInfo
import core.model.domain.Country
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.DailyCase

object DataProvider {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  
  private val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  private val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  private val newsData by lazy { context.readAssetsFile("news_data.json") }
  private val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  fun getWorldCasesInfo(): WorldCasesInfo {
    return WorldInfoJsonConverter().convert(worldCasesData)
  }
  
  fun getCountriesWrapper(): CountriesWrapper {
    return Gson().fromJson<CountriesWrapper>(allCountriesData,
      CountriesWrapper::class.java)
  }
  
  fun getAllCountries(): List<Country> {
    return CountryEntitiesToCountriesMapper().map(getCountriesWrapper().countries)
  }
  
  fun getDailyCases(): List<DailyCase> {
    return WorldInfoJsonConverter().convert(worldCasesData).totalDailyCases
  }
  
  fun getNewCases(): List<DailyCase> {
    return WorldInfoJsonConverter().convert(worldCasesData).newDailyCases
  }
  
  fun getGeneralInfo(): GeneralInfo {
    return Gson().fromJson<GeneralInfo>(generalInfoData, GeneralInfo::class.java)
  }
  
  fun getNews(): List<NewsItem> {
    val newsJsonConverter = NewsJsonConverter()
    return newsJsonConverter.convert(newsData)
  }
}