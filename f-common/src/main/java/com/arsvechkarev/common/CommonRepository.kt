package com.arsvechkarev.common

import com.arsvechkarev.common.executors.BaseListenableExecutor.CacheListener
import com.arsvechkarev.common.executors.BaseListenableExecutor.NetworkListener
import com.arsvechkarev.common.executors.CountriesInfoListenableExecutor
import com.arsvechkarev.common.executors.GeneralInfoListenableExecutor
import core.Loggable
import core.handlers.ResultAction
import core.handlers.ResultHandler
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createResultHandler
import core.handlers.createSuccessHandler
import core.log
import core.model.Country
import core.model.GeneralInfo
import core.releasable.BaseReleasable
import datetime.DateTime
import java.util.concurrent.TimeUnit

class CommonRepository(
  private val generalInfoListenableExecutor: GeneralInfoListenableExecutor,
  private val countriesInfoListenableExecutor: CountriesInfoListenableExecutor
) : BaseReleasable(), Loggable {
  
  override val logTag = "CommonRepository"
  
  private var generalInfoCacheHandler: SuccessHandler<GeneralInfo>? = null
  private var generalInfoNetworkHandler: ResultHandler<GeneralInfo, Throwable>? = null
  private var generalInfoCacheListener: CacheListener<TimedData<GeneralInfo>>? = null
  private var generalInfoNetworkListener: NetworkListener<TimedData<GeneralInfo>>? = null
  
  private var countriesInfoCacheHandler: SuccessHandler<List<Country>>? = null
  private var countriesInfoNetworkHandler: ResultHandler<List<Country>, Throwable>? = null
  private var countriesInfoCacheListener: CacheListener<TimedData<List<Country>>>? = null
  private var countriesInfoNetworkListener: NetworkListener<TimedData<List<Country>>>? = null
  
  init {
    addForRelease(generalInfoCacheHandler, generalInfoNetworkHandler, countriesInfoCacheHandler,
      countriesInfoNetworkHandler)
  }
  
  fun tryGetGeneralInfoFromCache(action: SuccessAction<GeneralInfo>) {
    generalInfoCacheHandler = createSuccessHandler(action)
    generalInfoCacheListener = object : CacheListener<TimedData<GeneralInfo>> {
    
      override fun onSuccess(result: TimedData<GeneralInfo>) {
        if (result.lastUpdateTime.isValid()) {
          log { "Sending general info value from cache" }
          generalInfoCacheHandler?.dispatchSuccess(result.data)
        } else {
          log { "General info in cache is outdated, dispatch nothing" }
          generalInfoCacheHandler?.dispatchNothing()
        }
      }
    
      override fun onNothing() {
        generalInfoCacheHandler?.dispatchNothing()
      }
    }
    generalInfoListenableExecutor.tryGetDataFromCache(generalInfoCacheListener!!)
  }
  
  fun loadGeneralInfo(action: ResultAction<GeneralInfo, Throwable>) {
    generalInfoNetworkHandler = createResultHandler(action)
    generalInfoNetworkListener = object : NetworkListener<TimedData<GeneralInfo>>() {
    
      override fun onSuccess(result: TimedData<GeneralInfo>) {
        generalInfoNetworkHandler?.dispatchSuccess(result.data)
      }
    
      override fun onFailure(failure: Throwable) {
        generalInfoNetworkHandler?.dispatchFailure(failure)
      }
    }
    generalInfoListenableExecutor.getDataFromNetWork(generalInfoNetworkListener!!)
  }
  
  fun tryGetCountriesInfoFromCache(action: SuccessAction<List<Country>>) {
    countriesInfoCacheHandler = createSuccessHandler(action)
    countriesInfoCacheListener = object : CacheListener<TimedData<List<Country>>> {
    
      override fun onSuccess(result: TimedData<List<Country>>) {
        if (result.lastUpdateTime.isValid()) {
          log { "Sending countries info from cache" }
          countriesInfoCacheHandler?.dispatchSuccess(result.data)
        } else {
          log { "Countries info in cache is outdated, dispatch nothing" }
          countriesInfoCacheHandler?.dispatchNothing()
        }
      }
    
      override fun onNothing() {
        countriesInfoCacheHandler?.dispatchNothing()
      }
    }
    countriesInfoListenableExecutor.tryGetDataFromCache(countriesInfoCacheListener!!)
  }
  
  fun loadCountriesInfo(action: ResultAction<List<Country>, Throwable>) {
    countriesInfoNetworkHandler = createResultHandler(action)
    countriesInfoNetworkListener = object : NetworkListener<TimedData<List<Country>>>() {
    
      override fun onSuccess(result: TimedData<List<Country>>) {
        countriesInfoNetworkHandler?.dispatchSuccess(result.data)
      }
    
      override fun onFailure(failure: Throwable) {
        countriesInfoNetworkHandler?.dispatchFailure(failure)
      }
    }
    countriesInfoListenableExecutor.getDataFromNetWork(countriesInfoNetworkListener!!)
  }
  
  fun DateTime.isValid(): Boolean {
    return this.differenceWith(DateTime.current(), TimeUnit.MINUTES) < CACHE_MAX_STORE_MINUTES
  }
  
  override fun release() {
    super.release()
    generalInfoListenableExecutor.release(generalInfoCacheListener)
    generalInfoListenableExecutor.release(generalInfoNetworkListener)
    countriesInfoListenableExecutor.release(countriesInfoCacheListener)
    countriesInfoListenableExecutor.release(countriesInfoNetworkListener)
  }
  
  companion object {
    private const val CACHE_MAX_STORE_MINUTES = 10
  }
}