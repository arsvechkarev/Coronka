package com.arsvechkarev.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import core.extenstions.toFormattedShortString
import core.model.DailyCase

class TotalCasesChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs) {
  
  private var totalDailyCases: List<DailyCase>? = null
  
  init {
    xAxis.apply {
      valueFormatter = DateAxisFormatter()
      setDrawGridLines(false)
      setLabelCount(4, true)
      textColor = ContextCompat.getColor(context, R.color.dark_text_primary)
      position = XAxisPosition.BOTTOM
    }
    axisRight.apply {
      axisMinimum = 0f
      setDrawGridLines(false)
      textColor = ContextCompat.getColor(context, R.color.dark_text_primary)
      valueFormatter = YAxisFormatter
    }
    axisLeft.apply {
      axisMinimum = 0f
      isEnabled = false
    }
    description.isEnabled = false
    legend.isEnabled = false
    isDoubleTapToZoomEnabled = false
    setScaleEnabled(false)
  }
  
  fun update(dailyCases: List<DailyCase>) {
    this.totalDailyCases = dailyCases
    val entries = createEntries(dailyCases)
    val lineDataSet = LineDataSet(entries, "")
    lineDataSet.apply {
      setDrawCircles(false)
      this.color = ContextCompat.getColor(context, R.color.dark_confirmed)
      setDrawFilled(true)
      val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(color, Color.TRANSPARENT))
      fillDrawable = gradient
      setDrawHorizontalHighlightIndicator(false)
      setDrawVerticalHighlightIndicator(false)
      setGradientColor(color, Color.TRANSPARENT)
    }
    data = LineData(lineDataSet).apply {
      setDrawValues(false)
    }
    invalidate()
    animateY(1000)
  }
  
  private fun createEntries(dailyCases: List<DailyCase>): List<Entry> {
    val entries = ArrayList<Entry>()
    dailyCases.forEachIndexed { i, case ->
      entries.add(Entry(i.toFloat(), case.cases.toFloat()))
    }
    return entries
  }
  
  private object YAxisFormatter : ValueFormatter() {
    
    override fun getFormattedValue(value: Float) = value.toFormattedShortString()
  }
  
  private inner class DateAxisFormatter : ValueFormatter() {
    
    override fun getFormattedValue(value: Float): String {
      return totalDailyCases!![value.toInt()].date
    }
  }
}
