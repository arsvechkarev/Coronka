package core.extenstions

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

const val DURATION_DEFAULT = 200L

fun View.rotateTo(angle: Float) {
  animate().rotation(angle).setDuration(DURATION_DEFAULT)
      .setInterpolator(AccelerateDecelerateInterpolator()).start()
}