package com.arsvechkarev.news.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.common.CoreDiComponent.networkAvailabilityNotifier
import com.arsvechkarev.common.CoreDiComponent.webApi
import com.arsvechkarev.common.NewYorkTimesNewsRepository
import com.arsvechkarev.news.BuildConfig
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.news.presentation.NewsViewModel
import core.concurrency.AndroidSchedulers
import core.datetime.EnglishTimeFormatter

object NewsModuleInjector {
  
  fun provideViewModel(fragment: NewsFragment): NewsViewModel {
    return ViewModelProvider(fragment, newsViewModelFactory).get(NewsViewModel::class.java)
  }
  
  private val newsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          val formatter = EnglishTimeFormatter
          val repository = NewYorkTimesNewsRepository(webApi, formatter, BuildConfig.NYT_API_KEY)
          return NewsViewModel(repository, networkAvailabilityNotifier, AndroidSchedulers) as T
        }
      }
    }
}