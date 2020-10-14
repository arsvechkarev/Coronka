package com.arsvechkarev.news.presentation

import com.arsvechkarev.common.NewYorkTimesNewsRepository
import core.BaseScreenState
import core.Failure
import core.MIN_NETWORK_DELAY
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.recycler.DifferentiableItem

class NewsViewModel(
  private val newsRepository: NewYorkTimesNewsRepository,
  private val schedulers: Schedulers = AndroidSchedulers,
  private val delay: Long = MIN_NETWORK_DELAY
) : RxViewModel() {
  
  fun startLoadingData() {
    rxCall {
      newsRepository.getLatestNews()
          .subscribeOn(schedulers.io())
          .map(::transformToScreenState)
          .observeOn(schedulers.mainThread())
          .onErrorReturn {
            it.printStackTrace()
            Failure.of(it)
          }
          .subscribe(_state::setValue)
    }
  }
  
  private fun transformToScreenState(list: List<DifferentiableItem>): BaseScreenState {
    return LoadedNews(list)
  }
}