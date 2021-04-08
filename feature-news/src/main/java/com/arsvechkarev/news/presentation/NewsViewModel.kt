package com.arsvechkarev.news.presentation

import api.recycler.DifferentiableItem
import base.RxViewModel
import base.extensions.startWithIf
import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.news.domain.NewsUseCase
import com.arsvechkarev.news.presentation.AdditionalItem.Mode.FAILURE
import com.arsvechkarev.news.presentation.AdditionalItem.Mode.LOADING
import core.BaseScreenState
import core.Failure
import core.Loading
import core.rx.Schedulers

class NewsViewModel(
  private val newsUseCase: NewsUseCase,
  private val schedulers: Schedulers
) : RxViewModel() {
  
  private var currentPage = 0
  
  override fun isItemLoading(item: Any?): Boolean {
    return item is Loading || item is LoadingNextPage
  }
  
  fun startLoadingData() {
    if (state.value != null) return
    performLoadingData()
  }
  
  fun tryLoadNextPage() {
    when (val value = state.value) {
      is LoadedNews -> performLoadingNextPage(value.news)
      is LoadedNewNews -> performLoadingNextPage(value.news)
    }
  }
  
  fun retryLoadingData() {
    when (state.value) {
      is Failure -> performLoadingData()
    }
  }
  
  fun retryLoadingNextPage() {
    when (val value = state.value) {
      is FailureLoadingNextPage -> performLoadingNextPage(value.list)
    }
  }
  
  fun onNetworkAvailable() {
    if (_state.value is FailureLoadingNextPage) {
      retryLoadingNextPage()
    } else if (_state.value is Failure) {
      retryLoadingData()
    }
  }
  
  private fun performLoadingData() {
    rxCall {
      newsUseCase.requestNews(currentPage)
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
  
  private fun performLoadingNextPage(currentList: List<DifferentiableItem>) {
    rxCall {
      newsUseCase.requestNews(++currentPage)
          .toObservable()
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState> { newList ->
            LoadedNewNews(news = currentList.removeLastAdditionalItem() + newList)
          }
          .observeOn(schedulers.mainThread())
          .startWithIf(LoadingNextPage(currentList.setLastAdditionalItem(LOADING))) {
            currentPage < newsUseCase.maxPagesCount
          }
          .doOnError { currentPage-- }
          .onErrorReturn { throwable ->
            FailureLoadingNextPage(currentList.setLastAdditionalItem(FAILURE), throwable)
          }
          .smartSubscribe(_state::setValue)
    }
  }
  
  private fun List<DifferentiableItem>.removeLastAdditionalItem(): List<DifferentiableItem> {
    if (this.lastOrNull() is AdditionalItem) {
      return ArrayList(this).apply { removeLast() }
    }
    return this
  }
  
  private fun List<DifferentiableItem>.setLastAdditionalItem(
    mode: AdditionalItem.Mode
  ): List<DifferentiableItem> {
    if (this.lastOrNull() is AdditionalItem) {
      (this.last() as AdditionalItem).mode = mode
      return this
    } else {
      return this + AdditionalItem(mode)
    }
  }
}