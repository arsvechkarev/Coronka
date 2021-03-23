package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSourceImpl
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.di.CoreComponent.networkAvailabilityNotifier
import core.di.CoreComponent.okHttpClient
import core.di.CoreComponent.schedulers
import core.di.CoreComponent.webApiFactory
import core.di.DependencyInterceptorManager.tryInterceptDependency
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object StatsComponent {
  
  private var _defaultGeneralInfoDataSource: GeneralInfoDataSource? = null
  
  private fun getDefaultGeneralInfoDataSource(client: OkHttpClient): GeneralInfoDataSource {
    if (_defaultGeneralInfoDataSource == null) {
      _defaultGeneralInfoDataSource = Retrofit.Builder()
          .client(client)
          .baseUrl(GeneralInfoDataSource.BASE_URL)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build()
          .create(GeneralInfoDataSource::class.java)
    }
    return _defaultGeneralInfoDataSource!!
  }
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    return ViewModelProvider(fragment, statsViewModelFactory).get(StatsViewModel::class.java)
  }
  
  private val statsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          val worldCasesInfoDataSource = WorldCasesInfoDataSourceImpl(webApiFactory.create())
          val generalInfoDataSource = tryInterceptDependency(GeneralInfoDataSource::class.java,
            defaultValue = { getDefaultGeneralInfoDataSource(okHttpClient) })
          @Suppress("UNCHECKED_CAST")
          return StatsViewModel(generalInfoDataSource, worldCasesInfoDataSource,
            networkAvailabilityNotifier, schedulers) as T
        }
      }
    }
}