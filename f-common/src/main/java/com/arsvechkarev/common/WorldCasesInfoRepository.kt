package com.arsvechkarev.common

import core.Networker
import core.jsontransformers.WorldCasesInfoTransformer
import core.model.DailyCase
import io.reactivex.Observable

class WorldCasesInfoRepository(private val networker: Networker) {
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return networker.request(URL).map(WorldCasesInfoTransformer::toDailyCases)
  }
  
  companion object {
    
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}