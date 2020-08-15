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
import core.FontManager
import core.extenstions.execute
import core.extenstions.f
import core.extenstions.sp

class Chip @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val colorFill: Int
  private val colorSecondary: Int
  private val textLayout: Layout
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
  private val rect = RectF()
  
  private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
  
  val text: String
    get() = textLayout.text.toString()
  
  var isActive = false
    set(value) {
      if (field != value) {
        field = value
        invalidate()
      }
    }
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.Chip, 0, 0)
    colorFill = attributes.getColor(R.styleable.Chip_colorFill, Color.WHITE)
    colorSecondary = attributes.getColor(R.styleable.Chip_colorSecondary, Color.BLACK)
    textPaint.textSize = attributes.getDimension(R.styleable.Chip_android_textSize, 16.sp)
    textPaint.typeface = FontManager.segoeUI
    rectPaint.strokeWidth = context.resources.getDimension(R.dimen.chip_stroke_size)
    textLayout = boringLayoutOf(textPaint, attributes.getText(R.styleable.Chip_android_text) ?: "")
    rectPaint.color = colorFill
    textPaint.color = colorFill
    attributes.recycle()
    
    if (paddingStart == 0 && paddingEnd == 0 && paddingTop == 0 && paddingBottom == 0) {
      // Padding is not set applying default padding
      val paddingHorizontal = context.resources.getDimension(R.dimen.chip_padding_horizontal).toInt()
      val poddingVertical = context.resources.getDimension(R.dimen.chip_padding_vertical).toInt()
      setPadding(paddingHorizontal, poddingVertical, paddingHorizontal, poddingVertical)
    }
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
      textPaint.color = colorFill
    }
    rect.set(strokeOffsetForRect, strokeOffsetForRect,
      width - strokeOffsetForRect, height - strokeOffsetForRect)
    canvas.drawRoundRect(rect, height.f, height.f, rectPaint)
    val strokeOffsetItems = rectPaint.strokeWidth
    canvas.execute {
      translate(paddingStart.f + strokeOffsetItems, paddingTop.f + strokeOffsetItems)
      textLayout.draw(canvas)
    }
  }
}