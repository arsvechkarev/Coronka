package com.arsvechkarev.map.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.BoringLayout
import android.text.Layout
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.map.R
import core.extenstions.block
import core.extenstions.dp
import core.extenstions.f
import core.extenstions.i
import core.extenstions.sp

class StatsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val innerSidePadding: Float
  private val innerLinePadding: Float
  private val chartLineCornersRadius: Float
  private val minChartLineLength = dp(4)
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
  
  private val confirmedLabel: Layout
  private val recoveredLabel: Layout
  private val deathsLabel: Layout
  private var confirmedNumberLayout: Layout? = null
  private var recoveredNumberLayout: Layout? = null
  private var deathsNumberLayout: Layout? = null
  
  private var total = -1f
  private var confirmed = -1f
  private var recovered = -1f
  private var deaths = -1f
  
  private var lineHeight: Float = 0f
  private var chartLineMaxLength = -1f
  private val chartLineHeight: Float
  private val chartLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  init {
    val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.StatsView, 0, 0)
    innerSidePadding = typedArray.getDimension(R.styleable.StatsView_innerSidePadding, dp(8))
    innerLinePadding = typedArray.getDimension(R.styleable.StatsView_innerLinePadding, dp(6))
    chartLineHeight = typedArray.getDimension(R.styleable.StatsView_lineHeight, dp(6))
    chartLineCornersRadius = typedArray.getDimension(R.styleable.StatsView_lineCornersRadius, dp(4))
    textPaint.textSize = typedArray.getDimension(R.styleable.StatsView_android_textSize, sp(18))
    val labelConfirmed = resources.getString(R.string.text_confirmed)
    val labelRecovered = resources.getString(R.string.text_recovered)
    val labelDeaths = resources.getString(R.string.text_deaths)
    val maxNumberTextWidth = maxOf(
      textPaint.measureText(labelConfirmed),
      textPaint.measureText(labelRecovered),
      textPaint.measureText(labelDeaths)
    ).toInt()
    confirmedLabel = boringLayout(labelConfirmed, maxNumberTextWidth)
    recoveredLabel = boringLayout(labelRecovered, maxNumberTextWidth)
    deathsLabel = boringLayout(labelDeaths, maxNumberTextWidth)
  }
  
  fun setNumbers(confirmed: Int, recovered: Int, deaths: Int) {
    this.confirmed = confirmed.f
    this.recovered = recovered.f
    this.deaths = deaths.f
    total = (confirmed + recovered + deaths).f
    val maxNumberTextWidth = maxOf(
      textPaint.measureText(confirmed.toString()),
      textPaint.measureText(recovered.toString()),
      textPaint.measureText(deaths.toString())
    ).toInt()
    confirmedNumberLayout = boringLayout(confirmed.toString(), maxNumberTextWidth)
    recoveredNumberLayout = boringLayout(recovered.toString(), maxNumberTextWidth)
    deathsNumberLayout = boringLayout(deaths.toString(), maxNumberTextWidth)
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (textsAreNotInitialized()) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    } else {
      lineHeight = maxOf(confirmedLabel.height, recoveredLabel.height,
        maxOf(deathsLabel.height, chartLineHeight.i)).f
      val measuredHeight = paddingTop + lineHeight * 3 + innerLinePadding * 2 + paddingBottom
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight.toInt())
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    if (textsAreNotInitialized()) {
      return
    }
    chartLineMaxLength = w.f - confirmedLabel.width - confirmedNumberLayout!!.width -
        paddingStart - paddingEnd - innerSidePadding * 2
  }
  
  override fun onDraw(canvas: Canvas) {
    if (textsAreNotInitialized()) {
      return
    }
    canvas.drawRect(0f, 0f, width.f, height.f, Paint().apply {
      style = Paint.Style.STROKE
      color = Color.RED
      strokeWidth = 6f
    })
    canvas.block {
      translate(paddingStart.f, 0f)
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
      drawLine(Color.RED, confirmed)
      translate(0f, lineHeight + innerLinePadding)
      drawLine(Color.YELLOW, recovered)
      translate(0f, lineHeight + innerLinePadding)
      drawLine(Color.BLACK, deaths)
    }
    canvas.block {
      translate(width - paddingEnd.f - confirmedNumberLayout!!.width, 0f)
      confirmedNumberLayout!!.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      recoveredNumberLayout!!.draw(canvas)
      translate(0f, lineHeight + innerLinePadding)
      deathsNumberLayout!!.draw(canvas)
    }
  }
  
  private fun Canvas.drawLine(color: Int, number: Float) {
    chartLinePaint.color = color
    var lineLength = chartLineMaxLength * (number / total)
    if (lineLength < minChartLineLength) lineLength *= 2
    drawRoundRect(0f, 0f, lineLength, chartLineHeight,
      chartLineCornersRadius, chartLineCornersRadius, chartLinePaint)
  }
  
  private fun textsAreNotInitialized(): Boolean {
    return confirmedNumberLayout == null || recoveredNumberLayout == null || deathsNumberLayout == null
  }
  
  private fun boringLayout(text: CharSequence, maxWidth: Int = -1): Layout {
    val metrics = BoringLayout.isBoring(text, textPaint)
    val maxWidthInternal = if (maxWidth == -1) metrics.width else maxWidth
    return BoringLayout.make(text, textPaint, maxWidthInternal,
      ALIGN_NORMAL, 0f, 0f, metrics, false)
  }
}