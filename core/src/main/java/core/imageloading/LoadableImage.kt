package core.imageloading

import android.graphics.Bitmap

interface LoadableImage {
  
  fun onBitmapLoaded(bitmap: Bitmap)
  
  fun onClearImage()
}