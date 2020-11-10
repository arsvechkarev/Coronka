package com.arsvechkarev.news.presentation

import com.arsvechkarev.common.NewYorkTimesNewsRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.RxViewModel
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout

class NewsViewModel(
  private val newsRepository: NewYorkTimesNewsRepository,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  private var currentPage = 0
  
  fun startLoadingData() {
    rxCall {
      newsRepository.getLatestNews(currentPage)
          .subscribeOn(schedulers.io())
          .map<BaseScreenState> { list -> LoadedNews(list) }
          .withNetworkDelay(schedulers)
          .withRequestTimeout()
          .observeOn(schedulers.mainThread())
          .startWith(Loading())
          .onErrorReturn(::Failure)
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun tryLoadNextPage() {
    if (currentPage >= 100) return
    rxCall {
      newsRepository.getLatestNews(++currentPage)
          .subscribeOn(schedulers.io())
          .map<BaseScreenState> { list -> LoadedNextPage(list) }
          .withNetworkDelay(schedulers)
          .withRequestTimeout()
          .observeOn(schedulers.mainThread())
          .startWith(LoadingNextPage)
          .onErrorReturn { e ->
            currentPage--
            FailureLoadingNextPage(e)
          }.smartSubscribe(_state::setValue)
    }
  }
}