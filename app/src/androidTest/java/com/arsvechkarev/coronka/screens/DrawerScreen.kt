package com.arsvechkarev.coronka.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.presentation.TextMap
import com.arsvechkarev.coronka.presentation.TextNews
import com.arsvechkarev.coronka.presentation.TextRankings
import com.arsvechkarev.coronka.presentation.TextStatistics
import com.arsvechkarev.coronka.presentation.TextTips

class DrawerScreen : Screen<DrawerScreen>() {
  
  val textStatistics = KView { withTag(TextStatistics) }
  val textNews = KView { withTag(TextNews) }
  val textMap = KView { withTag(TextMap) }
  val textRankings = KView { withTag(TextRankings) }
  val textTips = KView { withTag(TextTips) }
}