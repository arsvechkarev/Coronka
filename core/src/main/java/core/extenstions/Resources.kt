package core.extenstions

import core.Application

val Double.dp: Float get() = (Application.Values.density * this).toFloat()
val Double.sp: Float get() = (Application.Values.scaledDensity * this).toFloat()

val Int.dp: Float get() = Application.Values.density * this
val Int.sp: Float get() = Application.Values.scaledDensity * this

val Int.dpInt: Int get() = (Application.Values.density * this).toInt()
val Int.spInt: Int get() = (Application.Values.scaledDensity * this).toInt()