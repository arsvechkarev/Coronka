@file:Suppress("UsePropertyAccessSyntax")

package core.extenstions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

const val DURATION_DEFAULT = 200L
const val DURATION_MEDIUM = 400L

fun Animator.cancelIfRunning() {
  if (isRunning) {
    cancel()
  }
}

fun Animator.doOnEnd(block: () -> Unit) {
  addListener(object : AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: Animator?) {
      block()
    }
  })
}

fun ViewPropertyAnimator.doOnEnd(block: () -> Unit): ViewPropertyAnimator {
  setListener(object : AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: Animator?) {
      block()
    }
  })
  return this
}

fun View.animateVisible(andThen: () -> Unit = {}) {
  alpha = 0f
  visible()
  animate().alpha(1f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd(andThen)
}

fun View.animateVisibleAndScale(andThen: () -> Unit = {}) {
  scaleX = 0.9f
  scaleY = 0.9f
  alpha = 0f
  visible()
  animate().alpha(1f)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd(andThen)
}

fun View.animateGone(andThen: () -> Unit = {}) {
  animate().alpha(0f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd { gone(); andThen() }
}


fun View.animateGoneAndScale() {
  animate().alpha(0f)
      .scaleX(1.3f)
      .scaleY(1.3f)
      .setDuration(DURATION_MEDIUM)
      .setInterpolator(AccelerateDecelerateInterpolator())
}

fun View.rotateTo(angle: Float) {
  animate().rotation(angle).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator()).start()
}

fun View.animateColor(startColor: Int, endColor: Int, andThen: () -> Unit = {}) {
  ObjectAnimator.ofObject(this,
    "backgroundColor", ArgbEvaluator(), startColor, endColor).apply {
    duration = DURATION_DEFAULT
    interpolator = FastOutSlowInInterpolator()
    if (andThen != {}) {
      doOnEnd(andThen)
    }
    start()
  }
}