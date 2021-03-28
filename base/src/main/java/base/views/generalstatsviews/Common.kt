package base.views.generalstatsviews

import android.graphics.Paint
import base.resources.TextSizes

fun getTextSizeFor(
  text1: String,
  text2: String,
  text3: String,
  itemSize: Int,
  paint: Paint
): Float {
  calculateTextSize(itemSize, text1, paint)
  val firstTitleSize = paint.textSize
  calculateTextSize(itemSize, text2, paint)
  val secondTitleSize = paint.textSize
  calculateTextSize(itemSize, text3, paint)
  val thirdTitleSize = paint.textSize
  return minOf(firstTitleSize, secondTitleSize, thirdTitleSize)
}

fun calculateTextSize(width: Int, text: String, paint: Paint) {
  paint.textSize = 10f
  while (true) {
    if (paint.textSize > TextSizes.H1) {
      break
    }
    val titleTextWidth = paint.measureText(text)
    if (titleTextWidth > width - getTextHorizontalMargin(width) * 2f) {
      break
    }
    paint.textSize++
  }
}

fun getItemCornersRadius(width: Int) = width / 35f

fun getTextHorizontalMargin(itemSize: Int) = itemSize / 12f