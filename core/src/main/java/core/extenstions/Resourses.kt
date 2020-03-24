package core.extenstions

import android.view.View

fun View.dp(value: Int) = resources.displayMetrics.density * value

fun View.sp(value: Int) = resources.displayMetrics.scaledDensity * value

val Int.f get() = toFloat()

val Float.i get() = toInt()