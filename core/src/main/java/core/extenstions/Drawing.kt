package core.extenstions

import android.graphics.Canvas

inline fun Canvas.block(action: Canvas.() -> Unit) {
  save()
  action(this)
  restore()
}