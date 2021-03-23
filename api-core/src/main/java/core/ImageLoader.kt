package core

import android.view.View
import androidx.fragment.app.Fragment

interface ImageLoader {
  
  fun <T> load(fragment: Fragment, url: String, target: T, width: Int, height: Int)
      where T : View, T : LoadableImage
  
  fun <T> clear(fragment: Fragment, target: T)
      where T : View, T : LoadableImage
}