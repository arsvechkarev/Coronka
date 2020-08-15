package com.arsvechkarev.rankings.list

import android.graphics.Paint
import android.text.TextPaint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.StatsSmallHeaderViewGroup
import com.arsvechkarev.views.StatsSmallView
import core.FontManager
import core.extenstions.inflate
import core.recycler.ListAdapterDelegate
import core.recycler.SortableDisplayableItem

class HeaderItemAdapterDelegate : ListAdapterDelegate(Header2::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val statsSmallHeaderViewGroup = parent.inflate(R.layout.item_rankings_header) as StatsSmallHeaderViewGroup
    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      typeface = FontManager.segoeUI
      textSize = parent.context.resources.getDimension(R.dimen.rankings_small_stats_view_text_size)
    }
    val rankTextViewWidth = textPaint.measureText(StatsSmallView.RANK_TEXT_FOR_MEASURE)
    val numberTextViewWidth = textPaint.measureText(StatsSmallView.NUMBER_TEXT_FOR_MEASURE)
    statsSmallHeaderViewGroup.setMaxTextsWidths(
      rankTextViewWidth,
      (parent.width - rankTextViewWidth - numberTextViewWidth) * 0.8f,
      parent.resources.getDimension(R.dimen.rankings_item_header_country_name_offset),
      numberTextViewWidth
    )
    return object : RecyclerView.ViewHolder(statsSmallHeaderViewGroup) {}
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SortableDisplayableItem) = Unit
  
  object Header2 : SortableDisplayableItem {
    // Item is only one in adapter, so it doesn't matter what id it has
    override val id = -1
    override fun equals(other: Any?) = false
  }
}