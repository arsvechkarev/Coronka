package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import base.views.SmallStatsView
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import org.hamcrest.Matcher

class KSmallStatsView : KBaseView<KGeneralStatsView>, SmallStatsViewAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface SmallStatsViewAssertions : BaseAssertions {
  
  fun hasText(text: String) {
    view.matches<SmallStatsView> { item -> item.text == text }
  }
  
  fun hasNumber(formattedNumber: String) {
    view.matches<SmallStatsView> { item -> item.number == formattedNumber }
  }
  
  fun hasAmount(formattedAmount: String) {
    view.matches<SmallStatsView> { item -> item.amount == formattedAmount }
  }
}