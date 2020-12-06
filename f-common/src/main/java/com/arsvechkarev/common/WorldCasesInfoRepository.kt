package com.arsvechkarev.common

import core.RxNetworker
import core.extenstions.assertThat
import core.model.DailyCase
import io.reactivex.Observable
import org.json.JSONObject

class WorldCasesInfoRepository(private val networker: RxNetworker) {
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return networker.requestObservable(URL_TOTAL_CASES)
        .map { transformJson(it) }
  }
  
  private fun transformJson(json: String): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    val outerObject = JSONObject(json)
    val graphObject = outerObject.getJSONObject("graph")
    val casesArray = graphObject.getJSONArray("data")
    val dateArray = graphObject.getJSONArray("categories")
    assertThat(casesArray.length() == dateArray.length())
    val start = maxOf(casesArray.length() - MAX_CASES, 0)
    for (i in start until casesArray.length()) {
      val cases = casesArray.optInt(i)
      val date = dateArray.getString(i)
      val dailyCase = DailyCase(cases, date)
      dailyCases.add(dailyCase)
    }
    return dailyCases
  }
  
  companion object {
  
    private const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
    private const val URL_TOTAL_CASES = "https://covid19-update-api.herokuapp.com/api/v1/cases/graphs/totalCases"
  }
}