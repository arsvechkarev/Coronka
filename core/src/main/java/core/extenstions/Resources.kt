package core.extenstions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import core.Application

val Int.dp: Float get() = Application.density * this
val Int.sp: Float get() = Application.scaledDensity * this

val Int.dpInt: Int get() = (Application.density * this).toInt()
val Int.spInt: Int get() = (Application.scaledDensity * this).toInt()

val Int.f get() = toFloat()
val Float.i get() = toInt()

@ColorInt
fun Context.getAttrColor(@AttrRes resId: Int): Int {
  val typedValue = TypedValue()
  val resolved = theme.resolveAttribute(resId, typedValue, true)
  assertThat(resolved) { "Attribute cannot be resolved" }
  return typedValue.data
}

fun Context.dimen(@DimenRes resId: Int): Float {
  return resources.getDimension(resId)
}