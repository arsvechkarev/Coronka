package com.arsvechkarev.views.charts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
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
import core.extenstions.toFormattedShortString
import core.model.DailyCase

class DailyCasesChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs),
  OnChartValueSelectedListener {
  
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
      textColor = ContextCompat.getColor(context, R.color.dark_text_primary)
      position = XAxisPosition.BOTTOM
    }
    axisRight.apply {
      axisMinimum = 0f
      setDrawGridLines(false)
      textColor = ContextCompat.getColor(context, R.color.dark_text_primary)
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
  
  fun update(dailyCases: List<DailyCase>) {
    this.dailyCases = dailyCases
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
    if (event.action == ACTION_DOWN) onDown()
    if (event.action == ACTION_UP) onUp()
    return super.onTouchEvent(event)
  }
  
  private fun createEntries(dailyCases: List<DailyCase>): List<BarEntry> {
    val entries = ArrayList<BarEntry>()
    dailyCases.forEachIndexed { i, case ->
      entries.add(BarEntry(i.toFloat(), case.cases.toFloat()))
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
