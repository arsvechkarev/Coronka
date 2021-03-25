package com.arsvechkarev.news

import com.arsvechkarev.news.domain.NewsUseCase
import core.model.ui.NewsDifferentiableItem
import io.reactivex.Maybe
import java.net.UnknownHostException

val FakeNewsListPages = listOf(
  listOf(
    NewsDifferentiableItem("_id1", "news1", "desc1",
      "url1", "", "https://image1"),
  ),
  listOf(
    NewsDifferentiableItem("_id2", "news2", "desc2",
      "url2", "", "https://image2"),
  ),
  listOf(
    NewsDifferentiableItem("_id3", "news3", "desc3",
      "url3", "", "https://image3"),
  ),
  listOf(
    NewsDifferentiableItem("_id4", "news4", "desc4",
      "url4", "", "https://image4"),
  )
)

class FakeNewsUseCase(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : NewsUseCase {
  
  private var nextThrowable: Throwable? = null
  
  private var retryCount = 0
  
  fun setNextCallAsError(throwable: Throwable) {
    this.nextThrowable = throwable
  }
  
  override val maxPagesCount: Int = 4
  
  override fun requestLatestNews(page: Int) = Maybe.create<List<NewsDifferentiableItem>> { emitter ->
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
    if (page in 0 until maxPagesCount) {
      emitter.onSuccess(FakeNewsListPages[page])
    } else {
      emitter.onComplete()
    }
  }
}
