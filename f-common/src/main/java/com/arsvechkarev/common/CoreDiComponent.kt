package com.arsvechkarev.common

import android.content.Context
import android.net.ConnectivityManager
import com.arsvechkarev.storage.Database
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase
import core.AndroidNetworkAvailabilityNotifier
import core.NetworkAvailabilityNotifier
import core.RxWebApiFactory
import core.WebApi

object CoreDiComponent {
  
  private lateinit var webApiFactory: WebApi.Factory
  
  val webApi: WebApi get() = webApiFactory.create()
  
  lateinit var allCountriesDataSource: AllCountriesDataSource
    private set
  
  lateinit var networkAvailabilityNotifier: NetworkAvailabilityNotifier
    private set
  
  lateinit var metaInfoRepository: CountriesMetaInfoRepository
    private set
  
  fun initCustom(
    webApiFactory: WebApi.Factory,
    notifier: NetworkAvailabilityNotifier,
    applicationContext: Context,
  ) {
    init(webApiFactory, notifier, CountriesMetaInfoDatabase(applicationContext))
  }
  
  fun initDefault(applicationContext: Context) {
    val notifier = AndroidNetworkAvailabilityNotifier(
      applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )
    val database = CountriesMetaInfoDatabase(applicationContext)
    init(RxWebApiFactory, notifier, database)
  }
  
  private fun init(
    webApiFactory: WebApi.Factory,
    notifier: NetworkAvailabilityNotifier,
    database: Database
  ) {
    this.webApiFactory = webApiFactory
    allCountriesDataSource = AllCountriesDataSource(webApi)
    metaInfoRepository = CountriesMetaInfoRepository(database)
    networkAvailabilityNotifier = notifier
  }
}