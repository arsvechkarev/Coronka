package core.extenstions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import core.Application

val Double.dp: Float get() = (Application.Values.density * this).toFloat()
val Double.sp: Float get() = (Application.Values.scaledDensity * this).toFloat()

val Int.dp: Float get() = Application.Values.density * this
val Int.sp: Float get() = Application.Values.scaledDensity * this

val Int.dpInt: Int get() = (Application.Values.density * this).toInt()
val Int.spInt: Int get() = (Application.Values.scaledDensity * this).toInt()


fun Context.getAttrColor(@AttrRes resId: Int): Int {
  val typedArray = obtainStyledAttributes(TypedValue().data, intArrayOf(resId))
  val color = typedArray.getColor(0, -1)
  typedArray.recycle()
  require(color != -1)
  return color
}
