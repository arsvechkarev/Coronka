package com.arsvechkarev.common

import android.content.Context
import com.arsvechkarev.network.NetworkConnectionImpl
import com.arsvechkarev.network.RxNetworker
import com.arsvechkarev.storage.Saver
import com.arsvechkarev.storage.dao.CountriesDao
import core.NetworkConnection

object CommonModulesSingletons {
  
  val networker = RxNetworker()
  
  lateinit var connection: NetworkConnection
    private set
  lateinit var allCountriesRepository: AllCountriesRepository
    private set
  
  fun init(context: Context) {
    connection = NetworkConnectionImpl(context)
    val saver = Saver(AllCountriesRepository.SAVER_FILENAME, context)
    val sqLiteExecutor = CountriesSQLiteExecutor(CountriesDao())
    allCountriesRepository = AllCountriesRepository(networker, saver, sqLiteExecutor)
  }
}