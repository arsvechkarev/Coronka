package coreimpl

import android.content.Context
import android.net.ConnectivityManager
import core.di.CoreModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class CoreModuleImpl(applicationContext: Context) : CoreModule {
  
  override val schedulers = AndroidSchedulers
  
  override val threader = SchedulersThreader(AndroidSchedulers)
  
  override val databaseCreator = AssetsDatabaseCreator(applicationContext)
  
  override val networkAvailabilityNotifier = AndroidNetworkAvailabilityNotifier(
    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
  
  override val okHttpClient: OkHttpClient = OkHttpClient.Builder()
      .callTimeout(20, TimeUnit.SECONDS)
      .build()
  
  override val webApiFactory = OkHttpWebApiFactory(okHttpClient)
  
  override val imageLoader = GlideImageLoader
}