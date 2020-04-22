package com.arsvechkarev.common.executors

import com.arsvechkarev.common.TimedData
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.model.GeneralInfo
import datetime.DateTime
import org.json.JSONObject

class GeneralInfoListenableExecutor(
  private val networker: Networker,
  private val saver: Saver
) : BaseListenableExecutor<TimedData<GeneralInfo>>() {
  
  override fun performCacheRequest(): TimedData<GeneralInfo>? {
    if (saver.has(GENERAL_INFO_LAST_UPDATE_TIME)) {
      val generalInfo = constructGeneralInfo()
      val lastUpdateTime = DateTime.ofString(saver.getString(GENERAL_INFO_LAST_UPDATE_TIME))
      return TimedData(generalInfo, lastUpdateTime)
    }
    return null
  }
  
  override fun performNetworkRequest(): TimedData<GeneralInfo> {
    val result = networker.performRequest(URL)
    val json = JSONObject(result)
    val generalInfo = GeneralInfo(
      json.get(CONFIRMED).toString().toInt(),
      json.get(DEATHS).toString().toInt(),
      json.get(RECOVERED).toString().toInt()
    )
    return TimedData(generalInfo, DateTime.current())
  }
  
  override fun loadToCache(result: TimedData<GeneralInfo>) {
    threader.ioWorker.submit {
      val generalInfo = result.data
      saver.execute(synchronosly = true) {
        putString(GENERAL_INFO_LAST_UPDATE_TIME, result.lastUpdateTime.toString())
        putInt(CONFIRMED, generalInfo.confirmed)
        putInt(DEATHS, generalInfo.deaths)
        putInt(RECOVERED, generalInfo.recovered)
      }
    }
  }
  
  private fun constructGeneralInfo(): GeneralInfo {
    return GeneralInfo(
      saver.getInt(CONFIRMED),
      saver.getInt(DEATHS),
      saver.getInt(RECOVERED)
    )
  }
  
  companion object {
    const val SAVER_FILENAME = "GeneralInfoListenableExecutor"
    
    private const val GENERAL_INFO_LAST_UPDATE_TIME = "generalInfoLastUpdate"
    private const val CONFIRMED = "confirmed"
    private const val RECOVERED = "recovered"
    private const val DEATHS = "deaths"
    
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/brief"
  }
}