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
  private val color: Int = Color.BLACK
) : View(context) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = this@SmallStatsView.color
    typeface = FontManager.rubik
    this.textSize = this@SmallStatsView.textSize
  }
  private var textLayout: Layout? = null
  private var textLineHeight: Float = 0f
  private var numberLayout: Layout? = null
  
  fun updateData(text: String, number: Int) {
    textLayout = boringLayout(text)
    numberLayout = boringLayout(number.toString())
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    textLineHeight = maxOf(textLayout?.height ?: 0, numberLayout?.height ?: 0).f
    val measuredHeight = paddingTop + textLineHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(measuredHeight.toInt(), heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (textLayout == null || numberLayout == null) {
      return
    }
    canvas.block {
      translate(paddingStart.f, paddingTop.f)
      textLayout!!.draw(canvas)
      translate(width.f - numberLayout!!.width - paddingStart.f - paddingEnd.f, 0f)
      numberLayout!!.draw(canvas)
    }
  }
  
  private fun boringLayout(text: CharSequence): Layout {
    val metrics = BoringLayout.isBoring(text, textPaint)
    return BoringLayout.make(text, textPaint, metrics.width,
      Layout.Alignment.ALIGN_NORMAL, 0f, 0f, metrics, false)
  }
}