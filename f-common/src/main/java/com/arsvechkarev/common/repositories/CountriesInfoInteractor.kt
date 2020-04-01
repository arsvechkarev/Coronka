package com.arsvechkarev.common.repositories

import com.arsvechkarev.common.repositories.CountriesInfoExecutor.CountriesInfoListener
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig
import core.Loggable
import core.log
import core.model.Country
import datetime.DateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class CountriesInfoInteractor(
  private val threader: ApplicationConfig.Threader,
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val saver: Saver
) : Loggable {
  
  override val logTag = "Map_CountriesInfoInteractor"
  
  private var listener: CountriesInfoListener? = null
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun loadCountriesInfo(
    onSuccess: (List<Country>, DateTime) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    val future = threader.ioWorker.submit {
      removeListener()
      listener = object : CountriesInfoListener {
        override fun onSuccess(countriesData: List<Country>) {
          sqLiteExecutor.saveCountriesInfo(countriesData)
          val datetime = DateTime.current()
          saver.execute { putString(LAST_UPDATE_TIME, datetime.toString()) }
          onSuccess(countriesData, datetime)
        }
        
        override fun onFailure(throwable: Throwable) = onFailure(throwable)
      }
      countriesInfoExecutor.getCountriesInfoAsync(listener!!)
    }
    threader.ioWorker.submit {
      try {
        future?.get(15, TimeUnit.SECONDS)
      } catch (e: TimeoutException) {
        log(e) { "failure" }
        future?.cancel(true)
        threader.mainThreadWorker.submit { onFailure(e) }
      }
    }
  }
  
  fun tryUpdateFromCache(onSuccess: (List<Country>, DateTime) -> Unit) {
    threader.ioWorker.submit {
      val tableNotEmpty = sqLiteExecutor.isTableNotEmpty()
      log { "isTableNotEmpty = $tableNotEmpty" }
      if (tableNotEmpty) sqLiteExecutor.readFromDatabase {
        val dateTimeStr = saver.getString(LAST_UPDATE_TIME)
        threader.mainThreadWorker.submit {
          onSuccess(it, DateTime.ofString(dateTimeStr))
        }
      }
    }
  }
  
  fun removeListener() {
    listener?.let {
      countriesInfoExecutor.removeListener(it)
    }
  }
  
  companion object {
    
    const val LAST_UPDATE_TIME = "LAST_UPDATE_TIME"
    const val SAVER_FILENAME = "CountriesInfoInteractor"
  }
}