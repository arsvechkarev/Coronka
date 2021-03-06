package base.drawables

import android.graphics.Path
import base.resources.TextSizes
import kotlin.math.ceil
import kotlin.random.Random

class RankingsListLoadingStub : BaseLoadingStub() {
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val itemHeight = TextSizes.H4 * 1.5f
    val itemMargin = itemHeight * 0.4f
    val itemCount = ceil(height / (itemHeight + itemMargin)).toInt()
    var top = itemMargin
    val cornersRadius = itemHeight / 6f
    val rectLeft = itemHeight * 2.8f
    repeat(itemCount) {
      val circleY = top + itemHeight / 2f
      val radius = itemHeight / 2f
      path.addCircle(itemHeight, circleY, radius, Path.Direction.CW)
      val rectWidth = Random.nextInt((width * 0.3).toInt(), ((width - rectLeft) * 0.8f).toInt())
      path.addRoundRect(rectLeft, top, rectLeft + rectWidth, top + itemHeight, cornersRadius,
        cornersRadius, Path.Direction.CW)
      top += itemHeight + itemMargin
    }
  }
}