package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import core.extenstions.toFormattedShortString
import core.model.DailyCase

class NewCasesChart(context: Context, attrs: AttributeSet) : BarChart(context, attrs) {
  
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
    val barDataSet = BarDataSet(entries, "")
    barDataSet.apply {
      this.color = ContextCompat.getColor(context, R.color.dark_confirmed)
    }
    data = BarData(barDataSet).apply {
      setDrawValues(false)
    }
    invalidate()
    animateY(1000)
  }
  
  private fun createEntries(dailyCases: List<DailyCase>): List<BarEntry> {
    val entries = ArrayList<BarEntry>()
    dailyCases.forEachIndexed { i, case ->
      entries.add(BarEntry(i.toFloat(), case.cases.toFloat()))
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
