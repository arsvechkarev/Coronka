package com.arsvechkarev.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import core.extenstions.execute
import core.extenstions.f
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

@SuppressLint("ViewConstructor")
class PreventionView(
  context: Context,
  private val imagePadding: Int,
  private val textPadding: Int,
  private val imageSize: Int
) : View(context) {
  
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = TextSizes.H2
    color = Colors.TextPrimary
    typeface = Fonts.SegoeUiBold
  }
  
  private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Colors.Overlay
  }
  
  private var text: String? = null
  private var image: Drawable? = null
  private var textLayout: Layout? = null
  
  fun setData(@DrawableRes drawableRes: Int, @StringRes textRes: Int) {
    image = context.getDrawable(drawableRes)
    text = context.getString(textRes)
    invalidate()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val height = imageSize + imagePadding * 2
    setMeasuredDimension(widthMeasureSpec, resolveSize(height, heightMeasureSpec))
  }
  
  override fun onDraw(canvas: Canvas) {
    val image = image ?: return
    val text = text ?: return
    if (image.bounds.width() == 0) {
      image.setBounds(imagePadding, imagePadding, imagePadding + imageSize,
        imagePadding + imageSize)
    }
    if (textLayout == null) {
      val maxWidth = width - imagePadding - textPadding * 2 - imageSize
      textLayout = StaticLayout(text, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 1f,
        true)
    }
    val radius = getCornersRadius(width, height)
    canvas.drawRoundRect(0f, 0f, width.f, height.f, radius, radius, rectPaint)
    image.draw(canvas)
    canvas.execute {
      translate(
        imageSize + imagePadding + textPadding.f,
        height / 2f - textLayout!!.height / 2f
      )
      textLayout!!.draw(canvas)
    }
  }
  
  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    textLayout = null
  }
  
  companion object {
    
    fun getCornersRadius(width: Int, height: Int): Float {
      return minOf(width, height) / 5f
    }
  }
}