package com.arsvechkarev.coronka.screens

import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KDailyCasesChart
import com.arsvechkarev.coronka.customviews.KDateAndNumberLabel
import com.arsvechkarev.coronka.customviews.KGeneralStatsView
import com.arsvechkarev.stats.R

class StatsScreen : Screen<StatsScreen>() {
  
  val iconDrawer = KImageView { withId(R.id.statsIconDrawer) }
  val generalStatsView = KGeneralStatsView { withId(R.id.statsGeneralStatsView) }
  val totalCasesLabel = KDateAndNumberLabel { withId(R.id.statsTotalCasesLabel) }
  val totalCasesChart = KDailyCasesChart { withId(R.id.statsTotalCasesChart) }
  val newCasesLabel = KDateAndNumberLabel { withId(R.id.statsNewCasesLabel) }
  val newCasesChart = KDailyCasesChart { withId(R.id.statsNewCasesChart) }
}