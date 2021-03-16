package com.arsvechkarev.news.presentation

import core.BaseScreenState
import core.recycler.DifferentiableItem
import core.toFailureReason

class LoadedNews(val news: List<DifferentiableItem>) : BaseScreenState

class LoadedNextPage(val newNews: List<DifferentiableItem>) : BaseScreenState

class AdditionalItem(val mode: Mode) : BaseScreenState, DifferentiableItem {
  override val id: String = "AdditionalItem"
  override fun equals(other: Any?) = other is AdditionalItem
  override fun hashCode() = id.hashCode()
  
  enum class Mode {
    LOADING, FAILURE
  }
}

object LoadingNextPage : BaseScreenState

class FailureLoadingNextPage(val throwable: Throwable) : BaseScreenState {
  
  val reason = throwable.toFailureReason()
}