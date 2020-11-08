package com.arsvechkarev.views.charts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import core.extenstions.toFormattedShortString
import core.model.DailyCase
import core.viewbuilding.Colors

class DailyCasesChart @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LineChart(context, attrs), OnChartValueSelectedListener {
  
  private var dailyCaseListener: (dailyCase: DailyCase) -> Unit = { _ -> }
  private var dailyCases: List<DailyCase>? = null
  
  var onDown: () -> Unit = {}
  var onUp: () -> Unit = {}
  
  init {
    setOnChartValueSelectedListener(this)
    xAxis.apply {
      valueFormatter = DateAxisFormatter()
      setDrawGridLines(false)
      setLabelCount(4, true)
      textColor = Colors.TextPrimary
      position = XAxisPosition.BOTTOM
    }
    axisRight.apply {
      axisMinimum = 0f
      setDrawGridLines(false)
      textColor = Colors.TextPrimary
      valueFormatter = YAxisFormatter()
    }
    axisLeft.apply {
      axisMinimum = 0f
      isEnabled = false
    }
    description.isEnabled = false
    setNoDataText("")
    legend.isEnabled = false
    isDoubleTapToZoomEnabled = false
    setScaleEnabled(false)
  }
  
  fun update(dailyCases: List<DailyCase>, offset: Int = 0) {
    this.dailyCases = dailyCases
    val entries = createEntries(dailyCases, offset)
    val lineDataSet = LineDataSet(entries, "")
    lineDataSet.apply {
      setDrawCircles(false)
      this.color = Colors.Confirmed
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
    post {
      val x = entries.last().x
      val y = entries.last().y
      highlightValue(Highlight(x, y, 0), true)
    }
  }
  
  fun onDailyCaseClicked(listener: (dailyCase: DailyCase) -> Unit) {
    this.dailyCaseListener = listener
  }
  
  override fun onValueSelected(e: Entry, h: Highlight) {
    dailyCaseListener.invoke(dailyCases!![e.x.toInt()])
  }
  
  override fun onNothingSelected() = Unit
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!isEnabled) return false
    if (event.action == ACTION_DOWN) onDown()
    if (event.action == ACTION_UP) onUp()
    return super.onTouchEvent(event)
  }
  
  private fun createEntries(dailyCases: List<DailyCase>, offset: Int): List<BarEntry> {
    val entries = ArrayList<BarEntry>()
    dailyCases.forEachIndexed { i, case ->
      if (i >= offset) {
        entries.add(BarEntry(i.toFloat(), case.cases.toFloat()))
      }
    }
    return entries
  }
  
  
  private inner class YAxisFormatter : ValueFormatter() {
    
    override fun getFormattedValue(value: Float): String {
      return if (value == 0f) "" else value.toFormattedShortString(context)
    }
  }
  
  private inner class DateAxisFormatter : ValueFormatter() {
    
    override fun getFormattedValue(value: Float): String {
      return dailyCases!![value.toInt()].date
    }
  }
}
