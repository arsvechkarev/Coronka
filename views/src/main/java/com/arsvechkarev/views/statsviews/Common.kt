package com.arsvechkarev.views.statsviews

import android.graphics.Paint

fun getTextSizeFor(
  text1: String,
  text2: String,
  text3: String,
  width: Int,
  paint: Paint
): Float {
  val squareSize = getSquareSize(width)
  calculateTextSize(squareSize, text1, paint)
  val firstTitleSize = paint.textSize
  calculateTextSize(squareSize, text2, paint)
  val secondTitleSize = paint.textSize
  calculateTextSize(squareSize, text3, paint)
  val thirdTitleSize = paint.textSize
  return minOf(firstTitleSize, secondTitleSize, thirdTitleSize)
}

fun calculateTextSize(width: Int, text: String, paint: Paint) {
  paint.textSize = 10f
  while (true) {
    val titleTextWidth = paint.measureText(text)
    if (titleTextWidth > width - getTextHorizontalMargin(width) * 2f) {
      break
    }
    paint.textSize++
  }
}

fun getSquareSize(width: Int): Int {
  return (width - getItemMargin(width) * 2) / 3
}

fun getItemCornersRadius(width: Int): Float {
  return width / 35f
}

fun getItemMargin(width: Int): Int {
  return width / 18
}

fun getTextHorizontalMargin(itemSize: Int) = itemSize / 12f

fun getTextVerticalMargin(itemSize: Int) = itemSize / 8f