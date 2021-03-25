package com.arsvechkarev.common.domain

import core.model.data.WorldCasesInfo
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Data source for retrieving world cases info
 */
fun interface WorldCasesInfoDataSource {
  
  @GET("/arsvechkarev/coronavirus-data/main/daily_cases.json")
  fun requestWorldDailyCases(): Single<WorldCasesInfo>
  
  companion object {
    
    /** Base url for retrofit */
    const val BASE_URL = "https://raw.githubusercontent.com/"
  }
}