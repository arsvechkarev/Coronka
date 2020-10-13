package com.arsvechkarev.news.presentation

import core.recycler.DifferentiableItem
import core.state.BaseScreenState

class LoadedNews(
  val news: List<DifferentiableItem>
) : BaseScreenState()