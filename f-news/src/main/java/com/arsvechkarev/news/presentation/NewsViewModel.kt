package com.arsvechkarev.news.presentation

import com.arsvechkarev.common.NewYorkTimesNewsRepository
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.recycler.DifferentiableItem
import core.state.BaseScreenState

class NewsViewModel(
  private val newsRepository: NewYorkTimesNewsRepository,
  private val schedulers: Schedulers = AndroidSchedulers,
  private val delayMilliseconds: Long = 1000
) : RxViewModel() {
  
  fun startLoadingData() {
    rxCall {
      newsRepository.getLatestNews()
          .subscribeOn(schedulers.io())
          .map(::transformToScreenState)
          .observeOn(schedulers.mainThread())
          .subscribe(_state::setValue)
    }
  }
  
  private fun transformToScreenState(list: List<DifferentiableItem>): BaseScreenState {
    return LoadedNews(list)
  }
}