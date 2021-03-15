package com.arsvechkarev.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.paddings
import config.AnimationsConfigurator
import core.extenstions.boringLayoutOf
import core.extenstions.execute
import core.extenstions.getTextHeight
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

class RetryButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Colors.TextPrimary
    typeface = Fonts.SegoeUiBold
    textSize = TextSizes.H3
    textAlign = Paint.Align.CENTER
  }
  
  private val textLayout = boringLayoutOf(textPaint, context.getText(R.string.text_retry))
  
  init {
    setRipple(Colors.Ripple)
    isClickable = true
    isFocusable = true
    paddings(28.dp, 8.dp, 28.dp, 8.dp)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textWidth = textPaint.measureText(textLayout.text!!.toString())
    val textHeight = textPaint.getTextHeight(textLayout.text!!.toString())
    val width = textWidth + paddingStart + paddingEnd
    val height = textHeight + paddingTop + paddingBottom
    setMeasuredDimension(
      resolveSize(width.toInt(), widthMeasureSpec),
      resolveSize(height, heightMeasureSpec)
    )
  }
  
  override fun onDraw(canvas: Canvas) {
    canvas.execute {
      translate(width / 2f, height / 2f - textLayout.height / 2f)
      textLayout.draw(this)
    }
  }
  
  private fun setRipple(rippleColor: Int) {
    val r = 40.dp.toFloat()
    val roundRectShape = RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r),
      null, null)
    val backgroundRect = ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = Colors.Failure
    }
    val maskRect = ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = rippleColor
    }
    background = RippleDrawable(ColorStateList.valueOf(rippleColor),
      backgroundRect, maskRect)
  }
  
  override fun performClick(): Boolean {
    isClickable = false
    postDelayed({ isClickable = true }, AnimationsConfigurator.DurationDefault * 2)
    return super.performClick()
  }
}