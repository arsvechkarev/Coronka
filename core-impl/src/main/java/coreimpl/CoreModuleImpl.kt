package coreimpl

import android.content.Context
import android.net.ConnectivityManager
import core.WebApi
import core.di.CoreModule
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
  
  override val webApi: WebApi = OkHttpWebApi(okHttpClient)
  
  override val imageLoader = GlideImageLoader
  
  override val dateTimeFormatter = EnglishDateTimeFormatter(applicationContext,
    ThreeTenAbpDateTimeCreator)
  
  override val rxJava2CallAdapterFactory: CallAdapter.Factory = RxJava2CallAdapterFactory.create()
  
  override val gsonConverterFactory: Converter.Factory = GsonConverterFactory.create()
}