package com.arsvechkarev.common.repositories

import com.arsvechkarev.common.CountriesAndTime
import com.arsvechkarev.storage.Saver
import core.Application
import core.Loggable
import core.handlers.ResultAction
import core.handlers.ResultHandler
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createResultHandler
import core.handlers.createSuccessHandler
import core.log
import core.model.Country
import core.releasable.BaseReleasable
import datetime.DateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class CountriesInfoInteractor(
  private val threader: Application.Threader,
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val saver: Saver
) : BaseReleasable(), Loggable {
  
  override val logTag = "Map_CountriesInfoInteractor"
  
  private var countriesNetworkListener: ResultHandler<List<Country>, Throwable>? = null
  
  private var cacheHandler: SuccessHandler<CountriesAndTime>? = null
  private var networkHandler: ResultHandler<CountriesAndTime, Throwable>? = null
  
  init {
    addForRelease(cacheHandler, networkHandler)
  }
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun loadCountriesInfo(action: ResultAction<CountriesAndTime, Throwable>) {
    if (networkHandler == null) networkHandler = createResultHandler(action)
    networkHandler!!.runIfNotAlready {
      val future = threader.ioWorker.submit {
        removeListener()
        countriesNetworkListener = createResultHandler {
          onSuccess { countries ->
            sqLiteExecutor.saveCountriesInfo(countries)
            val datetime = DateTime.current()
            saver.execute { putString(LAST_UPDATE_TIME, datetime.toString()) }
            networkHandler?.dispatchSuccess(Pair(countries, datetime))
          }
          onFailure {
            networkHandler?.dispatchFailure(it)
          }
        }
        countriesInfoExecutor.getCountriesInfoAsync(countriesNetworkListener!!)
      }
      threader.ioWorker.submit {
        try {
          future?.get(15, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
          log(e) { "failure" }
          future?.cancel(true)
          threader.mainThreadWorker.submit { networkHandler?.dispatchFailure(e) }
        }
      }
    }
  }
  
  fun tryUpdateFromCache(action: SuccessAction<CountriesAndTime>) {
    if (cacheHandler == null) cacheHandler = createSuccessHandler(action)
    cacheHandler?.runIfNotAlready {
      threader.ioWorker.submit {
        val tableNotEmpty = sqLiteExecutor.isTableNotEmpty()
        log { "isTableNotEmpty = $tableNotEmpty" }
        if (tableNotEmpty) sqLiteExecutor.readFromDatabase {
          onSuccess {
            val dateTimeStr = saver.getString(LAST_UPDATE_TIME)
            threader.mainThreadWorker.submit {
              cacheHandler?.dispatchSuccess(Pair(it, DateTime.ofString(dateTimeStr)))
            }
          }
        }
      }
    }
  }
  
  fun removeListener() {
    countriesNetworkListener?.let { countriesInfoExecutor.removeListener(it) }
  }
  
  override fun release() {
    super.release()
    removeListener()
  }
  
  companion object {
    
    const val LAST_UPDATE_TIME = "LAST_UPDATE_TIME"
    const val SAVER_FILENAME = "CountriesInfoInteractor"
  }
}