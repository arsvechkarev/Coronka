package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.view.View
import core.FontManager
import core.extenstions.block
import core.extenstions.dp
import core.extenstions.f
import core.extenstions.sp

@SuppressLint("ViewConstructor")
class SmallStatsView(
  context: Context,
  private val innerSidePadding: Float = 8.dp,
  private val chartLineCornersRadius: Float = 4.dp,
  private val chartLineHeight: Float = 6.dp,
  private val textSize: Float = 16.sp
) : View(context) {
  
  private val minChartLinePercent = 0.04f
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    typeface = FontManager.rubik
    this.textSize = this@SmallStatsView.textSize
  }
  private var textLayout: Layout? = null
  private var textLineHeight: Float = 0f
  
  private var numberLayout: Layout? = null
  private var linePercent = -1f
  private var chartLineMaxLength = -1f
  private val chartLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  fun updateData(text: String, number: Int, percent: Float, color: Int) {
    chartLinePaint.color = color
    textLayout = boringLayout(text)
    numberLayout = boringLayout(number.toString())
    linePercent = calculateLinePercent(number.toFloat(), percent)
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val maxLineHeight = maxOf(textLayout?.height ?: 0, numberLayout?.height ?: 0).f
    val measuredHeight = paddingTop + maxLineHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (textLayout == null || numberLayout == null) {
      return
    }
    val textLayout = this.textLayout!!
    val numberLayout = this.numberLayout!!
    if (chartLineMaxLength == -1f) {
      chartLineMaxLength = width.f - textLayout.width - numberLayout.width -
          paddingStart - paddingEnd - innerSidePadding * 2
    }
    canvas.block {
      translate(paddingStart.f, paddingTop.f)
      textLayout.draw(canvas)
      translate(
        textLayout.width + innerSidePadding,
        (textLineHeight / 2 - chartLineHeight / 2)
      )
      val lineLength = linePercent * chartLineMaxLength
      drawRoundRect(0f, 0f, lineLength, chartLineHeight,
        chartLineCornersRadius, chartLineCornersRadius, chartLinePaint)
      translate(lineLength + innerSidePadding, 0f)
      numberLayout.draw(canvas)
    }
  }
  
  private fun calculateLinePercent(number: Float, percent: Float): Float {
    if (number == 0f) {
      return 0f
    }
    if (percent >= minChartLinePercent) {
      return percent
    }
    return percent + minChartLinePercent
  }
  
  private fun boringLayout(text: CharSequence, maxWidth: Int = -1): Layout {
    val metrics = BoringLayout.isBoring(text, textPaint)
    val maxWidthInternal = if (maxWidth == -1) metrics.width else maxWidth
    return BoringLayout.make(text, textPaint, maxWidthInternal,
      Layout.Alignment.ALIGN_NORMAL, 0f, 0f, metrics, false)
  }
}