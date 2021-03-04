package com.arsvechkarev.coronka.screens

import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KDailyCasesChart
import com.arsvechkarev.coronka.customviews.KDateAndNumberLabel
import com.arsvechkarev.coronka.customviews.KGeneralStatsView
import com.arsvechkarev.coronka.withStringId

class StatsScreen : Screen<StatsScreen>() {
  
  val iconDrawer = KImageView { withStringId("statsIconDrawer") }
  val generalStatsView = KGeneralStatsView { withStringId("statsGeneralStatsView") }
  val totalCasesLabel = KDateAndNumberLabel { withStringId("statsTotalCasesLabel") }
  val totalCasesChart = KDailyCasesChart { withStringId("statsTotalCasesChart") }
  val newCasesLabel = KDateAndNumberLabel { withStringId("statsNewCasesLabel") }
  val newCasesChart = KDailyCasesChart { withStringId("statsNewCasesChart") }
}