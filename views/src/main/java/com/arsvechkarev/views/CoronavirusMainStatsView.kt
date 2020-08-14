package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import core.FontManager
import core.extenstions.execute

class CoronavirusMainStatsView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = FontManager.segoeUI
    color = ContextCompat.getColor(context, R.color.dark_text_primary)
  }
  
  private val numberTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = FontManager.segoeUI
    color = ContextCompat.getColor(context, R.color.dark_text_primary)
  }
  
  private var title: String? = "Confirmed"
  private var numberText: String? = "14.658 M"
  
  private var titleLayout: BoringLayout? = null
  private var numberLayout: BoringLayout? = null
  
  fun setTitle(title: String) {
    this.title = title
    requestLayout()
  }
  
  fun setNumberText(text: String) {
    this.numberText = text
    requestLayout()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    if (title == null || numberText == null) {
      return
    }
    val offset = getTextOffset(w)
    titleLayout = setupTextLayout(w, offset, title!!, titlePaint)
    numberLayout = setupTextLayout(w, offset, numberText!!, numberTextPaint)
  }
  
  override fun onDraw(canvas: Canvas) {
    if (titleLayout == null || numberLayout == null) {
      return
    }
    val offset = getTextOffset(width)
    canvas.execute {
      translate(offset, offset)
      titleLayout!!.draw(canvas)
    }
    canvas.execute {
      translate(offset, height - offset - numberLayout!!.height)
      numberLayout!!.draw(canvas)
    }
  }
  
  private fun setupTextLayout(w: Int, offset: Float, text: String, paint: TextPaint): BoringLayout {
    paint.textSize = 10f
    while (true) {
      val titleTextWidth = paint.measureText(text)
      if (titleTextWidth > w - offset * 2f) {
        break
      }
      paint.textSize++
    }
    return boringLayoutOf(paint, text, alignment = Layout.Alignment.ALIGN_CENTER)
  }
  
  private fun getTextOffset(width: Int) = width / 12f
}