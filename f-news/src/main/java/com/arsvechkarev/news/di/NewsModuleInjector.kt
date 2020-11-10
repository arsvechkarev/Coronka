package com.arsvechkarev.news.di

import com.arsvechkarev.common.CommonModulesSingletons.networker
import com.arsvechkarev.common.NewYorkTimesNewsRepository
import com.arsvechkarev.news.BuildConfig
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.news.presentation.NewsViewModel
import core.concurrency.AndroidSchedulers
import core.datetime.EnglishTimeFormatter
import core.extenstions.createViewModel

object NewsModuleInjector {
  
  fun provideViewModel(fragment: NewsFragment): NewsViewModel {
    val formatter = EnglishTimeFormatter()
    val repository = NewYorkTimesNewsRepository(networker, formatter, BuildConfig.NYT_API_KEY)
    return fragment.createViewModel(repository, AndroidSchedulers)
  }
}