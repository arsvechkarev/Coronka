package com.arsvechkarev.common

import com.arsvechkarev.common.repositories.BaseListenableExecutor.CacheListener
import com.arsvechkarev.common.repositories.BaseListenableExecutor.NetworkListener
import com.arsvechkarev.common.repositories.CountriesInfoListenableExecutor
import com.arsvechkarev.common.repositories.GeneralInfoListenableExecutor
import com.arsvechkarev.storage.Saver
import core.handlers.ResultAction
import core.handlers.ResultHandler
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createResultHandler
import core.handlers.createSuccessHandler
import core.model.Country
import core.model.GeneralInfo
import core.releasable.BaseReleasable
import datetime.DateTime

class Repository(
  private val saver: Saver,
  private val generalInfoListenableExecutor: GeneralInfoListenableExecutor,
  private val countriesInfoListenableExecutor: CountriesInfoListenableExecutor
) : BaseReleasable() {
  
  private var generalInfoCacheHandler: SuccessHandler<TimedResult<GeneralInfo>>? = null
  private var generalInfoNetworkHandler: ResultHandler<GeneralInfo, Throwable>? = null
  private var generalInfoCacheListener: CacheListener<GeneralInfo>? = null
  private var generalInfoNetworkListener: NetworkListener<GeneralInfo>? = null
  
  private var countriesInfoCacheHandler: SuccessHandler<TimedResult<List<Country>>>? = null
  private var countriesInfoNetworkHandler: ResultHandler<List<Country>, Throwable>? = null
  private var countriesInfoCacheListener: CacheListener<List<Country>>? = null
  private var countriesInfoNetworkListener: NetworkListener<List<Country>>? = null
  
  init {
    addForRelease(generalInfoCacheHandler, generalInfoNetworkHandler, countriesInfoCacheHandler,
      countriesInfoNetworkHandler)
  }
  
  fun tryGetCountriesInfoFromCache(action: SuccessAction<TimedResult<List<Country>>>) {
    if (countriesInfoCacheHandler == null && countriesInfoCacheListener == null) {
      countriesInfoCacheHandler = createSuccessHandler(action)
      countriesInfoCacheListener = object : CacheListener<List<Country>> {
        
        override fun onSuccess(result: List<Country>) {
          val time = saver.getString(
            COUNTRIES_INFO_LAST_UPDATE_TIME)
          countriesInfoCacheHandler?.dispatchSuccess(TimedResult(result, DateTime.ofString(time)))
        }
      }
    }
    countriesInfoCacheHandler!!.runIfNotAlready {
      countriesInfoListenableExecutor.tryGetDataFromCache(countriesInfoCacheListener!!)
    }
  }
  
  fun loadCountriesInfo(action: ResultAction<List<Country>, Throwable>) {
    if (countriesInfoNetworkHandler == null && countriesInfoNetworkListener == null) {
      countriesInfoNetworkHandler = createResultHandler(action)
      countriesInfoNetworkListener = object : NetworkListener<List<Country>> {
        
        override fun onSuccess(result: List<Country>) {
          saver.execute {
            putString(COUNTRIES_INFO_LAST_UPDATE_TIME, DateTime.current().toString())
          }
          countriesInfoNetworkHandler?.dispatchSuccess(result)
        }
        
        override fun onFailure(failure: Throwable) {
          countriesInfoNetworkHandler?.dispatchFailure(failure)
        }
      }
    }
    countriesInfoNetworkHandler!!.runIfNotAlready {
      countriesInfoListenableExecutor.getDataFromNetWork(countriesInfoNetworkListener!!)
    }
  }
  
  fun tryGetGeneralInfoFromCache(action: SuccessAction<TimedResult<GeneralInfo>>) {
    if (generalInfoCacheHandler == null && generalInfoCacheListener == null) {
      generalInfoCacheHandler = createSuccessHandler(action)
      generalInfoCacheListener = object : CacheListener<GeneralInfo> {
        
        override fun onSuccess(result: GeneralInfo) {
          val time = saver.getString(
            GENERAL_INFO_LAST_UPDATE_TIME)
          generalInfoCacheHandler?.dispatchSuccess(TimedResult(result, DateTime.ofString(time)))
        }
      }
    }
    generalInfoCacheHandler!!.runIfNotAlready {
      generalInfoListenableExecutor.tryGetDataFromCache(generalInfoCacheListener!!)
    }
  }
  
  fun loadGeneralInfo(action: ResultAction<GeneralInfo, Throwable>) {
    if (generalInfoNetworkHandler == null && generalInfoNetworkListener == null) {
      generalInfoNetworkHandler = createResultHandler(action)
      generalInfoNetworkListener = object : NetworkListener<GeneralInfo> {
        
        override fun onSuccess(result: GeneralInfo) {
          saver.execute {
            putString(GENERAL_INFO_LAST_UPDATE_TIME, DateTime.current().toString())
          }
          generalInfoNetworkHandler?.dispatchSuccess(result)
        }
        
        override fun onFailure(failure: Throwable) {
          generalInfoNetworkHandler?.dispatchFailure(failure)
        }
      }
    }
    generalInfoNetworkHandler!!.runIfNotAlready {
      generalInfoListenableExecutor.getDataFromNetWork(generalInfoNetworkListener!!)
    }
  }
  
  override fun release() {
    super.release()
    generalInfoListenableExecutor.release(generalInfoCacheListener)
    generalInfoListenableExecutor.release(generalInfoNetworkListener)
    countriesInfoListenableExecutor.release(countriesInfoCacheListener)
    countriesInfoListenableExecutor.release(countriesInfoNetworkListener)
  }
  
  companion object {
    const val GENERAL_INFO_LAST_UPDATE_TIME = "GENERAL_INFO_LAST_UPDATE_TIME"
    const val COUNTRIES_INFO_LAST_UPDATE_TIME = "COUNTRIES_INFO_LAST_UPDATE_TIME"
    const val SAVER_FILENAME = "Repository"
  }
}