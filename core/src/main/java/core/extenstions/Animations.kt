package core.extenstions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
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