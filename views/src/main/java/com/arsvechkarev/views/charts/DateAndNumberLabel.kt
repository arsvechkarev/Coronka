package com.arsvechkarev.views.charts

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.arsvechkarev.viewdsl.DURATION_LONG
import core.Application
import core.extenstions.f
import core.extenstions.getTextHeight
import core.model.DailyCase
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

class DateAndNumberLabel @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private var dateTextHeight = 0
  private var numberTextHeight = 0
  
  private val dateTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    color = Colors.TextPrimary
    typeface = Fonts.SegoeUiBold
    textSize = TextSizes.H4
  }
  
  private val numberTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    color = Colors.Confirmed
    typeface = Fonts.SegoeUiBold
    textSize = TextSizes.H3
  }
  
  private val animator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator()
    duration = DURATION_LONG
    addUpdateListener {
      currentNumber = (it.animatedFraction * resultNumber).toInt()
      numberText = Application.numberFormatter.format(currentNumber)
      invalidate()
    }
  }
  private var currentNumber = 0
  private var resultNumber = 0
  
  private var dateText: String? = null
  private var numberText: String? = null
  
  fun drawCase(dailyCase: DailyCase) {
    if (dateText == null || numberText == null) {
      animateAppearance(dailyCase)
    } else {
      dateText = dailyCase.date
      numberText = Application.numberFormatter.format(dailyCase.cases)
      invalidate()
    }
  }
  
  private fun animateAppearance(dailyCase: DailyCase) {
    dateText = dailyCase.date
    resultNumber = dailyCase.cases
    currentNumber = 0
    animator.setFloatValues(0f, 1f)
    animator.start()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    // Measuring with blank data
    val stubNumber = "000 000 000"
    val stubDate = "Aug 99"
    numberTextHeight = numberTextPaint.getTextHeight(stubNumber)
    var height = numberTextHeight
    dateTextHeight = dateTextPaint.getTextHeight(stubDate)
    height += dateTextHeight
    height = (height * 1.5).toInt()
    setMeasuredDimension(
      resolveSize(numberTextPaint.measureText(stubNumber).toInt(), widthMeasureSpec),
      resolveSize(height, heightMeasureSpec)
    )
  }
  
  override fun onDraw(canvas: Canvas) {
    if (dateText == null || numberText == null) {
      return
    }
    val dateText = dateText!!
    val numberText = numberText!!
    canvas.drawText(numberText, 0, numberText.length, width / 2f, height.f, numberTextPaint)
    canvas.drawText(dateText, 0, dateText.length, width / 2f, dateTextHeight.f, dateTextPaint)
  }
}