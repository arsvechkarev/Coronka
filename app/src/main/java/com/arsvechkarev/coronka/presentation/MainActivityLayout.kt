package com.arsvechkarev.coronka.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation.BL_TR
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import base.resources.Colors.GradientHeaderEnd
import base.resources.Colors.GradientHeaderStart
import base.resources.Dimens.LogoIconSize
import base.resources.Styles
import base.resources.Styles.BoldTextView
import base.resources.TextSizes
import base.views.DrawerGroupLinearLayout
import base.views.DrawerLayout
import com.arsvechkarev.coronka.R
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.backgroundColor
import com.arsvechkarev.viewdsl.backgroundGradient
import com.arsvechkarev.viewdsl.drawables
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.padding
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder

const val DrawerLayout = "DrawerLayout"
const val DrawerGroupLinearLayout = "DrawerGroupLinearLayout"
const val TextStatistics = "TextStatistics"
const val TextNews = "TextNews"
const val TextMap = "TextMap"
const val TextRankings = "TextRankings"
const val TextTips = "TextTips"

fun Context.buildMainActivityLayout() = withViewBuilder {
  DrawerLayout(context).apply {
    tag(DrawerLayout)
    size(MatchParent, MatchParent)
    child<FrameLayout, LayoutParams>(MatchParent, MatchParent) {
      id = R.id.fragmentContainer
    }
    child<View, LayoutParams>(MatchParent, MatchParent) {
      // Dummy view for shadow effect
      backgroundColor(Color.BLACK)
      alpha = 0f
    }
    child<ScrollView, LayoutParams>(MatchParent, MatchParent) {
      isFillViewport = true
      child<DrawerGroupLinearLayout, LayoutParams>(MatchParent, MatchParent) {
        tag(DrawerGroupLinearLayout)
        paddings(top = StatusBarHeight)
        orientation(LinearLayout.VERTICAL)
        backgroundGradient(BL_TR, GradientHeaderStart, GradientHeaderEnd)
        child<LinearLayout>(MatchParent, WrapContent) {
          orientation(LinearLayout.HORIZONTAL)
          gravity(Gravity.CENTER_VERTICAL)
          margins(start = 8.dp, top = 12.dp, bottom = 12.dp)
          child<ImageView>(LogoIconSize, LogoIconSize) {
            image(R.drawable.logo_icon)
            padding(12.dp)
          }
          child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
            padding(12.dp)
            text(R.string.app_name)
            textSize(TextSizes.H0)
          }
        }
        drawerTextVew(TextStatistics, R.drawable.ic_statistics, R.string.label_stats)
        drawerTextVew(TextNews, R.drawable.ic_newspaper, R.string.label_news)
        drawerTextVew(TextMap, R.drawable.ic_map, R.string.label_map)
        drawerTextVew(TextRankings, R.drawable.ic_rankings, R.string.label_rankings)
        drawerTextVew(TextTips, R.drawable.ic_tips, R.string.label_tips)
      }
    }
  }
}

private fun DrawerGroupLinearLayout.drawerTextVew(tag: String, drawableRes: Int, textRes: Int) {
  addView(TextView(context).apply {
    size(MatchParent, WrapContent)
    apply(Styles.DrawerTextView)
    tag(tag)
    drawables(start = drawableRes)
    text(textRes)
  })
}