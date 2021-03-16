package com.arsvechkarev.news.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.RxViewModel
import core.Schedulers
import core.datasources.NewYorkTimesNewsDataSource
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry

class NewsViewModel(
  private val newsDataSource: NewYorkTimesNewsDataSource,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  private var currentPage = 0
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is FailureLoadingNextPage) {
      schedulers.mainThread().scheduleDirect(::tryLoadNextPage)
    } else if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      newsDataSource.requestLatestNews(currentPage)
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState>(::LoadedNews)
          .startWith(Loading)
          .onErrorReturn(::Failure)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun tryLoadNextPage() {
    if (currentPage >= newsDataSource.maxPages) return
    rxCall {
      newsDataSource.requestLatestNews(++currentPage)
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState>(::LoadedNextPage)
          .observeOn(schedulers.mainThread())
          .startWith(LoadingNextPage)
          .doOnError { currentPage-- }
          .onErrorReturn(::FailureLoadingNextPage)
          .smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}