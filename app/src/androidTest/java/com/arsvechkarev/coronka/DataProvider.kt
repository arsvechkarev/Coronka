package com.arsvechkarev.coronka

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.news.domain.NewsJsonConverter
import com.arsvechkarev.stats.domain.WorldInfoJsonConverter
import com.google.gson.Gson
import core.model.data.CountriesWrapper
import core.model.data.GeneralInfo
import core.model.data.MainStatistics
import core.model.data.NewsItem
import core.model.domain.Country
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.DailyCase

object DataProvider {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  
  private val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  private val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  private val newsData by lazy { context.readAssetsFile("news_data.json") }
  private val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  fun getMainStatistics(): MainStatistics {
    val worldCasesInfo = WorldInfoJsonConverter().convert(worldCasesData)
    return MainStatistics(getGeneralInfo(), worldCasesInfo)
  }
  
  fun getAllCountriesInfo(): List<Country> {
    val countriesWrapper = Gson().fromJson<CountriesWrapper>(allCountriesData,
      CountriesWrapper::class.java)
    return CountryEntitiesToCountriesMapper().map(countriesWrapper.countries)
  }
  
  fun getDailyCases(): List<DailyCase> {
    return WorldInfoJsonConverter().convert(worldCasesData).totalDailyCases
  }
  
  fun getGeneralInfo(): GeneralInfo {
    return Gson().fromJson<GeneralInfo>(generalInfoData, GeneralInfo::class.java)
  }
  
  fun getNews(): List<NewsItem> {
    val newsJsonConverter = NewsJsonConverter()
    return newsJsonConverter.convert(newsData)
  }
}