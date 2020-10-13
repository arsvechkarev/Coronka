package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.widget.FrameLayout
import android.widget.TextView
import core.extenstions.addViews
import core.viewbuilding.atMost
import core.viewbuilding.exactly
import core.viewbuilding.getMinimumSize
import core.viewbuilding.layoutWithLeftTop
import core.viewbuilding.size

@SuppressLint("ViewConstructor")
class NewsItemView(
  image: RoundedCornersImage,
  textTitle: TextView,
  textTime: TextView
) : FrameLayout(image.context) {
  
  val image: RoundedCornersImage
    get() = getChildAt(0) as RoundedCornersImage
  
  val textTitle: TextView
    get() = getChildAt(1) as TextView
  
  val textTime: TextView
    get() = getChildAt(2) as TextView
  
  init {
    addViews(image, textTitle, textTime)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = widthMeasureSpec.size
    val minSize = getMinimumSize(widthMeasureSpec, heightMeasureSpec)
    val padding = paddingForMinSize(minSize)
    val imageSize = imageSizeForMinSize(minSize)
    val itemHeight = imageSize + padding * 2
    image.measure(exactly(imageSize), exactly(imageSize))
    val textSizeLeft = width - imageSize - padding * 4
    textTime.measure(atMost(textSizeLeft), atMost(imageSize))
    textTitle.measure(
      exactly(textSizeLeft),
      atMost(itemHeight - padding - textTime.measuredHeight)
    )
    setMeasuredDimension(widthMeasureSpec, resolveSize(itemHeight, heightMeasureSpec))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val minSize = minOf(width, height)
    val padding = paddingForMinSize(minSize)
    image.layoutWithLeftTop(padding, padding)
    textTitle.layoutWithLeftTop(
      image.right + padding,
      height / 2 - (textTitle.measuredHeight + textTime.measuredHeight + padding) / 2
    )
    textTime.layoutWithLeftTop(image.right + padding, textTitle.bottom + padding)
  }
  
  private fun imageSizeForMinSize(minSize: Int) = minSize / 3
  
  private fun paddingForMinSize(minSize: Int) = minSize / 14
}