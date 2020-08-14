package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class ElementsInARowViewGroup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val offset = getChildMargin(width)
    val childSize = (width - offset * (childCount - 1) - paddingStart - paddingEnd) / childCount
    val measureSpec = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY)
    for (i in 0 until childCount) {
      getChildAt(i).measure(measureSpec, measureSpec)
    }
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(childSize, heightMeasureSpec)
    )
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val offset = getChildMargin(width)
    val childSize = (width - offset * (childCount - 1) - paddingStart - paddingEnd) / childCount
    var left = paddingStart
    for (i in 0 until childCount) {
      getChildAt(i).layout(left, paddingTop, left + childSize, height - paddingBottom)
      left += childSize + offset
    }
  }
  
  private fun getChildMargin(width: Int): Int {
    return width / 18
  }
}