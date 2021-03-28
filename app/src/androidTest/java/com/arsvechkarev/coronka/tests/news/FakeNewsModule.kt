package com.arsvechkarev.coronka.tests.news

import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.news.di.NewsModule
import com.arsvechkarev.news.domain.NewsUseCase
import core.Mapper
import core.model.data.NewsItem
import core.model.ui.NewsDifferentiableItem
import io.reactivex.Maybe

class FakeNewsModule(
  private val newsItemsMapper: Mapper<List<NewsItem>, List<NewsDifferentiableItem>>
) : NewsModule {
  
  override val newsUseCase = object : NewsUseCase {
    
    override val maxPagesCount = 2
    
    private var errorCount = 0
    
    override fun requestNews(page: Int): Maybe<List<NewsDifferentiableItem>> {
      if (errorCount < 1) {
        errorCount++
        return Maybe.error(Throwable())
      }
      val news = DataProvider.getNews()
      return Maybe.just(newsItemsMapper.map(news))
    }
  }
}