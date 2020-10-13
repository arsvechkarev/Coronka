package core

import androidx.fragment.app.Fragment

interface ImageLoader {
  
  fun load(fragment: Fragment, url: String)
}