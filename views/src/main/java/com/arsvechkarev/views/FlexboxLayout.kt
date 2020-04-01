package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class FlexboxLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val count = childCount
    // Measurement will ultimately be computing these values.
    var maxHeight = 0
    var maxWidth = 0
    var childState = 0
    var mLeftWidth = 0
    var rowCount = 0
    // Iterate through all children, measuring them and computing our dimensions
    // from their size.
    for (i in 0 until count) {
      val child = getChildAt(i)
      if (child.visibility == View.GONE) continue
      // Measure the child.
      measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
      maxWidth += Math.max(maxWidth, child.measuredWidth)
      mLeftWidth += child.measuredWidth
      if (mLeftWidth / width > rowCount) {
        maxHeight += child.measuredHeight
        rowCount++
      } else {
        maxHeight = Math.max(maxHeight, child.measuredHeight)
      }
      childState = View.combineMeasuredStates(childState, child.measuredState)
    }
    // Check against our minimum height and width
    maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
    maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
    // Report our final dimensions.
    setMeasuredDimension(
      View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
      View.resolveSizeAndState(maxHeight, heightMeasureSpec,
        childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val childLeft = this.paddingLeft
    val childTop = this.paddingTop
    val childRight = this.measuredWidth - this.paddingRight
    var curWidth: Int
    var curHeight: Int
    var curLeft = childLeft
    var curTop = childTop
    var maxHeight = 0
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      if (child.visibility == View.GONE) continue
      curWidth = childWidthMargins(child)
      curHeight = childHeightMargins(child)
      if (curLeft + curWidth >= childRight) {
        curLeft = childLeft
        curTop += maxHeight
        maxHeight = 0
      }
      child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
      if (maxHeight < curHeight) {
        maxHeight = curHeight
      }
      curLeft += curWidth
    }
  }
  
  private fun childHeightMargins(child: View): Int {
    val params = child.layoutParams as MarginLayoutParams
    return child.measuredHeight + params.topMargin + params.bottomMargin
  }
  
  private fun childWidthMargins(child: View): Int {
    val params = child.layoutParams as MarginLayoutParams
    return child.measuredWidth + params.marginStart + params.marginEnd
  }
  
  override fun checkLayoutParams(p: LayoutParams): Boolean {
    return p is MarginLayoutParams
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams? {
    return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }
  
  override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams? {
    return MarginLayoutParams(context, attrs)
  }
  
  override fun generateLayoutParams(p: LayoutParams?): LayoutParams? {
    return generateDefaultLayoutParams()
  }
}