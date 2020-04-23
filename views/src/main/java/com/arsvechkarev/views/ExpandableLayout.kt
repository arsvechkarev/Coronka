package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout

class ExpandableLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  
  private var defaultHeight = -1
  private var currentHeight = -1
  
  private val animator = ValueAnimator().apply {
    duration = 500
    interpolator = AccelerateDecelerateInterpolator()
    addUpdateListener {
      currentHeight = it.animatedValue as Int
      requestLayout()
    }
  }
  
  fun visible(animate: Boolean = true) {
    visibility = View.VISIBLE
    if (animate) {
      animator.setIntValues(0, defaultHeight)
      animator.start()
    } else {
      currentHeight = defaultHeight
      requestLayout()
    }
  }
  
  fun gone(animate: Boolean = true) {
    if (animate) {
      animator.setIntValues(defaultHeight, 0)
      animator.start()
    } else {
      currentHeight = 0
      requestLayout()
    }
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (defaultHeight == -1) {
      defaultHeight = measuredHeight
      currentHeight = 0
    }
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), currentHeight)
  }
}
