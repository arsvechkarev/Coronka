package com.arsvechkarev.common

import com.arsvechkarev.storage.Saver
import core.DateTime
import core.Loggable
import core.MAX_CACHE_MINUTES
import core.RxNetworker
import core.log
import core.model.GeneralInfo
import io.reactivex.Observable
import org.json.JSONObject

class GeneralInfoRepository(
  private val networker: RxNetworker,
  private val saver: Saver
) : Loggable {
  
  override val logTag = "Request_GeneralInfoRepository"
  
  fun getGeneralInfo(): Observable<GeneralInfo> {
    return getFromNetwork()
  }
  
  private fun getFromCache(): Observable<GeneralInfo> {
    return Observable.create { emitter ->
      if (saver.isUpToDate(GENERAL_INFO_LAST_UPDATE_TIME, MAX_CACHE_MINUTES)) {
        val generalInfo = GeneralInfo(
          saver.getInt(CONFIRMED),
          saver.getInt(DEATHS),
          saver.getInt(RECOVERED)
        )
        log { "Successfully found general info in cache" }
        emitter.onNext(generalInfo)
      }
      emitter.onComplete()
    }
  }
  
  private fun getFromNetwork(): Observable<GeneralInfo> {
    return networker.requestObservable(URL)
        .map(::transformJson)
        .doOnNext(::loadToCache)
  }
  
  private fun transformJson(json: String): GeneralInfo {
    val jsonObject = JSONObject(json)
    return GeneralInfo(
      jsonObject.get(CONFIRMED).toString().toInt(),
      jsonObject.get(DEATHS).toString().toInt(),
      jsonObject.get(RECOVERED).toString().toInt()
    )
  }
  
  private fun loadToCache(generalInfo: GeneralInfo) {
    saver.execute {
      putLong(GENERAL_INFO_LAST_UPDATE_TIME, DateTime.current().millis)
      putInt(CONFIRMED, generalInfo.confirmed)
      putInt(DEATHS, generalInfo.deaths)
      putInt(RECOVERED, generalInfo.recovered)
    }
  }
  
  companion object {
    
    const val SAVER_FILENAME = "GeneralInfoRepository"
    
    private const val GENERAL_INFO_LAST_UPDATE_TIME = "generalInfoLastUpdateTime"
    private const val CONFIRMED = "cases"
    private const val RECOVERED = "recovered"
    private const val DEATHS = "deaths"
    private const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}