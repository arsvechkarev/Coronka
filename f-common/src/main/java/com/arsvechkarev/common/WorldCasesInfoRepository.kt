package com.arsvechkarev.common

import core.Loggable
import core.RxNetworker
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.extenstions.assertThat
import core.model.DailyCase
import io.reactivex.Observable
import org.json.JSONObject

class WorldCasesInfoRepository(
  private val networker: RxNetworker,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : Loggable {
  
  override val logTag = "Request_WorldCasesRepository"
  
  fun getWorldDailyTotalCases(): Observable<List<DailyCase>> {
    return Observable.concat(getTotalCasesFromCache(), getTotalCasesFromNetwork())
        .subscribeOn(schedulersProvider.io())
        .firstElement()
        .toObservable()
        .share()
        .observeOn(schedulersProvider.mainThread())
  }
  
  private fun getTotalCasesFromCache(): Observable<List<DailyCase>> = Observable.create { emitter ->
    emitter.onComplete()
  }
  
  private fun getTotalCasesFromNetwork(): Observable<List<DailyCase>> {
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
    for (i in 0 until casesArray.length()) {
      val cases = casesArray.optInt(i)
      val date = dateArray.getString(i)
      val dailyCase = DailyCase(cases, date)
      dailyCases.add(dailyCase)
    }
    return dailyCases
  }
  
  companion object {
    private const val DAILY_CASES_LAST_UPDATE_TIME = "dailyCasesLastUpdateTime"
    private const val URL_TOTAL_CASES = "https://covid19-update-api.herokuapp.com/api/v1/cases/graphs/totalCases"
  }
}