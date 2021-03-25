package com.arsvechkarev.news.domain

import core.Mapper
import core.model.data.NewsItem
import core.model.ui.NewsDifferentiableItem
import io.reactivex.Maybe

/**
 * Use case for retrieving list of [NewsItem]
 */
interface NewsUseCase {
  
  /**
   * Returns max amount of pages for list
   */
  val maxPagesCount: Int
  
  /**
   * Returns list of [NewsItem] wrapped as [Maybe]
   */
  fun requestLatestNews(page: Int): Maybe<List<NewsDifferentiableItem>>
}

class DefaultNewsUseCase(
  private val newsApi: NewsApi,
  newYorkTimesApiKey: String,
  private val newsMapper: Mapper<List<NewsItem>, List<NewsDifferentiableItem>>,
) : NewsUseCase {
  
  override val maxPagesCount = 50
  
  private val params = mutableMapOf(
    "api-key" to newYorkTimesApiKey,
    "q" to "coronavirus",
    "fq" to "headline:(\"coronavirus\" \"covid-19\" \"covid\")",
    "sort" to "newest",
  )
  
  override fun requestLatestNews(page: Int): Maybe<List<NewsDifferentiableItem>> {
    if (page > maxPagesCount) {
      return Maybe.empty()
    }
    params["page"] = page.toString()
    return newsApi.requestLatestNews(HashMap(params))
        .map { list -> newsMapper.map(list) }
        .toMaybe()
  }
}