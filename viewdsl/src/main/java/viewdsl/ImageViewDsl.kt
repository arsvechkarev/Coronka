package viewdsl

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes

fun ImageView.image(@DrawableRes resId: Int) {
  setImageResource(resId)
}

fun ImageView.image(drawable: Drawable) {
  setImageDrawable(drawable)
}