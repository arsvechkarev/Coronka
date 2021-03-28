package com.arsvechkarev.coronka.fakes

import androidx.test.platform.app.InstrumentationRegistry
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import core.DateTimeFormatter
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.di.CoreModule
import core.di.DatabaseCreator
import core.rx.Schedulers
import coreimpl.AndroidSchedulers
import coreimpl.AssetsDatabaseCreator
import coreimpl.EnglishDateTimeFormatter
import coreimpl.GlideImageLoader
import coreimpl.ThreeTenAbpDateTimeCreator
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class FakeCoreModule : CoreModule {
  
  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  
  override val schedulers: Schedulers = AndroidSchedulers
  override val databaseCreator: DatabaseCreator = AssetsDatabaseCreator(context)
  override val networkAvailabilityNotifier: NetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  override val okHttpClient: OkHttpClient = OkHttpClient()
  override val imageLoader: ImageLoader = GlideImageLoader
  override val dateTimeFormatter: DateTimeFormatter = EnglishDateTimeFormatter(context,
    ThreeTenAbpDateTimeCreator)
  
  override val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
  
  override val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create()
}