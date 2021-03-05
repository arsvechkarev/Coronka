package com.arsvechkarev.coronka.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.coronka.customviews.KMyRecycleView
import com.arsvechkarev.coronka.withStringId

class RankingsScreen : Screen<RankingsScreen>() {
  
  val recyclerCountries = KMyRecycleView { withStringId("rankingsRecyclerView") }
  val fabFilter = KView { withStringId("rankingsFabFilter") }
  val bottomSheet = KView { withStringId("rankingsBottomSheet") }
  val bottomSheetCross = KView { withStringId("rankingsBottomSheetCross") }
  
  val chipWorldwide = KView { withStringId("chipWorldwide") }
  val chipEurope = KView { withStringId("chipEurope") }
  
  val chipConfirmed = KView { withStringId("chipConfirmed") }
  val chipRecovered = KView { withStringId("chipRecovered") }
}