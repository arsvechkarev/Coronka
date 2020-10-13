package com.arsvechkarev.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import core.extenstions.DURATION_DEFAULT
import core.viewbuilding.Fonts

class ClickableTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  
  init {
    val attributes = context.obtainStyledAttributes(attrs,
      R.styleable.ClickableTextView,
      defStyleAttr, 0)
    val rippleColor = attributes.getColor(
      R.styleable.ClickableTextView_rippleColor, Color.WHITE)
    setRipple(rippleColor)
    attributes.recycle()
    isClickable = true
    isFocusable = true
    typeface = Fonts.SegoeUi
    val pHorizontal = context.resources.getDimension(
      R.dimen.clickable_text_view_p_horizontal).toInt()
    val pVertical = context.resources.getDimension(
      R.dimen.clickable_text_view_p_vertical).toInt()
    setPadding(pHorizontal, pVertical, pHorizontal, pVertical)
  }
  
  private fun setRipple(rippleColor: Int) {
    val r = context.resources.getDimension(R.dimen.clickable_text_view_corners)
    val roundRectShape = RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r),
      null, null)
    val backgroundRect = ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = Color.TRANSPARENT
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
    postDelayed({ isClickable = true }, DURATION_DEFAULT * 2)
    return super.performClick()
  }
}