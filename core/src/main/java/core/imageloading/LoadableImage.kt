package core.imageloading

import android.graphics.Bitmap

/**
 * Represents an image that could be loaded and cleared
 */
interface LoadableImage {
  
  fun onBitmapLoaded(bitmap: Bitmap)
  
  fun onClearImage()
}