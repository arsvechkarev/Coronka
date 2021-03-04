package com.arsvechkarev.coronka.screens

import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KGeneralStatsView
import com.arsvechkarev.coronka.withStringId

class StatsScreen : Screen<StatsScreen>() {
  
  val statsIconDrawer = KImageView { withStringId("statsIconDrawer") }
  val statsGeneralStatsView = KGeneralStatsView { withStringId("statsGeneralStatsView") }
}