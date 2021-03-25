package base.views.charts

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import base.extensions.f
import base.extensions.getTextHeight
import base.extensions.toFormattedNumber
import base.extensions.toFormattedTextLabelDate
import base.resources.Colors
import base.resources.Fonts
import base.resources.TextSizes
import config.AnimationsConfigurator
import core.di.CoreComponent
import core.model.ui.DailyCase
import kotlin.math.roundToInt

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
    duration = AnimationsConfigurator.DurationLong
    addUpdateListener {
      val fraction = it.animatedValue as Float
      currentNumber = (fraction * resultNumber).roundToInt()
      if (fraction == 1f) {
        // When fraction is 1f, setting number text directly from
        // result number to prevent rounding errors
        numberText = resultNumber.toFormattedNumber()
      } else {
        numberText = currentNumber.toFormattedNumber()
      }
      invalidate()
    }
  }
  private var currentNumber = 0
  private var resultNumber = 0
  
  var dateText: String? = null
    private set
  var numberText: String? = null
    private set
  
  fun drawCase(dailyCase: DailyCase) {
    CoreComponent
    if (dateText == null || numberText == null) {
      animateAppearance(dailyCase)
    } else {
      dateText = dailyCase.date.toFormattedTextLabelDate()
      numberText = dailyCase.cases.toFormattedNumber()
      invalidate()
    }
  }
  
  private fun animateAppearance(dailyCase: DailyCase) {
    dateText = dailyCase.date.toFormattedTextLabelDate()
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