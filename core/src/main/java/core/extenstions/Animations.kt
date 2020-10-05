@file:Suppress("UsePropertyAccessSyntax")

package core.extenstions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

const val DURATION_SHORT = 150L
const val DURATION_DEFAULT = 300L
const val DURATION_MEDIUM = 500L
const val DURATION_LONG = 800L

val AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

fun Animator.cancelIfRunning() {
  if (isRunning) {
    cancel()
  }
}

fun Animator.doOnStart(block: () -> Unit) {
  addListener(object : AnimatorListenerAdapter() {
    override fun onAnimationStart(animation: Animator?) {
      block()
      removeListener(this)
    }
  })
}

fun Animator.doOnEnd(block: () -> Unit) {
  addListener(object : AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: Animator?) {
      block()
      removeListener(this)
    }
  })
}

fun View.animateVisible(andThen: () -> Unit = {}) {
  alpha = 0f
  visible()
  animate().alpha(1f).setDuration(DURATION_DEFAULT)
      .withEndAction { }
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction(andThen)
      .start()
}

fun animateVisible(vararg views: View, andThen: () -> Unit = {}) {
  var andThenPosted = false
  for (view in views) {
    if (!andThenPosted) {
      view.animateVisible(andThen)
      andThenPosted = true
    } else {
      view.animateVisible()
    }
  }
}

fun View.animateInvisible(andThen: () -> Unit = {}) {
  animate().alpha(0f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction {
        invisible()
        andThen()
      }
      .start()
}

fun animateInvisible(vararg views: View, andThen: () -> Unit = {}) {
  var andThenPosted = false
  for (view in views) {
    if (!andThenPosted) {
      view.animateInvisible(andThen)
      andThenPosted = true
    } else {
      view.animateInvisible()
    }
  }
}

fun View.animateVisibleAndScale(andThen: () -> Unit = {}) {
  isClickable = false
  alpha = 0f
  scaleX = 0.9f
  scaleY = 0.9f
  visible()
  animate().alpha(1f)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction {
        isClickable = true
        andThen()
      }
}


fun View.animateInvisibleAndScale() {
  isClickable = false
  animate().alpha(0f)
      .scaleX(1.2f)
      .scaleY(1.2f)
      .setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction {
        invisible()
        isClickable = true
        scaleX = 1f
        scaleY = 1f
        alpha = 1f
      }
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