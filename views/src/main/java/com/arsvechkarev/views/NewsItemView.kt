package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.widget.FrameLayout
import android.widget.TextView
import core.extenstions.addViews
import core.viewbuilding.atMost
import core.viewbuilding.exactly
import core.viewbuilding.layoutWithLeftTop
import core.viewbuilding.size

@SuppressLint("ViewConstructor")
class NewsItemView(
  image: RoundedCornersImage,
  textTitle: TextView,
  textDescription: TextView,
  textTime: TextView
) : FrameLayout(image.context) {
  
  val image get() = getChildAt(0) as RoundedCornersImage
  val textTitle get() = getChildAt(1) as TextView
  val textDescription get() = getChildAt(2) as TextView
  val textTime get() = getChildAt(3) as TextView
  
  init {
    addViews(image, textTitle, textDescription, textTime)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = widthMeasureSpec.size
    val padding = getOuterPadding(width)
    val imageSize = getImageSize(width)
    val imageHeight = imageSize + padding * 2
    image.measure(exactly(imageSize), exactly(imageSize))
    val spaceLeftForText = width - imageSize - padding * 4
    textTime.measure(atMost(spaceLeftForText), atMost(imageHeight))
    textTitle.measure(exactly(spaceLeftForText), atMost(imageHeight))
    textDescription.measure(exactly(spaceLeftForText), atMost(imageHeight))
    val textsHeight = textTime.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + getTextPadding(width) * 4
    val resultHeight = maxOf(imageHeight, textsHeight)
    setMeasuredDimension(widthMeasureSpec, resolveSize(resultHeight, heightMeasureSpec))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val padding = getOuterPadding(width)
    val textPadding = getTextPadding(width)
    val imageSize = getImageSize(width)
    val imageTop = height / 2 - imageSize / 2
    image.layout(padding, imageTop, padding + imageSize, imageTop + imageSize)
    val textsHeight = textTime.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + getTextPadding(width) * 2
    val textTop = height / 2 - textsHeight / 2
    val textLeft = image.right + padding
    textTitle.layoutWithLeftTop(textLeft, textTop)
    textDescription.layoutWithLeftTop(textLeft, textTitle.bottom + textPadding)
    textTime.layoutWithLeftTop(textLeft, textDescription.bottom + textPadding)
  }
  
  companion object {
    
    private fun getImageSize(size: Int) = (size / 3.8f).toInt()
    private fun getOuterPadding(size: Int) = size / 22
    private fun getTextPadding(size: Int) = size / 40
  }
}