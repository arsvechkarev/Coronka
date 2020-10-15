package com.arsvechkarev.news.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.recycler.DifferentiableItem

class LoadedNews(
  val news: List<DifferentiableItem>
) : BaseScreenState()

object LoadingNextPage : Loading(), DifferentiableItem {
  
  override val id: String = "LoadingNextPage"
  override fun equals(other: Any?) = other is LoadingNextPage
}

class FailureLoadingNextPage(e: Throwable) : Failure(e), DifferentiableItem {
  
  override val id = "FailureLoadingNextPage"
  override fun equals(other: Any?) = false
  override fun hashCode() = id.hashCode()
}

class LoadedNextPage(
  val news: List<DifferentiableItem>
) : BaseScreenState()