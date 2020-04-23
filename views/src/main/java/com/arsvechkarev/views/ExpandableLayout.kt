package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import core.extenstions.DURATION_DEFAULT

class ExpandableLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  
  private var expansionFraction = 0f
  
  private val animator = ValueAnimator().apply {
    duration = DURATION_DEFAULT
    interpolator = AccelerateDecelerateInterpolator()
    addUpdateListener {
      expansionFraction = it.animatedValue as Float
      requestLayout()
    }
  }
  
  fun visible(animate: Boolean = true) {
    if (animate) {
      animator.setFloatValues(expansionFraction, 1f)
      animator.start()
    } else {
      expansionFraction = 1f
      requestLayout()
    }
  }
  
  fun gone(animate: Boolean = true) {
    if (animate) {
      animator.setFloatValues(expansionFraction, 0f)
      animator.start()
    } else {
      expansionFraction = 0f
      requestLayout()
    }
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val measuredHeight = (expansionFraction * measuredHeight).toInt()
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight)
  }
}
