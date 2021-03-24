package com.arsvechkarev.coronka.fakes

import androidx.test.platform.app.InstrumentationRegistry
import api.threading.Threader
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import core.DateTimeFormatter
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.Schedulers
import core.WebApi
import core.di.CoreModule
import core.di.DatabaseCreator
import coreimpl.AndroidSchedulers
import coreimpl.AssetsDatabaseCreator
import coreimpl.EnglishDateTimeFormatter
import coreimpl.GlideImageLoader
import coreimpl.SchedulersThreader
import coreimpl.ThreeTenAbpDateTimeCreator
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class FakeCoreModule : CoreModule {
  
  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  
  override val threader: Threader = SchedulersThreader(FakeSchedulers)
  override val schedulers: Schedulers = AndroidSchedulers
  override val databaseCreator: DatabaseCreator = AssetsDatabaseCreator(context)
  override val networkAvailabilityNotifier: NetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  override val okHttpClient: OkHttpClient = OkHttpClient()
  override val webApi: WebApi = FakeWebApi
  override val imageLoader: ImageLoader = GlideImageLoader
  override val dateTimeFormatter: DateTimeFormatter = EnglishDateTimeFormatter(context,
    ThreeTenAbpDateTimeCreator)
  
  override val rxJava2CallAdapterFactory: CallAdapter.Factory = RxJava2CallAdapterFactory.create()
  
  override val gsonConverterFactory: Converter.Factory = GsonConverterFactory.create()
}