package core

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

object GlideImageLoader : ImageLoader {
  
  override fun load(fragment: Fragment, url: String, imageView: ImageView) {
    Glide.with(fragment)
        .load(url)
        .into(imageView)
  }
}