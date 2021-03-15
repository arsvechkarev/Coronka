package com.arsvechkarev.views.newsitem

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.arsvechkarev.viewdsl.AccelerateDecelerateInterpolator
import com.arsvechkarev.views.RoundedCornersImage
import config.AnimationsConfigurator
import core.viewbuilding.Colors

class NewsItemImage @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RoundedCornersImage(context, attrs, defStyleAttr) {
  
  private var isLoadedNewsImage = false
  
  private val alphaAnimator = ValueAnimator().apply {
    setFloatValues(0f, 1f)
    interpolator = AccelerateDecelerateInterpolator
    duration = AnimationsConfigurator.DurationDefault
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