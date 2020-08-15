package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.FrameLayout
import com.arsvechkarev.views.behaviors.Header
import core.extenstions.f

class GradientHeaderView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Header {
  
  private val startColor: Int
  private val endColor: Int
  
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val path = Path()
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.GradientHeaderView,
      defStyleAttr, 0)
    startColor = attributes.getColor(R.styleable.GradientHeaderView_android_startColor, Color.BLACK)
    endColor = attributes.getColor(R.styleable.GradientHeaderView_android_endColor, Color.WHITE)
    attributes.recycle()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val offset = h / 4f
    path.reset()
    path.moveTo(0f, 0f)
    path.lineTo(w.f, 0f)
    path.lineTo(w.f, h.f - offset / 1.5f)
    path.quadTo(w / 2f, h.f + offset / 2f, 0f, h.f - offset / 1.5f)
    path.close()
    
    paint.shader = LinearGradient(
      0f, h.f, w.f, 0f,
      intArrayOf(startColor, endColor), null,
      Shader.TileMode.CLAMP
    )
  }
  
  override fun dispatchDraw(canvas: Canvas) {
    canvas.drawPath(path, paint)
    super.dispatchDraw(canvas)
  }
}