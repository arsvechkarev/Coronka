package core.extenstions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import core.Application

val Double.dp: Float get() = (Application.density * this).toFloat()
val Double.sp: Float get() = (Application.scaledDensity * this).toFloat()

val Int.dp: Float get() = Application.density * this
val Int.sp: Float get() = Application.scaledDensity * this

val Int.dpInt: Int get() = (Application.density * this).toInt()
val Int.spInt: Int get() = (Application.scaledDensity * this).toInt()

val Int.f get() = toFloat()

val Float.i get() = toInt()

@ColorInt
fun Context.getAttrColor(@AttrRes resId: Int): Int {
  val typedValue = TypedValue()
  val resolved = theme.resolveAttribute(resId, typedValue, false)
  assertThat(resolved) { "Attribute cannot be resolved" }
  return typedValue.data
}

@ColorInt
fun Context.retrieveColor(@ColorRes colorRes: Int): Int {
  return ResourcesCompat.getColor(resources, colorRes, theme)
}