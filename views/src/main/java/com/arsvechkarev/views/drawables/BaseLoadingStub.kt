package com.arsvechkarev.views.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import androidx.core.content.ContextCompat
import com.arsvechkarev.views.R
import core.extenstions.execute
import core.extenstions.f

abstract class BaseLoadingStub(
  private val context: Context,
  backgroundColorRes: Int = R.color.dark_overlay,
  shineColorRes: Int = R.color.dark_overlay_shine,
  shineColorLightRes: Int = R.color.dark_overlay_shine_light,
  private val durationMillis: Long = 1200
) : Drawable(), Animatable, Runnable {
  
  private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var isRunning = false
  private var shineXOffset = 0f
  private var shineDx = 0f
  private val shineRect = RectF()
  private val path = Path()
  private val backgroundColor = ContextCompat.getColor(context, backgroundColorRes)
  private val shineColor = ContextCompat.getColor(context, shineColorRes)
  private val shineColorLight = ContextCompat.getColor(context, shineColorLightRes)
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundColor }
  
  private var shineXStart = 0f
  private var shineXEnd = 0f
  
  abstract fun drawBackgroundWithPath(path: Path, width: Float, height: Float)
  
  override fun onBoundsChange(bounds: Rect) {
    val width = bounds.width().f
    val height = bounds.height().f.coerceAtMost(context.resources.displayMetrics.heightPixels.f)
    val shineWidth = width * 3
    shineXStart = -width * 3
    shineXEnd = width * 4
    shineDx = width * 7 / (durationMillis.toFloat() / 1000 * 60)
    shineRect.set(shineXStart, -height, shineXStart + shineWidth, height * 2f)
    val gradient = LinearGradient(
      shineRect.left,
      shineRect.height() / 2f,
      shineRect.right,
      shineRect.height() / 2f,
      intArrayOf(backgroundColor, shineColor, shineColorLight,
        shineColorLight, shineColor, backgroundColor),
      floatArrayOf(0f, 0.2f, 0.45f, 0.55f, 0.8f, 0.95f),
      Shader.TileMode.CLAMP
    )
    shinePaint.shader = gradient
    drawBackgroundWithPath(path, width, height)
  }
  
  override fun draw(canvas: Canvas) {
    canvas.drawPath(path, paint)
    canvas.execute {
      clipPath(path)
      rotate(20f, width / 2f, height / 2f)
      translate(shineXOffset, 0f)
      drawRect(shineRect, shinePaint)
    }
  }
  
  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }
  
  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }
  
  override fun getOpacity() = PixelFormat.OPAQUE
  
  override fun start() {
    if (isRunning) return
    isRunning = true
    shineXOffset = shineXStart
    scheduleSelf(this, SystemClock.uptimeMillis() + 1000 / 60)
  }
  
  override fun stop() {
    if (!isRunning) return
    isRunning = false
    unscheduleSelf(this)
  }
  
  override fun isRunning(): Boolean {
    return isRunning
  }
  
  override fun run() {
    if (!isRunning) {
      return
    }
    shineXOffset += shineDx
    if (shineXOffset >= shineXEnd) {
      shineXOffset = shineXStart
    }
    scheduleSelf(this, SystemClock.uptimeMillis() + 1000 / 60)
    invalidateSelf()
  }
  
  companion object {
  
    fun View.applyLoadingDrawable(drawable: BaseLoadingStub) {
      background = drawable.apply { post { start() } }
    }
  }
}