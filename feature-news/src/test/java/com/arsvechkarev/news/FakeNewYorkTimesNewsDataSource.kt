package com.arsvechkarev.news

import com.arsvechkarev.news.domain.NewYorkTimesNewsDataSource
import core.model.NewsItemWithPicture
import io.reactivex.Maybe
import java.net.UnknownHostException

val FakeNewListPages = listOf(
  listOf(
    NewsItemWithPicture("_id1", "news1", "desc1",
      "url1", "", "https://image1"),
  ),
  listOf(
    NewsItemWithPicture("_id2", "news2", "desc2",
      "url2", "", "https://image2"),
  ),
  listOf(
    NewsItemWithPicture("_id3", "news3", "desc3",
      "url3", "", "https://image3"),
  ),
  listOf(
    NewsItemWithPicture("_id4", "news4", "desc4",
      "url4", "", "https://image4"),
  )
)

class FakeNewYorkTimesNewsDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : NewYorkTimesNewsDataSource {
  
  private var nextThrowable: Throwable? = null
  
  private var retryCount = 0
  
  fun setNextCallAsError(throwable: Throwable) {
    this.nextThrowable = throwable
  }
  
  override val maxPages: Int = 4
  
  override fun requestLatestNews(page: Int) = Maybe.create<List<NewsItemWithPicture>> { emitter ->
    if (nextThrowable != null) {
      emitter.onError(nextThrowable!!)
      nextThrowable = null
      return@create
    }
    if (retryCount < totalRetryCount) {
      retryCount++
      emitter.onError(errorFactory())
      return@create
    }
    if (page in 0 until maxPages) {
      emitter.onSuccess(FakeNewListPages[page])
    } else {
      emitter.onComplete()
    }
  }
}
