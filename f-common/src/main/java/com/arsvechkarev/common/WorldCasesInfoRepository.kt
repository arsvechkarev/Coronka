package com.arsvechkarev.common

import core.RxNetworker
import core.model.DailyCase
import io.reactivex.Observable
import org.json.JSONArray

class WorldCasesInfoRepository(private val networker: RxNetworker) {
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return networker.requestObservable(URL_TOTAL_CASES)
        .map { transformJson(it) }
  }
  
  private fun transformJson(json: String): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    val array = JSONArray(json)
    for (i in array.length() - MAX_CASES until array.length()) {
      val obj = array.getJSONObject(i)
      val cases = obj.getInt("Confirmed")
      val date = obj.getString("Date")
      val dailyCase = DailyCase(cases, date)
      dailyCases.add(dailyCase)
    }
    return dailyCases
  }
  
  private companion object {
    
    const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
    const val URL_TOTAL_CASES = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}