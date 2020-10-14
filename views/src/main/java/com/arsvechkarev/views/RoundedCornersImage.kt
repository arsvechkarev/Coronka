package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatImageView
import core.extenstions.execute
import core.extenstions.f

open class RoundedCornersImage @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
  
  private val path = Path()
  
  @CallSuper
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val radius = getCornerRadius(minOf(w, h))
    path.addRoundRect(0f, 0f, w.f, h.f, radius, radius, Path.Direction.CW)
  }
  
  override fun onDraw(canvas: Canvas) {
    canvas.execute {
      clipPath(path)
      super.onDraw(canvas)
    }
  }
  
  companion object {
    
    fun getCornerRadius(imageSize: Int) = imageSize / 20f
  }
}