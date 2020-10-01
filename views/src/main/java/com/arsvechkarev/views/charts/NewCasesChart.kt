package com.arsvechkarev.views.charts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.arsvechkarev.views.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import core.extenstions.f
import core.extenstions.toFormattedShortString
import core.model.DailyCase

class NewCasesChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs),
  OnChartValueSelectedListener {
  
  private var dailyCaseListener: (dailyCase: DailyCase) -> Unit = { _ -> }
  private var newDailyCases: List<DailyCase>? = null
  
  init {
    setOnChartValueSelectedListener(this)
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
    this.newDailyCases = dailyCases
    val entries = createEntries(dailyCases)
    val lineDataSet = LineDataSet(entries, "")
    lineDataSet.apply {
      setDrawCircles(false)
      this.color = ContextCompat.getColor(context, R.color.dark_confirmed)
      setDrawFilled(true)
      highLightColor = Color.WHITE
      val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(color, Color.TRANSPARENT))
      fillDrawable = gradient
      setDrawHorizontalHighlightIndicator(false)
      setGradientColor(color, Color.TRANSPARENT)
    }
    data = LineData(lineDataSet).apply {
      setDrawValues(false)
    }
    invalidate()
    post { highlightValue(highlighter.getHighlight(width.f, 0f), true); }
  }
  
  fun onDailyCaseClicked(listener: (dailyCase: DailyCase) -> Unit) {
    this.dailyCaseListener = listener
  }
  
  override fun onValueSelected(e: Entry, h: Highlight) {
    dailyCaseListener.invoke(newDailyCases!![e.x.toInt()])
  }
  
  override fun onNothingSelected() {
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
      return newDailyCases!![value.toInt()].date
    }
  }
}
