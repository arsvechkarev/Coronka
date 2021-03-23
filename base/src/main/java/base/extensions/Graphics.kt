package base.extensions

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils

val TEMP_RECT = Rect()
val TEMP_RECT_F = RectF()

fun TextPaint.getTextHeight(text: String = "A"): Int {
  TEMP_RECT.setEmpty()
  getTextBounds(text, 0, text.length, TEMP_RECT)
  return TEMP_RECT.height()
}

inline fun Canvas.execute(action: Canvas.() -> Unit) {
  val count = save()
  action(this)
  restoreToCount(count)
}

fun boringLayoutOf(
  textPaint: TextPaint,
  text: CharSequence,
  maxWidth: Float = -1f,
  alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
): BoringLayout {
  val metrics = BoringLayout.isBoring(text, textPaint)
  if (maxWidth == -1f) {
    return BoringLayout.make(text, textPaint, metrics.width,
      alignment, 0f, 0f, metrics, false)
  } else {
    return BoringLayout.make(text, textPaint, metrics.width,
      alignment, 0f, 0f, metrics, false,
      TextUtils.TruncateAt.END, maxWidth.toInt())
  }
}