package com.arsvechkarev.coronka.fakes

import androidx.test.platform.app.InstrumentationRegistry
import api.threading.Threader
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import core.ImageLoader
import core.NetworkAvailabilityNotifier
import core.Schedulers
import core.WebApi
import core.di.CoreModule
import core.di.DatabaseCreator
import coreimpl.AndroidSchedulers
import coreimpl.AssetsDatabaseCreator
import coreimpl.GlideImageLoader
import coreimpl.SchedulersThreader
import okhttp3.OkHttpClient

open class FakeCoreModule : CoreModule {
  
  private val context = InstrumentationRegistry.getInstrumentation().targetContext
  
  override val threader: Threader = SchedulersThreader(FakeSchedulers)
  override val schedulers: Schedulers = AndroidSchedulers
  override val databaseCreator: DatabaseCreator = AssetsDatabaseCreator(context)
  override val networkAvailabilityNotifier: NetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  override val okHttpClient: OkHttpClient = OkHttpClient()
  override val webApiFactory: WebApi.Factory = FakeWebApi
  override val imageLoader: ImageLoader = GlideImageLoader
}