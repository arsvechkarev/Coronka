package coreimpl

import android.content.Context
import android.net.ConnectivityManager
import core.di.CoreModule
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DefaultCoreModule(applicationContext: Context) : CoreModule {
  
  override val schedulers = AndroidSchedulers
  
  override val threader = SchedulersThreader(AndroidSchedulers)
  
  override val databaseCreator = AssetsDatabaseCreator(applicationContext)
  
  override val networkAvailabilityNotifier = AndroidNetworkAvailabilityNotifier(
    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
  
  override val okHttpClient: OkHttpClient = OkHttpClient.Builder()
      .callTimeout(20, TimeUnit.SECONDS)
      .addInterceptor { chain ->
        val request = chain.request()
        Timber.d("requesting = $request")
        chain.proceed(request)
      }
      .build()
  
  override val imageLoader = GlideImageLoader
  
  override val dateTimeFormatter = EnglishDateTimeFormatter(applicationContext,
    ThreeTenAbpDateTimeCreator)
  
  override val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
  
  override val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create()
}