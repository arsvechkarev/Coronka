package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.TextView
import core.extenstions.addViews
import core.extenstions.isOrientationPortrait
import core.viewbuilding.atMost
import core.viewbuilding.exactly
import core.viewbuilding.getMinimumSize
import core.viewbuilding.layoutWithLeftTop
import core.viewbuilding.size

@SuppressLint("ViewConstructor") // Created through code
class NewsItemView(
  image: NewsItemImage,
  textTitle: TextView,
  textDescription: TextView,
  textTime: TextView
) : ViewGroup(image.context) {
  
  val image get() = getChildAt(0) as NewsItemImage
  val textTitle get() = getChildAt(1) as TextView
  val textDescription get() = getChildAt(2) as TextView
  val textTime get() = getChildAt(3) as TextView
  
  init {
    addViews(image, textTitle, textDescription, textTime)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val minSize = getMinimumSize(widthMeasureSpec, heightMeasureSpec)
    val width = widthMeasureSpec.size
    val imagePadding = getImagePadding(minSize)
    val verticalPadding = getVerticalPadding(width)
    val imageSize = getImageSize(width)
    val imageHeight = imageSize + verticalPadding * 2
    image.measure(exactly(imageSize), exactly(imageSize))
    val spaceLeftForText = width - imageSize - imagePadding * 3
    textTime.measure(atMost(spaceLeftForText), atMost(imageHeight))
    textTitle.measure(exactly(spaceLeftForText), atMost(imageHeight))
    textDescription.measure(exactly(spaceLeftForText), atMost(imageHeight))
    val textsHeight = textTime.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + getTextPadding(width) * 4
    val resultHeight = maxOf(imageHeight, textsHeight)
    setMeasuredDimension(widthMeasureSpec, resolveSize(resultHeight, heightMeasureSpec))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val imagePadding = getImagePadding(width)
    val textPadding = getTextPadding(width)
    val imageSize = getImageSize(width)
    val imageTop = height / 2 - imageSize / 2
    image.layout(imagePadding, imageTop, imagePadding + imageSize, imageTop + imageSize)
    val textsHeight = textTime.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + getTextPadding(width) * 2
    val textTop = height / 2 - textsHeight / 2
    val textLeft = image.right + imagePadding
    textTitle.layoutWithLeftTop(textLeft, textTop)
    textDescription.layoutWithLeftTop(textLeft, textTitle.bottom + textPadding)
    textTime.layoutWithLeftTop(textLeft, textDescription.bottom + textPadding)
  }
  
  companion object {
  
    fun getImageSize(minSize: Int): Int {
      if (isOrientationPortrait) {
        return (minSize / 3.2f).toInt()
      } else {
        return (minSize / 4.5f).toInt()
      }
    }
  
    fun getImagePadding(size: Int): Int {
      if (isOrientationPortrait) {
        return size / 22
      } else {
        return size / 32
      }
    }
  
    fun getVerticalPadding(size: Int) = size / 18
  
    fun getTextPadding(size: Int) = size / 33
  }
}