package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import core.viewbuilding.Colors
import viewdsl.AccelerateDecelerateInterpolator
import viewdsl.DURATION_DEFAULT

class NewsItemImage @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RoundedCornersImage(context, attrs, defStyleAttr) {
  
  private var isLoadedNewsImage = false
  
  private val alphaAnimator = ValueAnimator().apply {
    setFloatValues(0f, 1f)
    interpolator = AccelerateDecelerateInterpolator
    duration = DURATION_DEFAULT
    addUpdateListener {
      drawable?.alpha = (it.animatedFraction * 255).toInt()
      invalidate()
    }
  }
  
  init {
    scaleType = ScaleType.CENTER_CROP
  }
  
  override fun setImageDrawable(drawable: Drawable?) {
    if (getDrawable() == null && !isLoadedNewsImage && drawable is BitmapDrawable) {
      // Drawable from network is loaded, start animation
      isLoadedNewsImage = true
      alphaAnimator.start()
    }
    super.setImageDrawable(drawable)
  }
  
  override fun drawClipped(canvas: Canvas) {
    canvas.drawColor(Colors.Overlay)
  }
}