package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.view.View
import core.Application.decimalFormatter
import core.Application.numberFormatter
import core.FontManager
import core.extenstions.execute
import core.extenstions.f

@SuppressLint("ViewConstructor")
class StatsSmallView(
  context: Context,
  private val textSize: Float = context.resources.getDimension(R.dimen.text_h4),
  private val color: Int = Color.WHITE
) : View(context) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = this@StatsSmallView.color
    typeface = FontManager.segoeUI
    this.textSize = this@StatsSmallView.textSize
  }
  
  private lateinit var text: String
  private var textLayout: Layout? = null
  private var numberLayout: Layout? = null
  private var amountLayout: Layout? = null
  private var numberLayoutMaxWidth = 0f
  
  fun updateData(number: Int, text: String, amount: Number) {
    this.text = text
    numberLayout = boringLayoutOf(textPaint, "$number.")
    numberLayoutMaxWidth = textPaint.measureText(RANK_TEXT_FOR_MEASURE)
    val amountString = when (amount) {
      is Double -> decimalFormatter.format(amount)
      is Float -> decimalFormatter.format(amount)
      else -> numberFormatter.format(amount)
    }
    amountLayout = boringLayoutOf(textPaint, amountString)
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textLineHeight = maxOf(textLayout?.height ?: 0, amountLayout?.height ?: 0).f
    val measuredHeight = paddingTop + textLineHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (numberLayout == null || amountLayout == null) {
      return
    }
    textLayout = boringLayoutOf(textPaint, text,
      (width - numberLayoutMaxWidth - amountLayout!!.width) * 0.85f)
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
    const val NUMBER_TEXT_FOR_MEASURE = "000,000,000"
  }
}