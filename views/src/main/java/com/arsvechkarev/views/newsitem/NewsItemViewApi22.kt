package com.arsvechkarev.views.newsitem

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.TextView
import viewdsl.addViews
import viewdsl.atMost
import viewdsl.exactly
import viewdsl.layoutWithLeftTop
import viewdsl.size
import viewdsl.text

@SuppressLint("ViewConstructor") // Created through code
class NewsItemViewApi22(
  image: NewsItemImage,
  textTitle: TextView,
  textDescription: TextView,
  textTime: TextView
) : ViewGroup(image.context), NewsItemView {
  
  private val image get() = getChildAt(0) as NewsItemImage
  private val textTitle get() = getChildAt(1) as TextView
  private val textDescription get() = getChildAt(2) as TextView
  private val textPublishedDate get() = getChildAt(3) as TextView
  
  init {
    addViews(image, textTitle, textDescription, textTime)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = widthMeasureSpec.size
    val imagePadding = NewsItemsUtils.getImagePadding(width)
    val verticalPadding = NewsItemsUtils.getVerticalPadding(width)
    val imageSize = NewsItemsUtils.getImageSize(width)
    val imageHeight = imageSize + verticalPadding * 2
    image.measure(exactly(imageSize), exactly(imageSize))
    val spaceLeftForText = width - imageSize - imagePadding * 3
    textPublishedDate.measure(atMost(spaceLeftForText), atMost(imageHeight))
    textTitle.measure(exactly(spaceLeftForText), atMost(imageHeight))
    textDescription.measure(exactly(spaceLeftForText), atMost(imageHeight))
    val textsHeight = textPublishedDate.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + NewsItemsUtils.getTextPadding(width) * 4
    val resultHeight = maxOf(imageHeight, textsHeight)
    setMeasuredDimension(widthMeasureSpec, resolveSize(resultHeight, heightMeasureSpec))
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val imagePadding = NewsItemsUtils.getImagePadding(width)
    val textPadding = NewsItemsUtils.getTextPadding(width)
    val imageSize = NewsItemsUtils.getImageSize(width)
    val imageTop = height / 2 - imageSize / 2
    image.layout(imagePadding, imageTop, imagePadding + imageSize, imageTop + imageSize)
    val textsHeight = textPublishedDate.measuredHeight + textTitle.measuredHeight +
        textDescription.measuredHeight + NewsItemsUtils.getTextPadding(width) * 2
    val textTop = height / 2 - textsHeight / 2
    val textLeft = image.right + imagePadding
    textTitle.layoutWithLeftTop(textLeft, textTop)
    textDescription.layoutWithLeftTop(textLeft, textTitle.bottom + textPadding)
    textPublishedDate.layoutWithLeftTop(textLeft, textDescription.bottom + textPadding)
  }
  
  override fun setData(title: String, description: String, publishedDate: String) {
    textTitle.text(title)
    textDescription.text(description)
    textPublishedDate.text(publishedDate)
  }
  
  override fun onBitmapLoaded(bitmap: Bitmap) {
    image.setImageBitmap(bitmap)
  }
  
  override fun onClearImage() {
    image.setImageDrawable(null)
  }
}