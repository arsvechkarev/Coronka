package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import core.extenstions.formattedMillions
import core.extenstions.getTextHeight
import core.model.GeneralInfo
import core.viewbuilding.Colors
import core.viewbuilding.Fonts

class GeneralStatsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = Fonts.SegoeUiBold
    color = Colors.TextPrimary
    textAlign = Paint.Align.CENTER
  }
  private val squarePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  private var titleTextSize = -1f
  private var titleTextHeight = -1f
  private var numberTextSize = -1f
  
  private val confirmedTitle = context.getString(R.string.text_confirmed)
  private val recoveredTitle = context.getString(R.string.text_recovered)
  private val deathsTitle = context.getString(R.string.text_deaths)
  private var confirmedNumber: String? = null
  private var recoveredNumber: String? = null
  private var deathsNumber: String? = null
  
  fun updateNumbers(generalInfo: GeneralInfo) {
    confirmedNumber = generalInfo.confirmed.formattedMillions(context)
    recoveredNumber = generalInfo.recovered.formattedMillions(context)
    deathsNumber = generalInfo.deaths.formattedMillions(context)
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    setMeasuredDimension(
      widthMeasureSpec,
      resolveSize(getSquareSize(width), heightMeasureSpec)
    )
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    titleTextSize = getTextSizeFor(
      confirmedTitle, recoveredTitle, deathsTitle, w, textPaint
    )
    titleTextHeight = textPaint.getTextHeight(confirmedTitle).toFloat()
  }
  
  override fun onDraw(canvas: Canvas) {
    confirmedNumber ?: return
    numberTextSize = getTextSizeFor(
      confirmedNumber!!, recoveredNumber!!, deathsNumber!!, width, textPaint
    )
    val offset = getSquareMargin(width).toFloat()
    val squareSize = getSquareSize(width).toFloat()
    canvas.drawItem(Colors.Confirmed, confirmedTitle, confirmedNumber!!, 0f)
    canvas.drawItem(Colors.Recovered, recoveredTitle, recoveredNumber!!,
      squareSize + offset)
    canvas.drawItem(Colors.Deaths, deathsTitle, deathsNumber!!,
      squareSize * 2 + offset * 2)
  }
  
  private fun Canvas.drawItem(
    color: Int,
    title: String,
    number: String,
    start: Float
  ) {
    val itemSize = getSquareSize(width).toFloat()
    val radius = getSquareCornersRadius(width)
    squarePaint.color = color
    drawRoundRect(start, 0f, start + itemSize, itemSize,
      radius, radius, squarePaint)
    textPaint.textSize = titleTextSize
    val verticalMargin = getTextVerticalMargin(itemSize.toInt())
    drawText(title, 0, title.length, start + itemSize / 2f,
      titleTextHeight / 2f + verticalMargin, textPaint)
    textPaint.textSize = numberTextSize
    drawText(number, 0, number.length, start + itemSize / 2f,
      height - verticalMargin, textPaint)
  }
  
  private fun getTextSizeFor(
    text1: String,
    text2: String,
    text3: String,
    width: Int,
    paint: Paint
  ): Float {
    val squareSize = getSquareSize(width)
    calculateTextSize(squareSize, text1, paint)
    val firstTitleSize = paint.textSize
    calculateTextSize(squareSize, text2, paint)
    val secondTitleSize = paint.textSize
    calculateTextSize(squareSize, text3, paint)
    val thirdTitleSize = paint.textSize
    return minOf(firstTitleSize, secondTitleSize, thirdTitleSize)
  }
  
  companion object {
    
    fun calculateTextSize(width: Int, text: String, paint: Paint) {
      paint.textSize = 10f
      while (true) {
        val titleTextWidth = paint.measureText(text)
        if (titleTextWidth > width - getTextHorizontalMargin(width) * 2f) {
          break
        }
        paint.textSize++
      }
    }
    
    fun getSquareSize(width: Int): Int {
      return (width - getSquareMargin(width) * 2) / 3
    }
    
    fun getSquareCornersRadius(width: Int): Float {
      return width / 35f
    }
    
    fun getSquareMargin(width: Int): Int {
      return width / 18
    }
    
    fun getTextHorizontalMargin(itemSize: Int) = itemSize / 12f
    
    fun getTextVerticalMargin(itemSize: Int) = itemSize / 8f
  }
}