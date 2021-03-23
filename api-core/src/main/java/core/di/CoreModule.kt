package core.di

import api.threading.Threader
import core.DateTimeFormatter
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.Schedulers
import core.WebApi
import okhttp3.OkHttpClient

interface CoreModule : Module {
  
  val threader: Threader
  
  val schedulers: Schedulers
  
  val databaseCreator: DatabaseCreator
  
  val networkAvailabilityNotifier: NetworkAvailabilityNotifier
  
  val okHttpClient: OkHttpClient
  
  val webApi: WebApi
  
  val imageLoader: ImageLoader
  
  val dateTimeFormatter: DateTimeFormatter
}

