package com.arsvechkarev.views.newsitem

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
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
  
  private var bitmap: Bitmap? = null
  private val bitmapMatrix = Matrix()
  private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    isFilterBitmap = true
    color = Colors.Overlay
  }
  
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
    initializeTexts(spaceLeftForText)
    drawTexts(layoutTitle!!, layoutDescription!!, layoutPublishedDate!!,
      textPadding, imageSize, imagePadding, canvas)
    drawBitmap(imageSize, imagePadding, canvas)
  }
  
  override fun onBitmapLoaded(bitmap: Bitmap) {
    this.bitmap = bitmap
    bitmapPaint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    invalidate()
  }
  
  override fun onClearImage() {
    bitmapPaint.shader = null
    bitmap = null
  }
  
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    layoutTitle = null
    layoutDescription = null
    layoutPublishedDate = null
    requestLayout()
  }
  
  private fun initializeTexts(spaceLeftForText: Int) {
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
  }
  
  private fun drawTexts(layoutTitle: StaticLayout, layoutDescription: StaticLayout, layoutPublishedDate: StaticLayout, textPadding: Int, imageSize: Int, imagePadding: Int, canvas: Canvas) {
    val textsHeight = layoutTitle.height + layoutDescription.height +
        layoutPublishedDate.height + textPadding * 2
    val textTop = (height - textsHeight) / 2
    val textLeft = imageSize + imagePadding * 2
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
  
  private fun drawBitmap(imageSize: Int, imagePadding: Int, canvas: Canvas) {
    val imageTop = height / 2 - imageSize / 2
    val radius = NewsItemsUtils.getCornerRadius(imageSize)
    if (bitmap != null && !bitmap!!.isRecycled) {
      val sx = imageSize.f / bitmap!!.width
      val sy = imageSize.f / bitmap!!.height
      val maxS = maxOf(sx, sy)
      val dy = (height / 2f) - bitmap!!.height / 2f * maxS
      val dx = (imagePadding + imageSize / 2f) - bitmap!!.width / 2f * maxS
      bitmapMatrix.setScale(maxS, maxS)
      bitmapMatrix.postTranslate(dx, dy)
      bitmapPaint.shader.setLocalMatrix(bitmapMatrix)
    }
    canvas.drawRoundRect(
      imagePadding.f, imageTop.f,
      imagePadding + imageSize.f,
      imageTop + imageSize.f,
      radius, radius, bitmapPaint
    )
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
  
    private const val titleMaxLines = 3
    private const val descriptionMaxLines = 2
  
    private const val TEXT_FOR_MEASURE = "A\nA\nA\nA"
  }
}