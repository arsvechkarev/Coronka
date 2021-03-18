package core

import android.content.Context
import android.net.ConnectivityManager
import core.database.CountriesMetaInfoDatabase
import core.database.Database
import core.datasources.CountriesMetaInfoDataSource
import core.datasources.CountriesMetaInfoDataSourceImpl
import core.datasources.TotalInfoDataSource
import core.datasources.TotalInfoDataSourceImpl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object CoreDiComponent {
  
  lateinit var webApiFactory: WebApi.Factory
    private set
  
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
    val okHttpWebApiFactory = OkHttpWebApiFactory(createOkHttpClient())
    init(okHttpWebApiFactory, notifier, database)
  }
  
  private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(20, TimeUnit.SECONDS)
        .build()
  }
  
  private fun init(
    webApiFactory: WebApi.Factory,
    notifier: NetworkAvailabilityNotifier,
    database: Database
  ) {
    this.webApiFactory = webApiFactory
    totalInfoDataSource = TotalInfoDataSourceImpl(webApiFactory.create())
    countriesMetaInfoDataSource = CountriesMetaInfoDataSourceImpl(database)
    networkAvailabilityNotifier = notifier
  }
}