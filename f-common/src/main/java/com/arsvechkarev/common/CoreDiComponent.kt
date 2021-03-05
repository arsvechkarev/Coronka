package com.arsvechkarev.common

import android.content.Context
import com.arsvechkarev.storage.DatabaseImpl
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseHelper
import core.NetworkConnection
import core.NetworkConnectionImpl
import core.Networker
import core.RxNetworker

object CoreDiComponent {
  
  lateinit var networker: Networker
    private set
  
  lateinit var connection: NetworkConnection
    private set
  
  lateinit var allCountriesDataSource: AllCountriesDataSource
    private set
  
  lateinit var metaInfoRepository: CountriesMetaInfoRepository
    private set
  
  fun initCustomNetworker(context: Context, networker: Networker) {
    init(context, networker)
  }
  
  fun initDefault(context: Context) {
    init(context, RxNetworker)
  }
  
  private fun init(context: Context, networker: Networker) {
    this.networker = networker
    connection = NetworkConnectionImpl(context)
    allCountriesDataSource = AllCountriesDataSource(networker)
    val database = DatabaseImpl(CountriesMetaInfoDatabaseHelper.instance)
    metaInfoRepository = CountriesMetaInfoRepository(database)
  }
}