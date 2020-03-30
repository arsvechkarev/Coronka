package core.extenstions

import core.ApplicationConfig

val Int.dp: Float get() = ApplicationConfig.Densities.density * this
val Int.sp: Float get() = ApplicationConfig.Densities.scaledDensity * this