package com.arsvechkarev.map.repository

import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig
import core.ResultHandler
import core.model.GeneralInfo
import org.json.JSONObject

class GeneralInfoRepository(
  private val threader: ApplicationConfig.Threader,
  private val networker: Networker,
  private val saver: Saver
) {
  
  fun getGeneralInfo(allowCache: Boolean = false, resultHandler: ResultHandler<GeneralInfo, Throwable>) {
    if (allowCache && saver.has(CONFIRMED)) {
      resultHandler.onSuccess(constructGeneralInfo())
      return
    }
    performNetworkRequest(resultHandler)
  }
  
  private fun constructGeneralInfo(): GeneralInfo {
    return GeneralInfo(
      saver.get(CONFIRMED).toInt(),
      saver.get(RECOVERED).toInt(),
      saver.get(DEATHS).toInt()
    )
  }
  
  private fun performNetworkRequest(resultHandler: ResultHandler<GeneralInfo, Throwable>) {
    try {
      threader.ioWorker.submit {
        val result = networker.syncRequest(URL)
        val json = JSONObject(result)
        val generalInfo = GeneralInfo(
          json.get(CONFIRMED).toString().toInt(),
          json.get(RECOVERED).toString().toInt(),
          json.get(DEATHS).toString().toInt()
        )
        threader.mainThreadWorker.submit {
          resultHandler.onSuccess(generalInfo)
        }
      }
    } catch (throwable: Throwable) {
      resultHandler.onFailure(throwable)
    }
  }
  
  companion object {
    const val SAVER_FILENAME = "GeneralInfo"
    
    private const val CONFIRMED = "confirmed"
    private const val RECOVERED = "recovered"
    private const val DEATHS = "deaths"
  
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/brief"
  }
}