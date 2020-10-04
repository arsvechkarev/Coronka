package com.arsvechkarev.views.drawables

import android.content.Context
import android.graphics.Path
import kotlin.random.Random

class RankingsListLoadingStub(context: Context) : BaseLoadingStub(context) {
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val itemHeight = height / ITEM_HEIGHT_COEFFICIENT
    val itemMargin = (height - itemHeight * ITEM_COUNT) / (ITEM_COUNT + 1f)
    var top = itemMargin
    val cornersRadius = itemHeight / 6f
    val rectLeft = itemHeight * 2.8f
    repeat(ITEM_COUNT) {
      val circleY = top + itemHeight / 2f
      val radius = itemHeight / 2f
      path.addCircle(itemHeight, circleY, radius, Path.Direction.CW)
      val rectWidth = Random.nextInt((width * 0.3).toInt(), ((width - rectLeft) * 0.8f).toInt())
      path.addRoundRect(rectLeft, top, rectLeft + rectWidth, top + itemHeight, cornersRadius,
        cornersRadius, Path.Direction.CW)
      top += itemHeight + itemMargin
    }
  }
  
  companion object {
    
    const val ITEM_COUNT = 13
    const val ITEM_HEIGHT_COEFFICIENT = 18
  }
}