package progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View

class ComplexProgressBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private val drawableBig: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim_big) as AnimatedVectorDrawable
  private val drawableSmall: AnimatedVectorDrawable =
      context.getDrawable(R.drawable.progress_anim_small) as AnimatedVectorDrawable
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    drawableBig.setBounds(0, 0, drawableBig.intrinsicWidth, drawableBig.intrinsicHeight)
    drawableSmall.setBounds(0, 0, drawableSmall.intrinsicWidth, drawableSmall.intrinsicHeight)
  }
  
  override fun onDraw(canvas: Canvas) {
    if (!drawableBig.isRunning) drawableBig.start()
    if (!drawableSmall.isRunning) drawableSmall.start()
    val count = canvas.save()
    val scale = width / drawableBig.intrinsicWidth.toFloat()
    canvas.save()
    canvas.rotate(180f, width / 2f, height / 2f)
    canvas.scale(scale, scale)
    drawableBig.draw(canvas)
    canvas.restore()
    canvas.scale(scale, scale)
    val coefficient = (drawableBig.intrinsicWidth - drawableSmall.intrinsicHeight.toFloat()) / 2
    canvas.translate(coefficient, coefficient)
    drawableSmall.draw(canvas)
    canvas.restoreToCount(count)
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    drawableBig.stop()
    drawableSmall.stop()
  }
}