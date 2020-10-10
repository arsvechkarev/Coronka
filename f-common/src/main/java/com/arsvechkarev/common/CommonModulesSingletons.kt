package com.arsvechkarev.common

import android.content.Context
import com.arsvechkarev.storage.DatabaseImpl
import com.arsvechkarev.storage.countries.CountriesMetaInfoHelper
import core.NetworkConnection
import core.NetworkConnectionImpl
import core.RxNetworker

object CommonModulesSingletons {
  
  val networker = RxNetworker()
  
  lateinit var connection: NetworkConnection
    private set
  
  lateinit var allCountriesRepository: AllCountriesRepository
    private set
  
  lateinit var metaInfoRepository: CountriesMetaInfoRepository
    private set
  
  fun init(context: Context) {
    connection = NetworkConnectionImpl(context)
    allCountriesRepository = AllCountriesRepository(networker)
    val database = DatabaseImpl(CountriesMetaInfoHelper.instance)
    metaInfoRepository = CountriesMetaInfoRepository(database)
  }
}