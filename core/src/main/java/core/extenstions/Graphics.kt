package core.extenstions

import android.graphics.Canvas
import kotlin.math.pow
import kotlin.math.roundToInt

inline fun Canvas.block(action: Canvas.() -> Unit) {
  save()
  action(this)
  restore()
}

fun lerpColor(startColor: Int, endColor: Int, fraction: Float): Int {
  val startA = (startColor shr 24 and 0xff) / 255.0f
  var startR = (startColor shr 16 and 0xff) / 255.0f
  var startG = (startColor shr 8 and 0xff) / 255.0f
  var startB = (startColor and 0xff) / 255.0f
  
  val endInt = endColor
  val endA = (endInt shr 24 and 0xff) / 255.0f
  var endR = (endInt shr 16 and 0xff) / 255.0f
  var endG = (endInt shr 8 and 0xff) / 255.0f
  var endB = (endInt and 0xff) / 255.0f
  
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