package com.arsvechkarev.views.noconnection

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import viewdsl.DURATION_LONG
import viewdsl.DURATION_MEDIUM

fun createWifiAnimator(onUpdate: ValueAnimator.(Int) -> Unit): ValueAnimator {
  return ValueAnimator.ofInt(0, 255).apply {
    duration = DURATION_MEDIUM
    repeatMode = ValueAnimator.REVERSE
    repeatCount = 4
    addUpdateListener {
      onUpdate(it.animatedValue as Int)
    }
  }
}

fun createHourglassAnimator(onUpdate: ValueAnimator.(Int) -> Unit): ValueAnimator {
  return ValueAnimator.ofFloat(
    0f, -15f, 15f, -15f, 15f, -15f, 15f, -15f, 15f, -15f, 0f
  ).apply {
    duration = DURATION_LONG
    interpolator = AccelerateDecelerateInterpolator()
    addUpdateListener {
      // Calculate alpha value for hourglass here instead of creating new animator
      val alpha = (it.animatedFraction * 2 * 255).toInt().coerceAtMost(255)
      onUpdate(alpha)
    }
  }
}