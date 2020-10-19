package core.imageloading

import android.graphics.drawable.Drawable

interface LoadableImage {
  
  fun onImageLoaded(image: Drawable)
}