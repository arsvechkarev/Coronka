package com.arsvechkarev.news.presentation

import core.BaseScreenState
import core.recycler.DifferentiableItem

class LoadedNews(
  val news: List<DifferentiableItem>
) : BaseScreenState()