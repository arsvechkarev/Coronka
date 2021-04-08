package com.arsvechkarev.coronka.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KMyRecyclerView
import com.arsvechkarev.rankings.R

class RankingsScreen : Screen<RankingsScreen>() {
  
  val recyclerCountries = KMyRecyclerView { withId(R.id.rankingsRecyclerView) }
  val fabFilter = KView { withId(R.id.rankingsFabFilter) }
  val bottomSheet = KView { withId(R.id.rankingsFilterDialog) }
  val bottomSheetCross = KView { withId(R.id.rankingsFilterDialogCross) }
  
  val chipWorldwide = KView { withId(R.id.chipWorldwide) }
  val chipEurope = KView { withId(R.id.chipEurope) }
  
  val chipConfirmed = KView { withId(R.id.chipConfirmed) }
  val chipRecovered = KView { withId(R.id.chipRecovered) }
}