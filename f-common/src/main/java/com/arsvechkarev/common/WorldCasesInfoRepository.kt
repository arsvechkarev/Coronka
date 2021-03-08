package com.arsvechkarev.common

import core.WebApi
import core.jsontransformers.WorldCasesInfoTransformer
import core.model.DailyCase
import io.reactivex.Observable

class WorldCasesInfoRepository(private val webApi: WebApi) {
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return webApi.request(URL).map(WorldCasesInfoTransformer::toDailyCases)
  }
  
  companion object {
    
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}