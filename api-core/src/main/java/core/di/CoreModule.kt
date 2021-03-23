package core.di

import api.threading.Threader
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.Schedulers
import core.WebApi
import okhttp3.OkHttpClient

interface CoreModule {
  
  val threader: Threader
  
  val schedulers: Schedulers
  
  val databaseCreator: DatabaseCreator
  
  val networkAvailabilityNotifier: NetworkAvailabilityNotifier
  
  val okHttpClient: OkHttpClient
  
  val webApiFactory: WebApi.Factory
  
  val imageLoader: ImageLoader
}

