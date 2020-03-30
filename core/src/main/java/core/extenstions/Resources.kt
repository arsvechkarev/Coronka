package core.extenstions

import core.ApplicationConfig

val Int.dp: Float get() = ApplicationConfig.Densities.density * this
val Int.sp: Float get() = ApplicationConfig.Densities.scaledDensity * this

val Int.dpInt: Int get() = (ApplicationConfig.Densities.density * this).toInt()
val Int.spInt: Int get() = (ApplicationConfig.Densities.scaledDensity * this).toInt()