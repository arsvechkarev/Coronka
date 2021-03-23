package com.arsvechkarev.coronka

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.common.domain.transformers.AllCountriesTransformer
import com.arsvechkarev.common.domain.transformers.WorldCasesInfoTransformer
import com.arsvechkarev.news.domain.NewsTransformer
import com.google.gson.Gson
import core.model.DailyCase
import core.model.GeneralInfo
import core.model.NewsItemWithPicture
import core.model.TotalInfo
import coreimpl.EnglishDateTimeFormatter
import coreimpl.ThreeTenAbpDateTimeCreator

object DataProvider {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  
  val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  val newsData by lazy { context.readAssetsFile("news_data.json") }
  val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  fun getTotalData(): TotalInfo {
    return AllCountriesTransformer.toTotalData(allCountriesData)
  }
  
  fun getDailyCases(): List<DailyCase> {
    return WorldCasesInfoTransformer.toDailyCases(worldCasesData)
  }
  
  fun getGeneralInfo(): GeneralInfo {
    return Gson().fromJson<GeneralInfo>(generalInfoData, GeneralInfo::class.java)
  }
  
  fun getNews(): List<NewsItemWithPicture> {
    return NewsTransformer.toNewsItems(
      EnglishDateTimeFormatter(context, ThreeTenAbpDateTimeCreator), newsData)
  }
}