package coreimpl

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import core.ImageLoader
import core.LoadableImage
import com.bumptech.glide.request.target.CustomViewTarget as GlideCustomViewTarget

object GlideImageLoader : ImageLoader {
  
  override fun <T> load(
    fragment: Fragment,
    url: String,
    target: T,
    width: Int,
    height: Int,
  ) where T : View, T : LoadableImage {
    Glide.with(fragment)
        .asBitmap()
        .load(url)
        .thumbnail(0.04f)
        .into(object : GlideCustomViewTarget<T, Bitmap>(target) {
  
          override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
            target.onBitmapLoaded(bitmap)
          }
  
          override fun onLoadFailed(p0: Drawable?) {
            target.onClearImage()
          }
  
          override fun onResourceCleared(p0: Drawable?) {
            target.onClearImage()
          }
        })
  }
  
  override fun <T> clear(fragment: Fragment, target: T)
      where T : LoadableImage, T : View {
    Glide.with(fragment).clear(target)
  }
}