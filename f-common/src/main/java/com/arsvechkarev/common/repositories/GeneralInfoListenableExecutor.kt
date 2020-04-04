package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.model.GeneralInfo
import org.json.JSONObject

class GeneralInfoListenableExecutor(
  private val networker: Networker,
  private val saver: Saver
) : BaseListenableExecutor<GeneralInfo>() {
  
  override fun performCacheRequest(): GeneralInfo? {
    if (saver.has(CONFIRMED)) {
      return constructGeneralInfo()
    }
    return null
  }
  
  override fun performNetworkRequest(): GeneralInfo {
    val result = networker.performRequest(URL)
    val json = JSONObject(result)
    return GeneralInfo(
      json.get(CONFIRMED).toString().toInt(),
      json.get(DEATHS).toString().toInt(),
      json.get(RECOVERED).toString().toInt()
    )
  }
  
  override fun loadToCache(result: GeneralInfo) {
    saver.execute {
      putInt(CONFIRMED, result.confirmed)
      putInt(DEATHS, result.deaths)
      putInt(RECOVERED, result.recovered)
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
    const val SAVER_FILENAME = "GeneralInfoExecutor"
    
    private const val CONFIRMED = "confirmed"
    private const val RECOVERED = "recovered"
    
    private const val DEATHS = "deaths"
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/brief"
    
  }
}