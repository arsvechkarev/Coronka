package core

import android.content.Context
import android.net.ConnectivityManager
import core.database.CountriesMetaInfoDatabase
import core.database.Database
import core.datasources.CountriesMetaInfoDataSource
import core.datasources.TotalInfoDataSource
import core.datasourcesimpl.CountriesMetaInfoDataSourceImpl
import core.datasourcesimpl.TotalInfoDataSourceImpl

object CoreDiComponent {
  
  private lateinit var webApiFactory: WebApi.Factory
  
  val webApi: WebApi get() = webApiFactory.create()
  
  lateinit var totalInfoDataSource: TotalInfoDataSource
    private set
  
  lateinit var networkAvailabilityNotifier: NetworkAvailabilityNotifier
    private set
  
  lateinit var countriesMetaInfoDataSource: CountriesMetaInfoDataSource
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
    CoreDiComponent.webApiFactory = webApiFactory
    totalInfoDataSource = TotalInfoDataSourceImpl(webApi)
    countriesMetaInfoDataSource = CountriesMetaInfoDataSourceImpl(database)
    networkAvailabilityNotifier = notifier
  }
}