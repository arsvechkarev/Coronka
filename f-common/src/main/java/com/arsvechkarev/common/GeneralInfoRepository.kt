package com.arsvechkarev.common

import com.arsvechkarev.network.RxNetworker
import com.arsvechkarev.storage.Saver
import core.Loggable
import core.MAX_CACHE_MINUTES
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.log
import core.model.GeneralInfo
import datetime.DateTime
import io.reactivex.Maybe
import io.reactivex.Single
import org.json.JSONObject

class GeneralInfoRepository(
  private val networker: RxNetworker,
  private val saver: Saver,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : Loggable {
  
  override val logTag = "Request_GeneralInfoRepository"
  
  fun getGeneralInfo(): Single<GeneralInfo> {
    return Maybe.concat(getFromCache(), getFromNetwork())
        .firstElement()
        .toSingle()
  }
  
  private fun getFromCache(): Maybe<GeneralInfo> {
    return Maybe.create { emitter ->
      if (saver.isUpToDate(GENERAL_INFO_LAST_UPDATE_TIME, MAX_CACHE_MINUTES)) {
        val generalInfo = GeneralInfo(
          saver.getInt(CONFIRMED),
          saver.getInt(DEATHS),
          saver.getInt(RECOVERED)
        )
        log { "Successfully found general info in cache" }
        emitter.onSuccess(generalInfo)
      } else {
        log { "No general info in cache (or the data is out of date)" }
        emitter.onComplete()
      }
    }
  }
  
  private fun getFromNetwork(): Maybe<GeneralInfo> = networker.performRequest(URL)
      .subscribeOn(schedulersProvider.io())
      .map(::transformJson)
      .doOnSuccess(::loadToCache)
      .toMaybe()
  
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