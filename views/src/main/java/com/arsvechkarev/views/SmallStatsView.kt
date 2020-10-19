package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.view.View
import core.extenstions.execute
import core.extenstions.f
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

@SuppressLint("ViewConstructor")
class SmallStatsView(
  context: Context,
  private val textSize: Float = TextSizes.H4,
  private val textColor: Int = Colors.TextPrimary
) : View(context) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = textColor
    typeface = Fonts.SegoeUiBold
    textSize = this@SmallStatsView.textSize
  }
  
  private var text: String? = null
  private var textLayout: Layout? = null
  private var numberLayout: Layout? = null
  private var amountLayout: Layout? = null
  private var numberLayoutMaxWidth = 0f
  
  fun updateData(rankNumber: Int, text: String, amount: String) {
    this.text = text
    textLayout = null
    numberLayout = boringLayoutOf(textPaint, "$rankNumber.")
    numberLayoutMaxWidth = textPaint.measureText(RANK_TEXT_FOR_MEASURE)
    amountLayout = boringLayoutOf(textPaint, amount)
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textLineHeight = maxOf(textLayout?.height ?: 0, amountLayout?.height ?: 0).f
    val measuredHeight = paddingTop + textLineHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    requestLayout()
  }
  
  override fun onDraw(canvas: Canvas) {
    if (numberLayout == null || amountLayout == null || text == null) {
      return
    }
    if (textLayout == null) {
      textLayout = boringLayoutOf(textPaint, text!!,
        (width - numberLayoutMaxWidth - amountLayout!!.width) * 0.85f)
    }
    val numberLayout = numberLayout!!
    val textLayout = textLayout!!
    val amountLayout = amountLayout!!
    canvas.execute {
      translate(paddingStart.f, paddingTop.f)
      numberLayout.draw(canvas)
      translate(numberLayoutMaxWidth, 0f)
      textLayout.draw(canvas)
      translate(
        width.f - amountLayout.width - numberLayoutMaxWidth - paddingStart.f - paddingEnd.f,
        0f)
      amountLayout.draw(canvas)
    }
  }
  
  companion object {
  
    const val RANK_TEXT_FOR_MEASURE = "000.000"
  }
}