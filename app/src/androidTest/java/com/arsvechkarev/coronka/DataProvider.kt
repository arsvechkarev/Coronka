package com.arsvechkarev.coronka

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.common.domain.transformers.AllCountriesTransformer
import com.arsvechkarev.news.domain.NewsJsonConverter
import com.google.gson.Gson
import core.model.DailyCase
import core.model.GeneralInfo
import core.model.MainStatistics
import core.model.NewsItemWithPicture
import core.model.TotalInfo
import coreimpl.EnglishDateTimeFormatter
import coreimpl.ThreeTenAbpDateTimeCreator

object DataProvider {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  
  private val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  private val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  private val newsData by lazy { context.readAssetsFile("news_data.json") }
  private val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  fun getWorldCasesInfo(): MainStatistics {
    val newDailyCases = WorldCasesInfoTransformer.toNewDailyCases(getDailyCases())
    return MainStatistics(getGeneralInfo(), getDailyCases(), newDailyCases)
  }
  
  fun getTotalInfo(): TotalInfo {
    return AllCountriesTransformer.toTotalData(allCountriesData)
  }
  
  fun getDailyCases(): List<DailyCase> {
    return WorldCasesInfoTransformer.toDailyCases(worldCasesData)
  }
  
  fun getGeneralInfo(): GeneralInfo {
    return Gson().fromJson<GeneralInfo>(generalInfoData, GeneralInfo::class.java)
  }
  
  fun getNews(): List<NewsItemWithPicture> {
    val dateTimeFormatter = EnglishDateTimeFormatter(context, ThreeTenAbpDateTimeCreator)
    val newsJsonConverter = NewsJsonConverter(dateTimeFormatter)
    return newsJsonConverter.convert(newsData)
  }
}