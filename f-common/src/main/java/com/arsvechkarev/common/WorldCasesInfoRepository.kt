package com.arsvechkarev.common

import core.Networker
import core.model.DailyCase
import io.reactivex.Observable
import org.json.JSONArray

class WorldCasesInfoRepository(private val networker: Networker) {
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return networker.request(URL).map(::transformJson)
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
  
  companion object {
    
    const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}