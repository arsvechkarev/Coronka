package core.imageloading

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition

class CustomViewTarget<T>(
  private val target: T,
) : ViewTarget<View?, Drawable?>(target) where T : View, T : LoadableImage {
  
  override fun onResourceReady(
    drawable: Drawable,
    transition: Transition<in Drawable?>?
  ) {
    target.onImageLoaded(drawable)
  }
}