package com.arsvechkarev.views.progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import com.arsvechkarev.viewdsl.startIfNotRunning
import com.arsvechkarev.viewdsl.stopIfRunning
import com.arsvechkarev.views.R
import com.arsvechkarev.views.progressbar.ProgressBar.Thickness.NORMAL
import com.arsvechkarev.views.progressbar.ProgressBar.Thickness.THICK
import core.extenstions.execute

class MaterialProgressBar constructor(
  context: Context,
  color: Int,
  thickness: ProgressBar.Thickness,
) : View(context) {
  
  private val drawable: AnimatedVectorDrawable =
      when (thickness) {
        NORMAL -> context.getDrawable(R.drawable.progress_anim_normal) as AnimatedVectorDrawable
        THICK -> context.getDrawable(R.drawable.progress_anim_thick) as AnimatedVectorDrawable
      }.apply {
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
      }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    if (visibility == VISIBLE) {
      drawable.startIfNotRunning()
    } else {
      drawable.stopIfRunning()
    }
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