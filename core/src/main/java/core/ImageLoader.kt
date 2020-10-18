package core

import android.widget.ImageView
import androidx.fragment.app.Fragment

interface ImageLoader {
  
  fun load(fragment: Fragment, url: String, imageView: ImageView)
}