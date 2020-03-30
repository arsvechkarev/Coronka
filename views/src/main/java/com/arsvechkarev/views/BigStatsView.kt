package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.BoringLayout
import android.text.Layout
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import core.Colors
import core.FontManager
import core.extenstions.block
import core.extenstions.dp
import core.extenstions.f
import core.extenstions.i
import core.extenstions.sp

class BigStatsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val innerSidePadding: Float
  private val innerLinePadding: Float
  private val chartLineCornersRadius: Float
  private val minChartLinePercent = 0.04f
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    typeface = FontManager.rubik
  }
  
  private val confirmedLabel: Layout
  private val recoveredLabel: Layout
  private val deathsLabel: Layout
  private var confirmedNumberLayout: Layout? = null
  private var recoveredNumberLayout: Layout? = null
  private var deathsNumberLayout: Layout? = null
  
  private var total = -1f
  private var confirmedLinePercent = -1f
  private var recoveredLinePercent = -1f
  private var deathsLinePercent = -1f
  
  private var lineHeight: Float = 0f
  private var chartLineMaxLength = -1f
  private val chartLineHeight: Float
  private val chartLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  init {
    val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.BigStatsView, 0, 0)
    innerSidePadding = attributes.getDimension(R.styleable.BigStatsView_innerSidePadding, dp(8))
    innerLinePadding = attributes.getDimension(R.styleable.BigStatsView_innerLinePadding, dp(8))
    chartLineHeight = attributes.getDimension(R.styleable.BigStatsView_lineHeight, dp(6))
    chartLineCornersRadius = attributes
        .getDimension(R.styleable.BigStatsView_lineCornersRadius, dp(4))
    textPaint.textSize = attributes.getDimension(R.styleable.BigStatsView_android_textSize, sp(20))
    val labelConfirmed = resources.getString(R.string.text_confirmed)
    val labelRecovered = resources.getString(R.string.text_recovered)
    val labelDeaths = resources.getString(R.string.text_deaths)
    val maxNumberTextWidth = maxOf(
      textPaint.measureText(labelConfirmed),
      textPaint.measureText(labelRecovered),
      textPaint.measureText(labelDeaths)
    ).toInt()
    confirmedLabel = boringLayout(labelConfirmed, ALIGN_NORMAL, maxNumberTextWidth)
    recoveredLabel = boringLayout(labelRecovered, ALIGN_NORMAL, maxNumberTextWidth)
    deathsLabel = boringLayout(labelDeaths, ALIGN_NORMAL, maxNumberTextWidth)
    attributes.recycle()
  }
  
  fun updateNumbers(confirmed: Int, recovered: Int, deaths: Int) {
    calculateLengths(confirmed.f, recovered.f, deaths.f)
    val maxNumberTextWidth = maxOf(
      textPaint.measureText(confirmed.toString()),
      textPaint.measureText(recovered.toString()),
      textPaint.measureText(deaths.toString())
    ).toInt()
    confirmedNumberLayout = boringLayout(confirmed.toString(), ALIGN_CENTER, maxNumberTextWidth)
    recoveredNumberLayout = boringLayout(recovered.toString(), ALIGN_CENTER, maxNumberTextWidth)
    deathsNumberLayout = boringLayout(deaths.toString(), ALIGN_CENTER, maxNumberTextWidth)
    chartLineMaxLength = -1f
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    lineHeight = maxOf(confirmedLabel.height, recoveredLabel.height,
      maxOf(deathsLabel.height, chartLineHeight.i)).f
    val measuredHeight = paddingTop + lineHeight * 3 + innerLinePadding * 2 + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (confirmedNumberLayout == null
        || recoveredNumberLayout == null
        || deathsNumberLayout == null) {
      return
    }
    if (chartLineMaxLength == -1f) {
      chartLineMaxLength = width.f - confirmedLabel.width - confirmedNumberLayout!!.width -
          paddingStart - paddingEnd - innerSidePadding * 2
    }
    canvas.block {
      translate(paddingStart.f, paddingTop.f)
      confirmedLabel.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      recoveredLabel.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      deathsLabel.draw(canvas)
    }
    canvas.block {
      translate(
        paddingStart.f + confirmedLabel.width + innerSidePadding,
        paddingTop.f + (lineHeight / 2 - chartLineHeight / 2)
      )
      drawLine(Colors.confirmedColor, confirmedLinePercent)
      translate(0f, lineHeight + innerLinePadding)
      drawLine(Colors.recoveredColor, recoveredLinePercent)
      translate(0f, lineHeight + innerLinePadding)
      drawLine(Colors.deathsColor, deathsLinePercent)
    }
    canvas.block {
      translate(width - paddingEnd.f - confirmedNumberLayout!!.width, paddingTop.f)
      confirmedNumberLayout!!.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      recoveredNumberLayout!!.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      deathsNumberLayout!!.draw(canvas)
    }
  }
  
  private fun boringLayout(text: CharSequence, alignment: Layout.Alignment, maxWidth: Int = -1): Layout {
    val metrics = BoringLayout.isBoring(text, textPaint)
    val maxWidthInternal = if (maxWidth == -1) metrics.width else maxWidth
    return BoringLayout.make(text, textPaint, maxWidthInternal,
      alignment, 0f, 0f, metrics, false)
  }
  
  private fun calculateLengths(confirmed: Float, recovered: Float, deaths: Float) {
    total = confirmed + recovered + deaths
    confirmedLinePercent = calculateLinePercent(confirmed, total)
    recoveredLinePercent = calculateLinePercent(recovered, total)
    deathsLinePercent = calculateLinePercent(deaths, total)
  }
  
  private fun calculateLinePercent(number: Float, total: Float): Float {
    if (number == 0f) {
      return 0f
    }
    val linePercent = number / total
    if (linePercent >= minChartLinePercent) {
      return linePercent
    }
    return linePercent + minChartLinePercent
  }
  
  private fun Canvas.drawLine(color: Int, linePercent: Float) {
    chartLinePaint.color = color
    val lineLength = linePercent * chartLineMaxLength
    drawRoundRect(0f, 0f, lineLength, chartLineHeight,
      chartLineCornersRadius, chartLineCornersRadius, chartLinePaint)
  }
}