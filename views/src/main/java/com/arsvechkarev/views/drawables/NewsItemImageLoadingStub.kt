package com.arsvechkarev.views.drawables

import android.graphics.Path
import com.arsvechkarev.views.RoundedCornersImage

class NewsItemImageLoadingStub : BaseLoadingStub() {
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val radius = RoundedCornersImage.getCornerRadius(minOf(width, height).toInt())
    path.addRoundRect(0f, 0f, width, height, radius, radius, Path.Direction.CW)
  }
}