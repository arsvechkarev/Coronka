package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.viewdsl.dimen
import core.extenstions.execute
import core.extenstions.f
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

class Chip @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = Fonts.SegoeUiBold
  }
  private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = dimen(R.dimen.chip_stroke_size)
  }
  
  private val rect = RectF()
  private var textLayout: Layout
  private var colorSecondary: Int = 0
  
  var isActive = false
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }
  
  var colorFill: Int = 0
    set(value) {
      rectPaint.color = value
      field = value
      invalidate()
    }
  
  var text: String
    get() = textLayout.text.toString()
    set(value) {
      textLayout = boringLayoutOf(textPaint, value)
      requestLayout()
    }
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.Chip, 0, 0)
    colorFill = attributes.getColor(R.styleable.Chip_colorFill, Color.WHITE)
    colorSecondary = attributes.getColor(R.styleable.Chip_colorSecondary, Color.BLACK)
    textPaint.textSize = attributes.getDimension(R.styleable.Chip_android_textSize, TextSizes.H5)
    textLayout = boringLayoutOf(textPaint, attributes.getText(R.styleable.Chip_android_text) ?: "")
    rectPaint.color = colorFill
    attributes.recycle()
  
    if (paddingStart == 0 && paddingEnd == 0 && paddingTop == 0 && paddingBottom == 0) {
      // Padding is not set -> applying default padding
      val paddingHorizontal = context.resources.getDimension(
        R.dimen.chip_padding_horizontal).toInt()
      val poddingVertical = context.resources.getDimension(
        R.dimen.chip_padding_vertical).toInt()
      setPadding(paddingHorizontal, poddingVertical, paddingHorizontal,
        poddingVertical)
    }
  }
  
  fun setTextSize(textSize: Float) {
    textPaint.textSize = textSize
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = paddingStart + textLayout.width + paddingEnd + rectPaint.strokeWidth * 2
    val height = textLayout.height + paddingTop + paddingBottom + rectPaint.strokeWidth * 2
    setMeasuredDimension(
      resolveSize(width.toInt(), widthMeasureSpec),
      resolveSize(height.toInt(), heightMeasureSpec)
    )
  }
  
  override fun onDraw(canvas: Canvas) {
    val strokeOffsetForRect: Float
    if (isActive) {
      strokeOffsetForRect = rectPaint.strokeWidth / 2f
      rectPaint.style = Paint.Style.FILL
      textPaint.color = colorSecondary
    } else {
      strokeOffsetForRect = rectPaint.strokeWidth
      rectPaint.style = Paint.Style.STROKE
      textPaint.color = this.colorFill
    }
    rect.set(strokeOffsetForRect, strokeOffsetForRect,
      width - strokeOffsetForRect, height - strokeOffsetForRect)
    canvas.drawRoundRect(rect, height.f, height.f, rectPaint)
    val strokeOffsetItems = rectPaint.strokeWidth
    canvas.execute {
      translate(paddingStart.f + strokeOffsetItems,
        paddingTop.f + strokeOffsetItems)
      textLayout.draw(canvas)
    }
  }
}