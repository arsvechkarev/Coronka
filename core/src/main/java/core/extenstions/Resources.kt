package core.extenstions

import core.ApplicationConfig

val Double.dp: Float get() = (ApplicationConfig.Values.density * this).toFloat()
val Double.sp: Float get() = (ApplicationConfig.Values.scaledDensity * this).toFloat()

val Int.dp: Float get() = ApplicationConfig.Values.density * this
val Int.sp: Float get() = ApplicationConfig.Values.scaledDensity * this

val Int.dpInt: Int get() = (ApplicationConfig.Values.density * this).toInt()
val Int.spInt: Int get() = (ApplicationConfig.Values.scaledDensity * this).toInt()