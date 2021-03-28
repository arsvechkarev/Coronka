package com.arsvechkarev.news.presentation

import api.recycler.DifferentiableItem
import core.BaseScreenState
import core.toFailureReason

class LoadedNews(val news: List<DifferentiableItem>) : BaseScreenState

class LoadedNewNews(val news: List<DifferentiableItem>) : BaseScreenState

class LoadingNextPage(val list: List<DifferentiableItem>) : BaseScreenState

class FailureLoadingNextPage(
  val list: List<DifferentiableItem>, val throwable: Throwable
) : BaseScreenState {
  
  val reason = throwable.toFailureReason()
}

data class AdditionalItem(var mode: Mode) : DifferentiableItem {
  
  override val id: String = "AdditionalItem"
  
  enum class Mode {
    LOADING, FAILURE
  }
}