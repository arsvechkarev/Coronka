package com.arsvechkarev.news.presentation

import android.net.ConnectivityManager
import com.arsvechkarev.common.NewYorkTimesNewsRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.RxViewModel
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry

class NewsViewModel(
  private val newsRepository: NewYorkTimesNewsRepository,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), ConnectivityManager.OnNetworkActiveListener {
  
  private var currentPage = 0
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkActive() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    } else if (_state.value is FailureLoadingNextPage) {
      schedulers.mainThread().scheduleDirect(::tryLoadNextPage)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      newsRepository.getLatestNews(currentPage)
          .subscribeOn(schedulers.io())
          .map<BaseScreenState> { list -> LoadedNews(list) }
          .withNetworkDelay(schedulers)
          .withRequestTimeout()
          .observeOn(schedulers.mainThread())
          .startWith(Loading())
          .withRetry()
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
          .withRetry()
          .onErrorReturn { e ->
            currentPage--
            FailureLoadingNextPage(e)
          }.smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}