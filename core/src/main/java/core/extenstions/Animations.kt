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

const val DURATION_DEFAULT = 300L
const val DURATION_MEDIUM = 500L

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
  alpha = 0f
  scaleX = 0.9f
  scaleY = 0.9f
  visible()
  animate().alpha(1f)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd(andThen)
}

fun View.animateInvisible(andThen: () -> Unit = {}) {
  animate().alpha(0f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd {
        invisible()
        alpha = 1f
        andThen()
      }
}


fun View.animateInvisibleAndScale() {
  animate().alpha(0f)
      .scaleX(1.2f)
      .scaleY(1.2f)
      .setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator())
      .doOnEnd {
        invisible()
        scaleX = 1f
        scaleY = 1f
        alpha = 1f
      }
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