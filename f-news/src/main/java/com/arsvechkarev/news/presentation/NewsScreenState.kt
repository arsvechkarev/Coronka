package com.arsvechkarev.news.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.recycler.DifferentiableItem

class LoadedNews(val news: List<DifferentiableItem>) : BaseScreenState()

class LoadedNextPage(val newNews: List<DifferentiableItem>) : BaseScreenState()

object LoadingNextPage : Loading(), DifferentiableItem {
  override val id: String = "LoadingNextPage"
  override fun equals(other: Any?) = other is LoadingNextPage
  override fun hashCode() = id.hashCode()
}

class FailureLoadingNextPage(e: Throwable) : Failure(e)
