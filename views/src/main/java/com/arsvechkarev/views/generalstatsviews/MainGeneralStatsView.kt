package com.arsvechkarev.views.generalstatsviews

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import com.arsvechkarev.views.R
import core.extenstions.formattedMillions
import core.extenstions.getTextHeight
import core.extenstions.i
import core.model.GeneralInfo
import core.viewbuilding.Colors
import core.viewbuilding.Fonts

class MainGeneralStatsView @JvmOverloads constructor(
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
  
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    adjustToOrientation(resources.configuration.orientation)
  }
  
  fun updateNumbers(generalInfo: GeneralInfo) {
    confirmedNumber = generalInfo.confirmed.formattedMillions(context)
    recoveredNumber = generalInfo.recovered.formattedMillions(context)
    deathsNumber = generalInfo.deaths.formattedMillions(context)
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(getSquareSize(width), heightMeasureSpec)
    )
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    titleTextSize = getTextSizeFor(
      confirmedTitle, recoveredTitle, deathsTitle, getSquareSize(w), textPaint
    )
    titleTextHeight = textPaint.getTextHeight(confirmedTitle).toFloat()
  }
  
  override fun onDraw(canvas: Canvas) {
    confirmedNumber ?: return
    numberTextSize = getTextSizeFor(
      confirmedNumber!!, recoveredNumber!!, deathsNumber!!, getSquareSize(width), textPaint
    )
    val offset = getItemMargin(width).toFloat()
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
    val radius = getItemCornersRadius(width)
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
  
  override fun onConfigurationChanged(newConfig: Configuration) {
    val orientation = newConfig.orientation
    adjustToOrientation(orientation)
  }
  
  private fun adjustToOrientation(orientation: Int) {
    if (orientation == ORIENTATION_PORTRAIT) {
      (layoutParams as MarginLayoutParams).setMargins(0, 0, 0, 0)
    } else {
      val margin = resources.getDimension(R.dimen.general_stats_view_landscape_margin).i
      (layoutParams as MarginLayoutParams).setMargins(margin, 0, margin, 0)
    }
  }
  
  companion object {
    
    fun getItemMargin(width: Int) = width / 18
    
    private fun getTextVerticalMargin(itemSize: Int) = itemSize / 8f
    
    private fun getSquareSize(width: Int): Int {
      return (width - getItemMargin(width) * 2) / 3
    }
  }
}