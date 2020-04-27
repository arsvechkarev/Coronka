package progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View

internal class VectorDrawableProgress(context: Context) : View(context) {
  
  private val drawableBig: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim_big) as AnimatedVectorDrawable
  private val drawableMedium: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim_medium) as AnimatedVectorDrawable
  private val drawableSmall: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim_small) as AnimatedVectorDrawable
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val hw = w / 2
    val hh = h / 2
    var left = hw - drawableBig.intrinsicWidth / 2
    var top = hh - drawableBig.intrinsicHeight / 2
    drawableBig.setBounds(left, top,
      left + drawableBig.intrinsicWidth, top + drawableBig.intrinsicHeight)
    left = hw - drawableMedium.intrinsicWidth / 2
    top = hh - drawableMedium.intrinsicHeight / 2
    drawableMedium.setBounds(left, top,
      left + drawableMedium.intrinsicWidth, top + drawableMedium.intrinsicHeight)
    left = hw - drawableSmall.intrinsicWidth / 2
    top = hh - drawableSmall.intrinsicHeight / 2
    drawableSmall.setBounds(left, top,
      left + drawableSmall.intrinsicWidth, top + drawableSmall.intrinsicHeight)
  }
  
  override fun onDraw(canvas: Canvas) {
    if (!drawableBig.isRunning) drawableBig.start()
    if (!drawableMedium.isRunning) drawableMedium.start()
    if (!drawableSmall.isRunning) drawableSmall.start()
    canvas.save()
    val scale = width / drawableBig.intrinsicWidth.toFloat()
    canvas.scale(scale, scale, width / 2f, height / 2f)
    drawableBig.draw(canvas)
    canvas.scale(1f, -1f, width / 2f, height / 2f)
    canvas.rotate(100f, width / 2f, height / 2f)
    drawableMedium.draw(canvas)
    canvas.scale(-1f, 1f, width / 2f, height / 2f)
    canvas.rotate(200f, width / 2f, height / 2f)
    drawableSmall.draw(canvas)
    canvas.restore()
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    drawableBig.stop()
    drawableMedium.stop()
    drawableSmall.stop()
  }
}