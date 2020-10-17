package core.extenstions

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import androidx.annotation.ColorInt
import kotlin.math.pow
import kotlin.math.roundToInt

val TEMP_RECT = Rect()
val TEMP_RECT_F = RectF()

fun TextPaint.getTextHeight(text: String): Int {
  TEMP_RECT.setEmpty()
  getTextBounds(text, 0, text.length, TEMP_RECT)
  return TEMP_RECT.height()
}

inline fun Canvas.execute(action: Canvas.() -> Unit) {
  val count = save()
  action(this)
  restoreToCount(count)
}

@ColorInt
fun Int.withAlpha(alpha: Float): Int {
  assertThat(alpha in 0f..1f) { "Alpha should be in range 0..1, but is $alpha" }
  val alphaInt = (alpha * 255).toInt()
  return this and (alphaInt shl 24)
}

@ColorInt
fun lerpColor(startColor: Int, endColor: Int, fraction: Float): Int {
  val startA = (startColor shr 24 and 0xff) / 255.0f
  var startR = (startColor shr 16 and 0xff) / 255.0f
  var startG = (startColor shr 8 and 0xff) / 255.0f
  var startB = (startColor and 0xff) / 255.0f
  
  val endA = (endColor shr 24 and 0xff) / 255.0f
  var endR = (endColor shr 16 and 0xff) / 255.0f
  var endG = (endColor shr 8 and 0xff) / 255.0f
  var endB = (endColor and 0xff) / 255.0f
  
  // convert from sRGB to linear
  startR = startR.toDouble().pow(2.2).toFloat()
  startG = startG.toDouble().pow(2.2).toFloat()
  startB = startB.toDouble().pow(2.2).toFloat()
  
  endR = endR.toDouble().pow(2.2).toFloat()
  endG = endG.toDouble().pow(2.2).toFloat()
  endB = endB.toDouble().pow(2.2).toFloat()
  
  // compute the interpolated color in linear space
  var a = startA + fraction * (endA - startA)
  var r = startR + fraction * (endR - startR)
  var g = startG + fraction * (endG - startG)
  var b = startB + fraction * (endB - startB)
  
  // convert back to sRGB in the [0..255] range
  a *= 255.0f
  r = r.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
  g = g.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
  b = b.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
  
  return (a.roundToInt() shl 24) or (r.roundToInt() shl 16) or (g.roundToInt() shl 8) or b.roundToInt()
}