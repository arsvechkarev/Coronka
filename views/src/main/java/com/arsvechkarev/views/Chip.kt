package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import core.FontManager
import core.extenstions.assertThat
import core.extenstions.dp
import core.extenstions.execute
import core.extenstions.f
import core.extenstions.i
import core.extenstions.sp

class Chip @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val colorFill: Int
  private val colorSecondary: Int
  private val textLayout: Layout
  private var cornerRadius: Float
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
  private val rect = RectF()
  
  private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
  private var icon: Drawable? = null
  private var iconSize = -1f
  private var iconMargin = 0f
  private lateinit var activeColorFilter: PorterDuffColorFilter
  private lateinit var inactiveColorFilter: PorterDuffColorFilter
  
  private var onIconClickListener: () -> Unit = {}
  
  var isActive = false
    set(value) {
      field = value
      invalidate()
    }
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.Chip, 0, 0)
    colorFill = attributes.getColor(R.styleable.Chip_colorFill, Color.WHITE)
    colorSecondary = attributes.getColor(R.styleable.Chip_colorSecondary, Color.BLACK)
    textPaint.textSize = attributes.getDimension(R.styleable.Chip_android_textSize, 16.sp)
    textPaint.typeface = FontManager.rubik
    rectPaint.strokeWidth = attributes.getDimension(R.styleable.Chip_android_strokeWidth, 2.dp)
    textLayout = boringLayoutOf(textPaint, attributes.getText(R.styleable.Chip_android_text) ?: "")
    cornerRadius = attributes.getDimension(R.styleable.Chip_chipCornerRadius, 10.dp)
    if (attributes.getBoolean(R.styleable.Chip_showIcon, false)) {
      icon = ContextCompat.getDrawable(context, R.drawable.ic_question)!!.mutate()
      activeColorFilter = PorterDuffColorFilter(colorFill, PorterDuff.Mode.SRC_ATOP)
      inactiveColorFilter = PorterDuffColorFilter(colorSecondary, PorterDuff.Mode.SRC_ATOP)
      icon!!.colorFilter = activeColorFilter
      iconSize = attributes.getDimension(R.styleable.Chip_iconSize, 20.dp)
      iconMargin = attributes.getDimension(R.styleable.Chip_iconMargin, 4.dp)
    }
    attributes.recycle()
    rectPaint.color = colorFill
    textPaint.color = colorFill
  }
  
  fun onIconClicked(block: () -> Unit) {
    assertThat(icon != null) { "Icon is null, but trying to set icon click listener" }
    this.onIconClickListener = block
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = paddingStart + textLayout.width + iconMargin +
        iconSize + paddingEnd + rectPaint.strokeWidth * 2
    val maxItemHeight = maxOf(textLayout.height, iconSize.i)
    val height = maxItemHeight + paddingTop + paddingBottom + rectPaint.strokeWidth * 2
    setMeasuredDimension(
      resolveSize(width.toInt(), widthMeasureSpec),
      resolveSize(height.toInt(), heightMeasureSpec)
    )
  }
  
  override fun onDraw(canvas: Canvas) {
    val strokeOffsetForRect: Float
    if (isActive) {
      icon?.colorFilter = inactiveColorFilter
      strokeOffsetForRect = rectPaint.strokeWidth / 2f
      rectPaint.style = Paint.Style.FILL
      textPaint.color = colorSecondary
    } else {
      icon?.colorFilter = activeColorFilter
      strokeOffsetForRect = rectPaint.strokeWidth
      rectPaint.style = Paint.Style.STROKE
      textPaint.color = colorFill
    }
    rect.set(strokeOffsetForRect, strokeOffsetForRect,
      width - strokeOffsetForRect, height - strokeOffsetForRect)
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, rectPaint)
    val strokeOffsetItems = rectPaint.strokeWidth
    canvas.execute {
      translate(paddingStart.f + strokeOffsetItems, paddingTop.f + strokeOffsetItems)
      textLayout.draw(canvas)
    }
    val icon = icon ?: return
    val left = width - paddingBottom - iconSize.i
    val top = height / 2 - iconSize.i / 2
    icon.setBounds(left, top, left + iconSize.i, top + iconSize.i)
    icon.draw(canvas)
  }
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (icon != null && event.action == ACTION_UP) {
      val x = event.x
      val y = event.y
      if (y < height && y > 0 && x < width && x > width - iconSize - paddingEnd) {
        onIconClickListener()
        return true
      }
    }
    return super.onTouchEvent(event)
  }
}