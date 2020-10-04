package com.arsvechkarev.views.drawables

import android.content.Context
import android.graphics.Path
import android.view.View.MeasureSpec
import com.arsvechkarev.views.Chip
import com.arsvechkarev.views.R

class SelectedChipsLoadingStub(
  context: Context,
  textSizeRes: Int,
  chipMarginRes: Int,
) : BaseLoadingStub(context) {
  
  private val worldwideChipWidth: Float
  private val confirmedChipWidth: Float
  private val chipHeight: Float
  private val chipMargin = context.resources.getDimension(chipMarginRes)
  private val dividerHeight = context.resources.getDimension(R.dimen.divider_height)
  
  init {
    val tempChip = Chip(context).apply {
      setTextSize(context.resources.getDimension(textSizeRes))
      text = context.getString(R.string.text_worldwide)
    }
    val measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    tempChip.measure(measureSpec, measureSpec)
    chipHeight = tempChip.measuredHeight.toFloat()
    worldwideChipWidth = tempChip.measuredWidth.toFloat()
    tempChip.text = context.getString(R.string.text_confirmed)
    tempChip.measure(measureSpec, measureSpec)
    confirmedChipWidth = tempChip.measuredWidth.toFloat()
  }
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val radius = height / 2f
    path.addRoundRect(chipMargin, 0f, chipMargin + worldwideChipWidth, chipHeight,
      radius, radius, Path.Direction.CW)
    val left = chipMargin * 2f + worldwideChipWidth
    path.addRoundRect(left, 0f, left + confirmedChipWidth, chipHeight,
      radius, radius, Path.Direction.CW)
    path.addRect(0f, height - dividerHeight, width, height, Path.Direction.CW)
  }
}