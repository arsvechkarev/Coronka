package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Lays out elements (except first one) in a row with assigning equal width and height to
 * each. First child is laid out to fill width and height of a parent
 */
class ElementsInARowViewGroup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val offset = getChildMargin(width)
    val children = childCount - 1
    val childSize = (width - offset * (children - 1) - paddingStart - paddingEnd) / children
    val measureSpec = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY)
    for (i in 1 until childCount) {
      getChildAt(i).measure(measureSpec, measureSpec)
    }
    getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(childSize, heightMeasureSpec)
    )
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    getChildAt(0).layout(0, 0, width, height)
    val children = childCount - 1
    val offset = getChildMargin(width)
    val childSize = (width - offset * (children - 1) - paddingStart - paddingEnd) / children
    var left = paddingStart
    for (i in 1 until childCount) {
      getChildAt(i).layout(left, paddingTop, left + childSize, height - paddingBottom)
      left += childSize + offset
    }
  }
  
  companion object {
    
    fun getChildMargin(width: Int): Int {
      return width / 18
    }
  }
}