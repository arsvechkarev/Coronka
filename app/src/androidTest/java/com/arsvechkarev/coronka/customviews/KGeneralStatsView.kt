package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import com.arsvechkarev.views.generalstatsviews.MainGeneralStatsView
import core.extenstions.formatGeneralInfo
import core.model.GeneralInfo
import org.hamcrest.Matcher

class KGeneralStatsView : KBaseView<KGeneralStatsView>, GeneralStatsViewAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface GeneralStatsViewAssertions : BaseAssertions {
  
  fun hasGeneralInfo(generalInfo: GeneralInfo) {
    view.matches<MainGeneralStatsView>("has general info $generalInfo") { item ->
      item.confirmedNumber == generalInfo.confirmed.formatGeneralInfo(item.context)
          && item.recoveredNumber == generalInfo.recovered.formatGeneralInfo(item.context)
          && item.deathsNumber == generalInfo.deaths.formatGeneralInfo(item.context)
    }
  }
}