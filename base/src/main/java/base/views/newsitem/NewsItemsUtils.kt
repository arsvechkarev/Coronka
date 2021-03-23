package base.views.newsitem

import com.arsvechkarev.viewdsl.isOrientationPortrait

object NewsItemsUtils {
  
  fun getImageSize(width: Int): Int {
    if (isOrientationPortrait) {
      return (width / 3.2f).toInt()
    } else {
      return (width / 4.5f).toInt()
    }
  }
  
  fun getImagePadding(width: Int): Int {
    if (isOrientationPortrait) {
      return width / 22
    } else {
      return width / 32
    }
  }
  
  fun getVerticalPadding(width: Int) = width / 18
  
  fun getTextPadding(width: Int) = width / 33
  
  fun getCornerRadius(imageSize: Int) = imageSize / 20f
}