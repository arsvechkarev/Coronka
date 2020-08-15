package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import core.extenstions.i

class StatsSmallHeaderViewGroup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
  
  private lateinit var rankTextView: TextView
  private lateinit var countryNameTextView: TextView
  private lateinit var numberTextView: TextView
  
  private var rankTextViewMaxWidth = -1f
  private var countryNameTextViewMaxWidth = -1f
  private var countryNameTextStartOffset = -1f
  private var numberTextViewMaxWidth = -1f
  
  override fun onFinishInflate() {
    super.onFinishInflate()
    rankTextView = getChildAt(0) as TextView
    countryNameTextView = getChildAt(1) as TextView
    numberTextView = getChildAt(2) as TextView
  }
  
  fun setMaxTextsWidths(
    rankTextViewWidth: Float,
    countryNameTextViewWidth: Float,
    countryNameTextStartOffset: Float,
    numberTextViewWidth: Float
  ) {
    this.rankTextViewMaxWidth = rankTextViewWidth
    this.countryNameTextViewMaxWidth = countryNameTextViewWidth
    this.countryNameTextStartOffset = countryNameTextStartOffset
    this.numberTextViewMaxWidth = numberTextViewWidth
    requestLayout()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (rankTextViewMaxWidth == -1f) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }
    var width = measureTextView(rankTextView, rankTextViewMaxWidth.i, heightMeasureSpec)
    width += measureTextView(countryNameTextView, countryNameTextViewMaxWidth.i, heightMeasureSpec)
    width += measureTextView(numberTextView, numberTextViewMaxWidth.i, heightMeasureSpec)
    width += paddingStart + paddingEnd
    
    var height = maxOf(
      rankTextView.measuredHeight, countryNameTextView.measuredHeight, numberTextView.measuredHeight)
    height += paddingTop + paddingBottom
    
    setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    if (rankTextViewMaxWidth == -1f) {
      return
    }
    rankTextView.layout(
      paddingStart,
      height / 2 - rankTextView.measuredHeight / 2,
      paddingStart + rankTextView.measuredWidth,
      height / 2 + rankTextView.measuredHeight / 2
    )
    val left = rankTextViewMaxWidth.i + countryNameTextStartOffset.i
    countryNameTextView.layout(
      left,
      height / 2 - countryNameTextView.measuredHeight / 2,
      left + countryNameTextView.measuredWidth,
      height / 2 + countryNameTextView.measuredHeight / 2
    )
    numberTextView.layout(
      width - numberTextView.measuredWidth - paddingBottom,
      height / 2 - numberTextView.measuredHeight / 2,
      width - paddingBottom,
      height / 2 + numberTextView.measuredHeight / 2
    )
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
    return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }
  
  override fun generateLayoutParams(p: LayoutParams): LayoutParams {
    return MarginLayoutParams(p)
  }
  
  override fun checkLayoutParams(p: LayoutParams): Boolean {
    return p is MarginLayoutParams
  }
  
  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return MarginLayoutParams(context, attrs)
  }
  
  private fun measureTextView(textView: TextView, maxWidth: Int, heightMeasureSpec: Int): Int {
    textView.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), heightMeasureSpec)
    return textView.measuredWidth
  }
}