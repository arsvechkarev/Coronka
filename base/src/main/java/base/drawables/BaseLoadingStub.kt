package base.drawables

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
import base.extensions.execute
import base.extensions.f
import base.resources.Colors
import config.AnimationsConfigurator

abstract class BaseLoadingStub(
  private val backgroundColor: Int = Colors.Overlay,
  private val shineColor: Int = Colors.OverlayShine,
  private val shineColorLight: Int = Colors.OverlayShineLight,
  private val durationMillis: Long = AnimationsConfigurator.DurationLoadingStubIdle
) : Drawable(), Animatable, Runnable {
  
  private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var isRunning = false
  private var shineXOffset = 0f
  private var shineDx = 0f
  private val shineRect = RectF()
  private val path = Path()
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundColor }
  
  private var shineXStart = 0f
  private var shineXEnd = 0f
  
  abstract fun drawBackgroundWithPath(path: Path, width: Float, height: Float)
  
  override fun onBoundsChange(bounds: Rect) {
    val width = bounds.width().f
    val height = bounds.height().f
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
    path.reset()
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
  
    val View.asLoadingStub get() = background as BaseLoadingStub
  
    fun View.setLoadingDrawable(drawable: BaseLoadingStub) {
      background = drawable.apply { post { start() } }
    }
  }
}