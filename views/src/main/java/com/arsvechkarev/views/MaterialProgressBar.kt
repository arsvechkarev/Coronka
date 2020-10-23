package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.viewdsl.startIfNotRunning
import com.arsvechkarev.viewdsl.stopIfRunning
import core.extenstions.execute

class MaterialProgressBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val drawable: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim) as AnimatedVectorDrawable
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
  }
  
  override fun onDraw(canvas: Canvas) {
    drawable.startIfNotRunning()
    val scale = width / drawable.intrinsicWidth.toFloat()
    canvas.execute {
      canvas.scale(scale, scale)
      drawable.draw(canvas)
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    drawable.stopIfRunning()
  }
}