package com.arsvechkarev.coronka.fakeapi

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.coronka.readAssetsFile
import core.datetime.EnglishTimeFormatter
import core.jsontransformers.AllCountriesTransformer
import core.jsontransformers.GeneralInfoTransformer
import core.jsontransformers.NewsTransformer
import core.jsontransformers.WorldCasesInfoTransformer
import core.model.DailyCase
import core.model.GeneralInfo
import core.model.NewsItemWithPicture
import core.model.TotalData

object DataProvider {
  
  private val context = InstrumentationRegistry.getInstrumentation().context
  
  val allCountriesData by lazy { context.readAssetsFile("all_countries_data.json") }
  val worldCasesData by lazy { context.readAssetsFile("world_cases_data.json") }
  val newsData by lazy { context.readAssetsFile("news_data.json") }
  val generalInfoData by lazy { context.readAssetsFile("general_info_data.json") }
  
  fun getTotalData(): TotalData {
    return AllCountriesTransformer.toTotalData(allCountriesData)
  }
  
  fun getDailyCases(): List<DailyCase> {
    return WorldCasesInfoTransformer.toDailyCases(worldCasesData)
  }
  
  fun getGeneralInfo(): GeneralInfo {
    return GeneralInfoTransformer.toGeneralInfo(generalInfoData)
  }
  
  fun getNews(): List<NewsItemWithPicture> {
    return NewsTransformer.toNewsItems(EnglishTimeFormatter, newsData)
  }
}