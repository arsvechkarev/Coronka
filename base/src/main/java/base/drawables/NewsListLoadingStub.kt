package base.drawables

import android.graphics.Path
import base.extensions.randomFloat
import base.resources.Dimens
import base.resources.TextSizes
import base.views.RoundedCornersImage
import base.views.newsitem.NewsItemsUtils
import kotlin.math.ceil

class NewsListLoadingStub : BaseLoadingStub() {
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val imageSize = NewsItemsUtils.getImageSize(width.toInt())
    val imageMargin = NewsItemsUtils.getImagePadding(width.toInt()).toFloat()
    val imageCount = ceil(height / (imageSize + imageMargin)).toInt()
    var top = imageMargin
    val cornersRadius = RoundedCornersImage.getCornerRadius(imageSize)
    val smallCornersRadius = cornersRadius / 3f
    val titleLeft = imageSize + imageMargin * 2
    val titleHeight = TextSizes.H5
    repeat(imageCount) {
      drawItem(path, imageMargin, top, imageSize, cornersRadius, width, titleHeight, titleLeft,
        smallCornersRadius)
      top += imageSize + imageMargin * 2.5f
    }
  }
  
  private fun drawItem(
    path: Path,
    imageMargin: Float,
    top: Float,
    imageSize: Int,
    cornersRadius: Float,
    width: Float,
    titleHeight: Float,
    titleLeft: Float,
    smallCornersRadius: Float
  ) {
    drawImage(path, imageMargin, top, imageSize, cornersRadius)
    drawDivider(top, imageSize, imageMargin, path, width)
    drawTitleLines(top, titleHeight, width, imageMargin, path, titleLeft, imageSize,
      smallCornersRadius)
  }
  
  private fun drawImage(
    path: Path,
    imageMargin: Float,
    top: Float,
    imageSize: Int,
    cornersRadius: Float
  ) {
    path.addRoundRect(imageMargin, top, imageMargin + imageSize,
      top + imageSize, cornersRadius, cornersRadius, Path.Direction.CW)
  }
  
  private fun drawDivider(
    top: Float,
    imageSize: Int,
    imageMargin: Float,
    path: Path,
    width: Float
  ) {
    val dividerTop = top + imageSize + imageMargin
    path.addRect(0f, dividerTop, width,
      dividerTop + Dimens.DividerHeight, Path.Direction.CW)
  }
  
  private fun drawTitleLines(
    top: Float,
    titleHeight: Float,
    width: Float,
    imageMargin: Float,
    path: Path,
    titleLeft: Float,
    imageSize: Int,
    smallCornersRadius: Float
  ) {
    var titleTop = top + titleHeight / 1.5f
    val maxRight = width - imageMargin * 3
    val count = (imageSize / (titleHeight * 1.5f) * 0.8f).toInt()
    repeat(count) {
      path.addRoundRect(titleLeft, titleTop,
        randomTitleWidth(titleLeft, width, imageSize, maxRight),
        titleTop + titleHeight, smallCornersRadius, smallCornersRadius,
        Path.Direction.CW)
      titleTop += titleHeight * 1.5f
    }
  }
  
  private fun randomTitleWidth(titleLeft: Float, width: Float, imageSize: Int, right: Float) =
      randomFloat(titleLeft + (width - imageSize) / 2.5f, right)
}