package com.arsvechkarev.common

import com.arsvechkarev.common.executors.BaseListenableExecutor.CacheListener
import com.arsvechkarev.common.executors.BaseListenableExecutor.NetworkListener
import com.arsvechkarev.common.executors.CountriesInfoListenableExecutor
import com.arsvechkarev.common.executors.GeneralInfoListenableExecutor
import core.handlers.ResultAction
import core.handlers.ResultHandler
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createResultHandler
import core.handlers.createSuccessHandler
import core.model.Country
import core.model.GeneralInfo
import core.releasable.BaseReleasable

class CommonRepository(
  private val generalInfoListenableExecutor: GeneralInfoListenableExecutor,
  private val countriesInfoListenableExecutor: CountriesInfoListenableExecutor
) : BaseReleasable() {
  
  private var generalInfoCacheHandler: SuccessHandler<TimedData<GeneralInfo>>? = null
  private var generalInfoNetworkHandler: ResultHandler<TimedData<GeneralInfo>, Throwable>? = null
  private var generalInfoCacheListener: CacheListener<TimedData<GeneralInfo>>? = null
  private var generalInfoNetworkListener: NetworkListener<TimedData<GeneralInfo>>? = null
  
  private var countriesInfoCacheHandler: SuccessHandler<TimedData<List<Country>>>? = null
  private var countriesInfoNetworkHandler: ResultHandler<TimedData<List<Country>>, Throwable>? = null
  private var countriesInfoCacheListener: CacheListener<TimedData<List<Country>>>? = null
  private var countriesInfoNetworkListener: NetworkListener<TimedData<List<Country>>>? = null
  
  init {
    addForRelease(generalInfoCacheHandler, generalInfoNetworkHandler, countriesInfoCacheHandler,
      countriesInfoNetworkHandler)
  }
  
  fun tryGetGeneralInfoFromCache(action: SuccessAction<TimedData<GeneralInfo>>) {
    if (generalInfoCacheHandler == null && generalInfoCacheListener == null) {
      generalInfoCacheHandler = createSuccessHandler(action)
      generalInfoCacheListener = object : CacheListener<TimedData<GeneralInfo>> {
        
        override fun onSuccess(result: TimedData<GeneralInfo>) {
          generalInfoCacheHandler?.dispatchSuccess(result)
        }
        
        override fun onNothing() {
          generalInfoCacheHandler?.dispatchNothing()
        }
      }
    }
    generalInfoCacheHandler!!.runIfNotAlready {
      generalInfoListenableExecutor.tryGetDataFromCache(generalInfoCacheListener!!)
    }
  }
  
  fun loadGeneralInfo(action: ResultAction<TimedData<GeneralInfo>, Throwable>) {
    if (generalInfoNetworkHandler == null && generalInfoNetworkListener == null) {
      generalInfoNetworkHandler = createResultHandler(action)
      generalInfoNetworkListener = object : NetworkListener<TimedData<GeneralInfo>> {
        
        override fun onSuccess(result: TimedData<GeneralInfo>) {
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
  
  fun tryGetCountriesInfoFromCache(action: SuccessAction<TimedData<List<Country>>>) {
    if (countriesInfoCacheHandler == null && countriesInfoCacheListener == null) {
      countriesInfoCacheHandler = createSuccessHandler(action)
      countriesInfoCacheListener = object : CacheListener<TimedData<List<Country>>> {
        
        override fun onSuccess(result: TimedData<List<Country>>) {
          countriesInfoCacheHandler?.dispatchSuccess(result)
        }
        
        override fun onNothing() {
          countriesInfoCacheHandler?.dispatchNothing()
        }
      }
    }
    countriesInfoCacheHandler!!.runIfNotAlready {
      countriesInfoListenableExecutor.tryGetDataFromCache(countriesInfoCacheListener!!)
    }
  }
  
  fun loadCountriesInfo(action: ResultAction<TimedData<List<Country>>, Throwable>) {
    if (countriesInfoNetworkHandler == null && countriesInfoNetworkListener == null) {
      countriesInfoNetworkHandler = createResultHandler(action)
      countriesInfoNetworkListener = object : NetworkListener<TimedData<List<Country>>> {
        
        override fun onSuccess(result: TimedData<List<Country>>) {
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
  
  override fun release() {
    super.release()
    generalInfoListenableExecutor.release(generalInfoCacheListener)
    generalInfoListenableExecutor.release(generalInfoNetworkListener)
    countriesInfoListenableExecutor.release(countriesInfoCacheListener)
    countriesInfoListenableExecutor.release(countriesInfoNetworkListener)
  }
}