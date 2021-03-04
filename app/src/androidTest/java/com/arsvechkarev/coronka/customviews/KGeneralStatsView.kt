package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.assertion.ViewAssertions
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.views.generalstatsviews.MainGeneralStatsView
import core.extenstions.assertThat
import core.extenstions.formatGeneralInfo
import core.model.GeneralInfo
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class KGeneralStatsView : KBaseView<KGeneralStatsView>, GeneralStatsAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface GeneralStatsAssertions : BaseAssertions {
  
  fun hasGeneralInfo(generalInfo: GeneralInfo) {
    view.check(ViewAssertions.matches(object : BaseMatcher<View>() {
      
      override fun describeTo(description: Description) {
        description.appendText("has general info $generalInfo")
      }
      
      override fun matches(item: Any): Boolean {
        assertThat(
          item is MainGeneralStatsView) { "Expected MainGeneralStatsView, but received $item" }
        return item.confirmedNumber == generalInfo.confirmed.formatGeneralInfo(item.context)
            && item.recoveredNumber == generalInfo.recovered.formatGeneralInfo(item.context)
            && item.deathsNumber == generalInfo.deaths.formatGeneralInfo(item.context)
      }
      
      override fun describeMismatch(item: Any, mismatchDescription: Description) {
      }
    }))
  }
}