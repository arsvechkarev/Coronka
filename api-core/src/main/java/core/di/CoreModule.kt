package core.di

import api.threading.Threader
import core.DateTimeFormatter
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.Schedulers
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface CoreModule : Module {
  
  val threader: Threader
  
  val schedulers: Schedulers
  
  val databaseCreator: DatabaseCreator
  
  val networkAvailabilityNotifier: NetworkAvailabilityNotifier
  
  val okHttpClient: OkHttpClient
  
  val imageLoader: ImageLoader
  
  val dateTimeFormatter: DateTimeFormatter
  
  val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
  
  val gsonConverterFactory: GsonConverterFactory
}

