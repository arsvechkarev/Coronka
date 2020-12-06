package com.arsvechkarev.common

import core.RxNetworker
import core.model.GeneralInfo
import io.reactivex.Observable
import org.json.JSONObject

class GeneralInfoRepository(private val networker: RxNetworker) {
  
  fun getGeneralInfo(): Observable<GeneralInfo> {
    return networker.requestObservable(URL)
        .map<GeneralInfo>(::transformJson)
  }
  
  private fun transformJson(json: String): GeneralInfo {
    val jsonObject = JSONObject(json)
    return GeneralInfo(
      jsonObject.get(CONFIRMED).toString().toInt(),
      jsonObject.get(DEATHS).toString().toInt(),
      jsonObject.get(RECOVERED).toString().toInt()
    )
  }
  
  companion object {
    
    private const val CONFIRMED = "cases"
    private const val RECOVERED = "recovered"
    private const val DEATHS = "deaths"
    private const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}