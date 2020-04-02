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
import core.extenstions.f
import core.extenstions.sp

@SuppressLint("ViewConstructor")
class SmallStatsView(
  context: Context,
  private val textSize: Float = 18.sp,
  private val color: Int = Color.WHITE
) : View(context) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = this@SmallStatsView.color
    typeface = FontManager.rubik
    this.textSize = this@SmallStatsView.textSize
  }
  private var numberLayout: Layout? = null
  private var textLayout: Layout? = null
  private var amountLayout: Layout? = null
  
  fun updateData(number: Int, text: String, amount: Int) {
    numberLayout = boringLayout("$number.  ")
    textLayout = boringLayout(text)
    amountLayout = boringLayout(amount.toString())
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textLineHeight = maxOf(textLayout?.height ?: 0, amountLayout?.height ?: 0).f
    val measuredHeight = paddingTop + textLineHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (numberLayout == null || textLayout == null || amountLayout == null) {
      return
    }
    val numberLayout = numberLayout!!
    val textLayout = textLayout!!
    val amountLayout = amountLayout!!
    canvas.block {
      translate(paddingStart.f, paddingTop.f)
      numberLayout.draw(canvas)
      translate(numberLayout.width.f, 0f)
      textLayout.draw(canvas)
      translate(
        width.f - amountLayout.width - numberLayout.width - paddingStart.f - paddingEnd.f,
        0f)
      amountLayout.draw(canvas)
    }
  }
  
  private fun boringLayout(text: CharSequence): Layout {
    val metrics = BoringLayout.isBoring(text, textPaint)
    return BoringLayout.make(text, textPaint, metrics.width,
      Layout.Alignment.ALIGN_NORMAL, 0f, 0f, metrics, false)
  }
}