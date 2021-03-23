package base.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Region
import android.graphics.Shader
import android.graphics.drawable.Drawable
import base.extensions.execute
import base.extensions.f
import base.resources.Colors
import base.resources.Dimens

class GradientHeaderDrawable(
  private val startColor: Int = Colors.GradientHeaderStart,
  private val endColor: Int = Colors.GradientHeaderEnd,
  private val curveSize: Float = Dimens.GradientHeaderCurveSize,
) : Drawable() {
  
  private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val gradientPath = Path()
  private var yScaleCoefficient: Float = 0f
  
  override fun onBoundsChange(bounds: Rect) {
    val w = bounds.width()
    val h = bounds.height()
    gradientPath.reset()
    gradientPath.moveTo(0f, 0f)
    gradientPath.lineTo(w.f, 0f)
    gradientPath.lineTo(w.f, h - curveSize)
    gradientPath.quadTo(w / 2f, h.f, 0f, h.f - curveSize)
    gradientPath.close()
    gradientPaint.shader = LinearGradient(
      0f, h.f, w.f, 0f,
      intArrayOf(startColor, endColor), null,
      Shader.TileMode.CLAMP
    )
    Region().apply {
      setPath(gradientPath, Region(bounds))
      yScaleCoefficient = h.f / this.bounds.height()
    }
  }
  
  override fun draw(canvas: Canvas) = canvas.execute {
    scale(1f, yScaleCoefficient, 0f, bounds.height() / 2f)
    drawPath(gradientPath, gradientPaint)
  }
  
  override fun setAlpha(alpha: Int) {
    gradientPaint.alpha = alpha
  }
  
  override fun setColorFilter(colorFilter: ColorFilter?) {
    gradientPaint.colorFilter = colorFilter
  }
  
  override fun getOpacity() = PixelFormat.OPAQUE
}