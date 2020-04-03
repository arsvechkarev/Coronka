package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.Application
import core.handlers.ResultAction
import core.handlers.ResultHandler
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createResultHandler
import core.handlers.createSuccessHandler
import core.model.GeneralInfo
import core.releasable.BaseReleasable
import org.json.JSONObject

class GeneralInfoExecutor(
  private val threader: Application.Threader,
  private val networker: Networker,
  private val saver: Saver
) : BaseReleasable() {
  
  private var cacheHandler: SuccessHandler<GeneralInfo>? = null
  private var networkHandler: ResultHandler<GeneralInfo, Throwable>? = null
  
  init {
    addForRelease(cacheHandler, networkHandler)
  }
  
  fun tryUpdateFromCache(action: SuccessAction<GeneralInfo>) {
    if (cacheHandler == null) {
      cacheHandler = createSuccessHandler(action)
    }
    cacheHandler!!.runIfNotAlready {
      if (saver.has(CONFIRMED)) {
        cacheHandler?.dispatchSuccess(constructGeneralInfo())
      }
    }
  }
  
  fun getGeneralInfo(action: ResultAction<GeneralInfo, Throwable>) {
    if (networkHandler == null) {
      networkHandler = createResultHandler(action)
    }
    networkHandler?.runIfNotAlready {
      threader.ioWorker.submit {
        try {
          val result = networker.performRequest(URL)
          val json = JSONObject(result)
          val generalInfo = GeneralInfo(
            json.get(CONFIRMED).toString().toInt(),
            json.get(DEATHS).toString().toInt(),
            json.get(RECOVERED).toString().toInt()
          )
          saveInfo(generalInfo)
          threader.mainThreadWorker.submit { networkHandler?.dispatchSuccess(generalInfo) }
        } catch (throwable: Throwable) {
          threader.mainThreadWorker.submit { networkHandler?.dispatchFailure(throwable) }
        }
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