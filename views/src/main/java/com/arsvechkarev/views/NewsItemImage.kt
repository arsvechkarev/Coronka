package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.arsvechkarev.views.drawables.BaseLoadingStub
import com.arsvechkarev.views.drawables.NewsItemImageLoadingStub
import core.extenstions.AccelerateDecelerateInterpolator
import core.extenstions.DURATION_DEFAULT
import core.extenstions.doOnEnd

class NewsItemImage @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RoundedCornersImage(context, attrs, defStyleAttr) {
  
  private var isLoadedNewsImage = false
  private val stubDrawable: BaseLoadingStub = NewsItemImageLoadingStub()
  
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
    post { stubDrawable.start() }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    stubDrawable.setBounds(0, 0, w, h)
  }
  
  override fun setImageDrawable(drawable: Drawable?) {
    if (getDrawable() == null && !isLoadedNewsImage && drawable is BitmapDrawable) {
      // Drawable from network is loaded, start animation
      isLoadedNewsImage = true
      alphaAnimator.doOnEnd { stubDrawable.stop() }
      alphaAnimator.start()
      stubDrawable.start()
    }
    super.setImageDrawable(drawable)
  }
  
  override fun onDraw(canvas: Canvas) {
    stubDrawable.draw(canvas)
    super.onDraw(canvas)
  }
}