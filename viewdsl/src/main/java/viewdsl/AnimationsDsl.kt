@file:Suppress("UsePropertyAccessSyntax")

package viewdsl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
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
  if (alpha == 1f && visibility == View.VISIBLE) {
    andThen()
    return
  }
  alpha = 0f
  visible()
  animate().alpha(1f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction(andThen)
      .start()
}

fun View.animateInvisible(andThen: () -> Unit = {}) {
  if (visibility == INVISIBLE) {
    andThen()
    return
  }
  animate().alpha(0f).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction {
        invisible()
        andThen()
      }
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

fun ViewGroup.animateChildrenVisible() = forEachChild { it.animateVisible() }

fun ViewGroup.animateChildrenInvisible() = forEachChild { it.animateInvisible() }
