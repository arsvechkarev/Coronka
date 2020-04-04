package com.arsvechkarev.common.di

import android.content.Context
import com.arsvechkarev.common.Repository
import com.arsvechkarev.common.repositories.CountriesInfoListenableExecutor
import com.arsvechkarev.common.repositories.CountriesSQLiteExecutor
import com.arsvechkarev.common.repositories.GeneralInfoListenableExecutor
import com.arsvechkarev.network.NetworkConnectionImpl
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.Application.Threader
import core.NetworkConnection

object SingletonsInjector {
  
  private val networker = Networker()
  private val sqLiteExecutor = CountriesSQLiteExecutor(Threader)
  
  lateinit var generalInfoListenableExecutor: GeneralInfoListenableExecutor
    private set
  lateinit var countriesInfoListenableExecutor: CountriesInfoListenableExecutor
    private set
  lateinit var connection: NetworkConnection
    private set
  lateinit var repositorySaver: Saver
    private set
  
  fun init(context: Context) {
    val generalInfoSaver = Saver(GeneralInfoListenableExecutor.SAVER_FILENAME, context)
    countriesInfoListenableExecutor = CountriesInfoListenableExecutor(networker, sqLiteExecutor)
    generalInfoListenableExecutor = GeneralInfoListenableExecutor(networker, generalInfoSaver)
    repositorySaver = Saver(Repository.SAVER_FILENAME, context)
    connection = NetworkConnectionImpl(context)
  }
}