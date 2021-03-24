package com.arsvechkarev.coronka.tests.news

import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.news.di.NewsModule
import com.arsvechkarev.news.domain.NewsUseCase
import core.model.NewsItemWithPicture
import io.reactivex.Maybe

class FakeNewsModule : NewsModule {
  
  override val newsUseCase = object : NewsUseCase {
    
    override val maxPagesCount = 2
    
    private var errorCount = 0
    
    override fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>> {
      if (errorCount < 1) {
        errorCount++
        return Maybe.error(Throwable())
      }
      return Maybe.just(DataProvider.getNews())
    }
  }
}