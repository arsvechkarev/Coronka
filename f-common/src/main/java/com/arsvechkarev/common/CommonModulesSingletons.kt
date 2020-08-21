package com.arsvechkarev.common

import android.content.Context
import com.arsvechkarev.storage.Saver
import core.NetworkConnection
import core.NetworkConnectionImpl
import core.RxNetworker
import core.dao.CountriesDao

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