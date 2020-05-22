package com.arsvechkarev.common.executors

import core.concurrency.AndroidThreader
import core.concurrency.Threader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseListenableExecutor<S>(
  val threader: Threader = AndroidThreader,
  private val timeoutSeconds: Long = 15
) {
  
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
    threader.onIoThread {
      val result = performCacheRequest()
      if (result != null) {
        threader.onMainThread {
          cacheListeners.forEach { it.onSuccess(result) }
          cacheListeners.clear()
          isLoadingFromCache.set(false)
        }
      } else {
        threader.onMainThread {
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
    threader.onBackground {
      val future = threader.onIoThread {
        val result = performNetworkRequest()
        loadToCache(result)
        synchronized(networkLock) {
          threader.onMainThread {
            networkListeners.forEach { it.onSuccess(result) }
            networkListeners.clear()
            isLoadingFromNetwork.set(false)
          }
        }
      }
      try {
        future.get(timeoutSeconds, TimeUnit.SECONDS)
      } catch (e: Throwable) {
        future.cancel(true)
        synchronized(networkLock) {
          isLoadingFromNetwork.set(false)
          networkListeners.forEach {
            networkListeners.remove(it)
            if (it.retryCount == 0 || e is TimeoutException) {
              it.onFailure(e)
            } else {
              it.retryCount--
              getDataFromNetWork(it)
            }
          }
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
  
  abstract class NetworkListener<S>(internal var retryCount: Int = 3) {
    
    abstract fun onSuccess(result: S)
    
    abstract fun onFailure(failure: Throwable)
  }
}