package com.arsvechkarev.news.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.CommonModulesSingletons.networker
import com.arsvechkarev.common.NewYorkTimesNewsRepository
import com.arsvechkarev.news.BuildConfig
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.news.presentation.NewsViewModel
import core.datetime.EnglishTimeFormatter

object NewsModuleInjector {
  
  fun provideViewModel(fragment: NewsFragment): NewsViewModel {
    val newYorkTimesNewsRepository = NewYorkTimesNewsRepository(
      networker, EnglishTimeFormatter(), BuildConfig.NYT_API_KEY
    )
    val factory = newsViewModelFactory(newYorkTimesNewsRepository)
    return ViewModelProviders.of(fragment, factory).get(NewsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun newsViewModelFactory(
    newYorkTimesNewsRepository: NewYorkTimesNewsRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = NewsViewModel(newYorkTimesNewsRepository)
      return viewModel as T
    }
  }
}