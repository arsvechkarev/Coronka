package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.BoringLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import core.extenstions.assertThat
import core.extenstions.execute
import core.extenstions.formattedMillions
import core.viewbuilding.Colors
import core.viewbuilding.Fonts

class CoronavirusMainStatsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = Fonts.SegoeUi
    color = Colors.TextPrimary
  }
  
  private val numberTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = Fonts.SegoeUi
    color = Colors.TextPrimary
  }
  
  private val title: String
  private var numberText: String? = null
  private var titleLayout: BoringLayout? = null
  private var numberLayout: BoringLayout? = null
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.CoronavirusMainStatsView,
      0, 0)
    title = attributes.getString(R.styleable.CoronavirusMainStatsView_android_title)!!
    attributes.recycle()
  }
  
  fun prepareNumber(number: Int, textSize: Float) {
    numberText = getTextForNumber(context, number)
    numberTextPaint.textSize = textSize
    invalidate()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    titlePaint.textSize = getTitleTextSize(w, context)
    titleLayout = boringLayoutOf(titlePaint, title)
  }
  
  override fun onDraw(canvas: Canvas) {
    if (numberText == null) {
      return
    }
    if (numberLayout == null) {
      numberLayout = boringLayoutOf(numberTextPaint, numberText!!)
    }
    val offset = getTextOffset(width)
    canvas.execute {
      translate(width / 2f - titleLayout!!.width / 2f, offset)
      titleLayout!!.draw(canvas)
    }
    canvas.execute {
      translate(width / 2f - numberLayout!!.width / 2f, height - offset - numberLayout!!.height)
      numberLayout!!.draw(canvas)
    }
  }
  
  companion object {
  
    fun getTextSize(width: Int, text: String): Float {
      assertThat(width != 0) { "Width = 0, unable to calculate text size" }
      val paint = Paint().apply {
        typeface = Fonts.SegoeUi
      }
      return calculateTextSize(width, text, paint)
    }
  
    fun getTextForNumber(context: Context, number: Int): String {
      return number.formattedMillions(context)
    }
  
    private fun getTitleTextSize(width: Int, context: Context): Float {
      val confirmedTitleSize = getTextSize(width,
        context.getString(R.string.text_confirmed))
      val recoveredTitleSize = getTextSize(width,
        context.getString(R.string.text_recovered))
      val deathsTitleSize = getTextSize(width, context.getString(R.string.text_deaths))
      return minOf(confirmedTitleSize, recoveredTitleSize, deathsTitleSize)
    }
  
    private fun calculateTextSize(width: Int, text: String, paint: Paint): Float {
      paint.textSize = 10f
      while (true) {
        val titleTextWidth = paint.measureText(text)
        if (titleTextWidth > width - getTextOffset(width) * 2f) {
          break
        }
        paint.textSize++
      }
      return paint.textSize
    }
    
    private fun getTextOffset(width: Int) = width / 12f
  }
}