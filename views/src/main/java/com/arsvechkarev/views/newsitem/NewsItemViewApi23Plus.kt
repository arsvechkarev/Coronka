package com.arsvechkarev.views.newsitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import core.extenstions.execute
import core.extenstions.f
import core.viewbuilding.Colors
import viewdsl.rippleBackground
import viewdsl.size

@SuppressLint("ViewConstructor")
@RequiresApi(23)
class NewsItemViewApi23Plus(
  context: Context,
  private val titlePaint: TextPaint,
  private val descriptionPaint: TextPaint,
  private val publishedDatePaint: TextPaint
) : View(context), NewsItemView {
  
  private var image: Drawable? = null
  private var layoutTitle: StaticLayout? = null
  private var layoutDescription: StaticLayout? = null
  private var layoutPublishedDate: StaticLayout? = null
  
  private var textTitle: String? = null
  private var textDescription: String? = null
  private var textPublishedDate: String? = null
  
  init {
    rippleBackground(Colors.Ripple)
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = widthMeasureSpec.size
    val verticalPadding = NewsItemsUtils.getVerticalPadding(width)
    val imageSize = NewsItemsUtils.getImageSize(width)
    val imageHeight = imageSize + verticalPadding * 2
    val titleLayout = staticLayout(TEXT_FOR_MEASURE, titlePaint, width, titleMaxLines)
    val descriptionLayout = staticLayout(TEXT_FOR_MEASURE,
      descriptionPaint, width, descriptionMaxLines)
    val publishedDateLayout = staticLayout(TEXT_FOR_MEASURE,
      publishedDatePaint, width, 1)
    val textsHeight = titleLayout.height + descriptionLayout.height + publishedDateLayout.height +
        NewsItemsUtils.getTextPadding(width) * 4
    val resultHeight = maxOf(imageHeight, textsHeight)
    setMeasuredDimension(widthMeasureSpec, resolveSize(resultHeight, heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    if (textTitle == null || textDescription == null || textPublishedDate == null) {
      return
    }
    val imageSize = NewsItemsUtils.getImageSize(width)
    val imagePadding = NewsItemsUtils.getImagePadding(width)
    val textPadding = NewsItemsUtils.getTextPadding(width)
    val spaceLeftForText = width - imageSize - imagePadding * 3
    if (layoutTitle == null) {
      layoutTitle = staticLayout(textTitle!!, titlePaint, spaceLeftForText, titleMaxLines)
    }
    if (layoutDescription == null) {
      layoutDescription = staticLayout(textDescription!!, descriptionPaint,
        spaceLeftForText, descriptionMaxLines)
    }
    if (layoutPublishedDate == null) {
      layoutPublishedDate = staticLayout(textPublishedDate!!, publishedDatePaint,
        spaceLeftForText, 1)
    }
    val layoutTitle = layoutTitle!!
    val layoutDescription = layoutDescription!!
    val layoutPublishedDate = layoutPublishedDate!!
    val imageTop = height / 2 - imageSize / 2
    image?.setBounds(imagePadding, imageTop, imagePadding + imageSize, imageTop + imageSize)
    image?.draw(canvas)
    val textsHeight = layoutTitle.height + layoutDescription.height +
        layoutPublishedDate.height + textPadding * 2
    val textTop = (height - textsHeight) / 2
    val textLeft = imageSize + imagePadding * 2
    println("qqq: draw textHeight = $textsHeight")
    canvas.execute {
      translate(textLeft.f, textTop.f)
      execute {
        layoutTitle.draw(canvas)
      }
      execute {
        translate(0f, layoutTitle.height + textPadding.f)
        layoutDescription.draw(canvas)
      }
      execute {
        translate(
          0f,
          layoutTitle.height + layoutDescription.height + textPadding * 2f
        )
        layoutPublishedDate.draw(canvas)
      }
    }
  }
  
  override fun setData(title: String, description: String, publishedDate: String) {
    textTitle = title
    textDescription = description
    textPublishedDate = publishedDate
    layoutTitle = null
    layoutDescription = null
    layoutPublishedDate = null
    invalidate()
  }
  
  override fun onImageLoaded(image: Drawable) {
    this.image = image
    invalidate()
  }
  
  private fun staticLayout(
    text: CharSequence,
    textPaint: TextPaint,
    maxWidth: Int,
    maxLines: Int
  ): StaticLayout {
    return StaticLayout.Builder.obtain(text, 0, text.length, textPaint, maxWidth)
        .setMaxLines(maxLines)
        .setIncludePad(false)
        .setEllipsize(TextUtils.TruncateAt.END)
        .build()
  }
  
  companion object {
    
    private const val titleMaxLines: Int = 3
    private const val descriptionMaxLines: Int = 2
    
    const val TEXT_FOR_MEASURE = "A\nA\nA\nA"
  }
}