package com.arsvechkarev.common.di

import android.content.Context
import com.arsvechkarev.common.executors.CountriesInfoListenableExecutor
import com.arsvechkarev.common.executors.CountriesSQLiteExecutor
import com.arsvechkarev.common.executors.GeneralInfoListenableExecutor
import com.arsvechkarev.network.NetworkConnectionImpl
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import com.arsvechkarev.storage.dao.CountriesDao
import core.NetworkConnection

object SingletonsInjector {
  
  private val networker = Networker()
  private val sqLiteExecutor = CountriesSQLiteExecutor(CountriesDao())
  
  lateinit var generalInfoListenableExecutor: GeneralInfoListenableExecutor
    private set
  lateinit var countriesInfoListenableExecutor: CountriesInfoListenableExecutor
    private set
  lateinit var connection: NetworkConnection
    private set
  
  fun init(context: Context) {
    val generalExecutorSaver = Saver(GeneralInfoListenableExecutor.SAVER_FILENAME, context)
    val countriesExecutorSaver = Saver(CountriesInfoListenableExecutor.SAVER_FILENAME, context)
    generalInfoListenableExecutor = GeneralInfoListenableExecutor(networker, generalExecutorSaver)
    countriesInfoListenableExecutor = CountriesInfoListenableExecutor(networker, sqLiteExecutor,
      countriesExecutorSaver)
    connection = NetworkConnectionImpl(context)
  }
}