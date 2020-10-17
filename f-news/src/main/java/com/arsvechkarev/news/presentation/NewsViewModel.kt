package com.arsvechkarev.news.presentation

import com.arsvechkarev.common.NewYorkTimesNewsRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.MIN_NETWORK_DELAY
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.recycler.DifferentiableItem
import java.util.concurrent.TimeUnit.MILLISECONDS

class NewsViewModel(
  private val newsRepository: NewYorkTimesNewsRepository,
  private val schedulers: Schedulers = AndroidSchedulers,
  private val delay: Long = MIN_NETWORK_DELAY
) : RxViewModel() {
  
  private var currentPage = 0
  
  fun startLoadingData() {
    rxCall {
      newsRepository.getLatestNews(currentPage)
          .subscribeOn(schedulers.io())
          .map(::transformToLoadedNews)
          .delay(delay, MILLISECONDS, schedulers.computation(), true)
          .observeOn(schedulers.mainThread())
          .startWith(Loading())
          .onErrorReturn(::Failure)
          .smartSubscribe { state ->
            _state.setValue(state)
          }
    }
  }
  
  fun tryLoadNextPage() {
    if (currentPage >= 100) return
    rxCall {
      newsRepository.getLatestNews(++currentPage)
          .subscribeOn(schedulers.io())
          .map(::transformToLoadedNextPage)
          .delay(delay, MILLISECONDS, schedulers.computation(), true)
          .observeOn(schedulers.mainThread())
          .startWith(LoadingNextPage)
          .onErrorReturn { e ->
            currentPage--
            FailureLoadingNextPage(e)
          }.smartSubscribe(_state::setValue)
    }
  }
  
  private fun transformToLoadedNews(list: List<DifferentiableItem>): BaseScreenState {
    return LoadedNews(list)
  }
  
  private fun transformToLoadedNextPage(list: List<DifferentiableItem>): BaseScreenState {
    return LoadedNextPage(list)
  }
}