package com.arsvechkarev.coronka.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KMyRecyclerView
import com.arsvechkarev.news.presentation.NewsFragment.Companion.ButtonRetry
import com.arsvechkarev.news.presentation.NewsFragment.Companion.ErrorLayout
import com.arsvechkarev.news.presentation.NewsFragment.Companion.RecyclerView

class NewsScreen : Screen<NewsScreen>() {
  
  val recyclerView = KMyRecyclerView { withTag(RecyclerView) }
  val buttonRetry = KView { withTag(ButtonRetry) }
  val errorLayout = KView { withTag(ErrorLayout) }
}