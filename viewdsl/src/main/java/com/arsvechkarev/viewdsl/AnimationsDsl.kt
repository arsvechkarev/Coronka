@file:Suppress("UsePropertyAccessSyntax")

package com.arsvechkarev.viewdsl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.Animatable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import config.AnimationsConfigurator

val AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
val OvershootInterpolator = OvershootInterpolator()

fun Animator.startIfNotRunning() {
  if (!isRunning) start()
}

fun Animator.cancelIfRunning() {
  if (isRunning) cancel()
}

fun Animatable.startIfNotRunning() {
  if (!isRunning) start()
}

fun Animatable.stopIfRunning() {
  if (isRunning) stop()
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
  animate().alpha(1f).setDuration(AnimationsConfigurator.DurationDefault)
      .setInterpolator(AccelerateDecelerateInterpolator)
      .withEndAction(andThen)
      .start()
}

fun View.animateInvisible(andThen: () -> Unit = {}) {
  animate().alpha(0f).setDuration(AnimationsConfigurator.DurationDefault)
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

fun ViewGroup.animateChildrenVisible() = forEachChild { it.animateVisible() }

fun ViewGroup.animateChildrenInvisible() = forEachChild { it.animateInvisible() }