package core.imageloading

import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

object GlideImageLoader : ImageLoader {
  
  override fun <T> load(
    fragment: Fragment,
    url: String,
    target: T,
    width: Int,
    height: Int,
  ) where T : View, T : LoadableImage {
    Glide.with(fragment)
        .load(url)
        .centerInside()
        .override(width, height)
        .into(CustomViewTarget<T>(target))
  }
  
  override fun <T> clear(fragment: Fragment, target: T)
      where T : LoadableImage, T : View {
    Glide.with(fragment).clear(target)
  }
}