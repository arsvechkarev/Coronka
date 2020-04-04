package com.arsvechkarev.common.executors

import core.Application.Threader
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseListenableExecutor<S>(private val threader: Threader = Threader) {
  
  private val cacheLock = Any()
  private val cacheListeners = ArrayList<CacheListener<S>>()
  private val isLoadingFromCache = AtomicBoolean(false)
  
  private val networkLock = Any()
  private val networkListeners = ArrayList<NetworkListener<S>>()
  private val isLoadingFromNetwork = AtomicBoolean(false)
  
  abstract fun performCacheRequest(): S?
  
  abstract fun performNetworkRequest(): S
  
  abstract fun loadToCache(result: S)
  
  fun tryGetDataFromCache(listener: CacheListener<S>) {
    if (cacheListeners.contains(listener)) {
      return
    }
    synchronized(cacheLock) {
      cacheListeners.add(listener)
    }
    if (isLoadingFromCache.get()) {
      return
    }
    isLoadingFromCache.set(true)
    threader.ioWorker.submit {
      val result = performCacheRequest()
      if (result != null) {
        threader.mainThreadWorker.submit {
          cacheListeners.forEach { it.onSuccess(result) }
          cacheListeners.clear()
          isLoadingFromCache.set(false)
        }
      } else {
        threader.mainThreadWorker.submit {
          cacheListeners.forEach { it.onNothing() }
          cacheListeners.clear()
          isLoadingFromCache.set(false)
        }
      }
    }
  }
  
  fun getDataFromNetWork(listener: NetworkListener<S>) {
    if (networkListeners.contains(listener)) {
      return
    }
    synchronized(networkLock) {
      networkListeners.add(listener)
    }
    if (isLoadingFromNetwork.get()) {
      return
    }
    isLoadingFromNetwork.set(true)
    try {
      threader.ioWorker.submit {
        val result = performNetworkRequest()
        loadToCache(result)
        synchronized(networkLock) {
          threader.mainThreadWorker.submit {
            networkListeners.forEach { it.onSuccess(result) }
            networkListeners.clear()
            isLoadingFromNetwork.set(false)
          }
        }
      }
    } catch (e: Throwable) {
      synchronized(networkLock) {
        threader.mainThreadWorker.submit {
          networkListeners.forEach { it.onFailure(e) }
          networkListeners.clear()
          isLoadingFromNetwork.set(false)
        }
      }
    }
  }
  
  fun release(listener: CacheListener<S>?) {
    cacheListeners.remove(listener)
  }
  
  fun release(listener: NetworkListener<S>?) {
    networkListeners.remove(listener)
  }
  
  interface CacheListener<S> {
    
    fun onSuccess(result: S)
    
    fun onNothing() {}
  }
  
  interface NetworkListener<S> {
    
    fun onSuccess(result: S)
    
    fun onFailure(failure: Throwable)
  }
}