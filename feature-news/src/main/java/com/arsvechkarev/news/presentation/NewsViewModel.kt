package com.arsvechkarev.news.presentation

import base.RxViewModel
import base.extensions.startWithIf
import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.news.domain.NewsUseCase
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.Schedulers
import timber.log.Timber

class NewsViewModel(
  private val newsUseCase: NewsUseCase,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  private var currentPage = 0
  
  init {
    Timber.d("Logggging NewsViewModel initBlock, state=${state.value}")
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is FailureLoadingNextPage) {
      schedulers.mainThread().scheduleDirect(::retryLoadingNextPage)
    } else if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::retryLoadingData)
    }
  }
  
  override fun isItemLoading(item: Any?): Boolean {
    return item is Loading || item is LoadingNextPage
  }
  
  fun startLoadingData() {
    if (state.value != null) return
    performLoadingData()
  }
  
  fun tryLoadNextPage() {
    if (state.value is LoadingNextPage) return
    performLoadingNextPage()
  }
  
  fun retryLoadingData() {
    performLoadingData()
  }
  
  fun retryLoadingNextPage() {
    if (state.value !is FailureLoadingNextPage) return
    performLoadingNextPage()
  }
  
  private fun performLoadingData() {
    rxCall {
      newsUseCase.requestLatestNews(currentPage)
          .toObservable()
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
  
  private fun performLoadingNextPage() {
    rxCall {
      newsUseCase.requestLatestNews(++currentPage)
          .toObservable()
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState>(::LoadedNextPage)
          .observeOn(schedulers.mainThread())
          .startWithIf(LoadingNextPage) { currentPage < newsUseCase.maxPagesCount }
          .doOnError { currentPage-- }
          .onErrorReturn(::FailureLoadingNextPage)
          .smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    Timber.d("Logggging NewsViewModel onCleared")
    networkAvailabilityNotifier.unregisterListener(this)
  }
}