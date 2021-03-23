package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import base.views.charts.DailyCasesChart
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import org.hamcrest.Matcher

class KDailyCasesChart : KBaseView<KDailyCasesChart>, DailyCasesChartAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface DailyCasesChartAssertions : BaseAssertions {
  
  fun hasEntryForIndex(index: Int, entryX: Float, entryY: Float) {
    val description = "has entry ($entryX, $entryY) at index $index"
    view.matches<DailyCasesChart>(description) { chart ->
      val entryForIndex = chart.lineData.dataSets[0].getEntryForIndex(index)
      return@matches entryForIndex.x == entryX && entryForIndex.y == entryY
    }
  }
}