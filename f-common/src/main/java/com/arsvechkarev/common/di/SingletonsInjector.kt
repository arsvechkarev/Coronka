package com.arsvechkarev.common.di

import android.content.Context
import com.arsvechkarev.common.repositories.CountriesInfoExecutor
import com.arsvechkarev.common.repositories.CountriesSQLiteExecutor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.network.NetworkConnectionImpl
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig.Threader
import core.NetworkConnection

object SingletonsInjector {
  
  val networker = Networker()
  val countriesInfoExecutor = CountriesInfoExecutor(Threader, networker)
  val countriesSQLiteExecutor = CountriesSQLiteExecutor(Threader)
  
  lateinit var generalInfoExecutor: GeneralInfoExecutor
    private set
  lateinit var connection: NetworkConnection
    private set
  
  fun init(context: Context) {
    val saver = Saver(GeneralInfoExecutor.SAVER_FILENAME, context)
    generalInfoExecutor = GeneralInfoExecutor(Threader, networker, saver)
    connection = NetworkConnectionImpl(context)
  }
}