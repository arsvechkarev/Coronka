package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig
import core.model.GeneralInfo
import org.json.JSONObject

class GeneralInfoExecutor(
  private val threader: ApplicationConfig.Threader,
  private val networker: Networker,
  private val saver: Saver
) {
  
  fun tryUpdateFromCache(onSuccess: (GeneralInfo) -> Unit) {
    if (saver.has(CONFIRMED)) {
      onSuccess(constructGeneralInfo())
    }
  }
  
  fun getGeneralInfo(
    onSuccess: (GeneralInfo) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    threader.ioWorker.submit {
      performNetworkRequest(onSuccess = {
        saveInfo(it)
        onSuccess(it)
      }, onFailure = onFailure)
    }
  }
  
  private fun performNetworkRequest(
    onSuccess: (GeneralInfo) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    try {
      val result = networker.performRequest(URL)
      val json = JSONObject(result)
      val generalInfo = GeneralInfo(
        json.get(CONFIRMED).toString().toInt(),
        json.get(DEATHS).toString().toInt(),
        json.get(RECOVERED).toString().toInt()
      )
      threader.mainThreadWorker.submit { onSuccess(generalInfo) }
    } catch (throwable: Throwable) {
      threader.mainThreadWorker.submit { onFailure(throwable) }
    }
  }
  
  private fun constructGeneralInfo(): GeneralInfo {
    return GeneralInfo(
      saver.getInt(CONFIRMED),
      saver.getInt(DEATHS),
      saver.getInt(RECOVERED)
    )
  }
  
  private fun saveInfo(generalInfo: GeneralInfo) {
    saver.execute {
      putInt(CONFIRMED, generalInfo.confirmed)
      putInt(DEATHS, generalInfo.deaths)
      putInt(RECOVERED, generalInfo.recovered)
    }
  }
  
  companion object {
    
    const val SAVER_FILENAME = "GeneralInfoExecutor"
    
    private const val CONFIRMED = "confirmed"
    private const val RECOVERED = "recovered"
    private const val DEATHS = "deaths"
    
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/brief"
  }
}