package com.arsvechkarev.news.domain

import core.model.NewsItemWithPicture
import io.reactivex.Maybe

/**
 * Use case for retrieving list of [NewsItemWithPicture]
 */
interface NewsUseCase {
  
  /**
   * Returns max amount of pages for list
   */
  val maxPagesCount: Int
  
  /**
   * Returns list of [NewsItemWithPicture] wrapped as [Maybe]
   */
  fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>>
}

class DefaultNewsUseCase(
  private val newsApi: NewsApi, newYorkTimesApiKey: String,
) : NewsUseCase {
  
  override val maxPagesCount = 50
  
  private val params = mutableMapOf(
    "api-key" to newYorkTimesApiKey,
    "q" to "coronavirus",
    "fq" to "headline:(\"coronavirus\" \"covid-19\" \"covid\")",
    "sort" to "newest",
  )
  
  override fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>> {
    if (page > maxPagesCount) {
      return Maybe.empty()
    }
    params["page"] = page.toString()
    return newsApi.requestLatestNews(HashMap(params)).toMaybe()
  }
}