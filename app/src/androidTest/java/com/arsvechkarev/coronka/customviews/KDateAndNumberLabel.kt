package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import base.views.charts.DateAndNumberLabel
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import org.hamcrest.Matcher

class KDateAndNumberLabel : KBaseView<KDateAndNumberLabel>, DateAndNumberLabelAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface DateAndNumberLabelAssertions : BaseAssertions {
  
  fun hasDateText(text: String) {
    view.matches<DateAndNumberLabel> { label -> label.dateText == text }
  }
  
  fun hasNumberText(text: String) {
    view.matches<DateAndNumberLabel> { label -> label.numberText == text }
  }
}