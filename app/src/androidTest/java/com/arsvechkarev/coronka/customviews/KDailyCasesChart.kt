package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.assertion.ViewAssertions
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.agoda.kakao.common.views.KView
import com.arsvechkarev.views.charts.DailyCasesChart
import core.extenstions.assertThat
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class KDailyCasesChart : KBaseView<KView>, DailyCasesChartAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface DailyCasesChartAssertions : BaseAssertions {
  
  fun hasEntryForIndex(index: Int, entryX: Any, entryY: Any) {
    view.check(ViewAssertions.matches(object : BaseMatcher<View>() {
      
      override fun describeTo(description: Description) {
        description.appendText("has entry ($entryX, $entryY) at index $index")
      }
      
      override fun matches(item: Any): Boolean {
        assertThat(item is DailyCasesChart) { "Expected DailyCasesChart, but received $item" }
        val entryForIndex = item.lineData.dataSets[0].getEntryForIndex(index)
        val formattedX = item.xAxis.valueFormatter.getFormattedValue(entryForIndex.x)
        val formattedY = item.axisRight.valueFormatter.getFormattedValue(entryForIndex.y)
        return formattedX == entryX && formattedY == entryY
      }
      
      override fun describeMismatch(item: Any, mismatchDescription: Description) {
        assertThat(item is DailyCasesChart)
        mismatchDescription.appendText("Expected entry ($entryX, $entryY), at index $index")
      }
    }))
  }
}