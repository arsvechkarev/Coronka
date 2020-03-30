package com.arsvechkarev.common.repositories

import com.arsvechkarev.common.repositories.CountriesInfoExecutor.CountriesInfoListener
import core.ApplicationConfig
import core.Loggable
import core.log
import core.model.Country
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class CountriesInfoInteractor(
  private val threader: ApplicationConfig.Threader,
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor
) : Loggable {
  
  override val logTag = "Map_CountriesInfoInteractor"
  
  private var listener: CountriesInfoListener? = null
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(
    onSuccess: (List<Country>) -> Unit,
    onFailure: (Throwable) -> Unit = {}
  ) {
    val future = threader.ioWorker.submit {
      removeListener()
      listener = object : CountriesInfoListener {
        override fun onSuccess(countriesData: List<Country>) {
          sqLiteExecutor.saveCountriesInfo(countriesData)
          onSuccess(countriesData)
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
  
  fun tryUpdateFromCache(onSuccess: (List<Country>) -> Unit) {
    threader.ioWorker.submit {
      val tableNotEmpty = sqLiteExecutor.isTableNotEmpty()
      log { "isTableNotEmpty = $tableNotEmpty" }
      if (tableNotEmpty) sqLiteExecutor.readFromDatabase {
        threader.mainThreadWorker.submit { onSuccess(it) }
      }
    }
  }
  
  fun removeListener() {
    listener?.let {
      countriesInfoExecutor.removeListener(it)
    }
  }
}