package com.arsvechkarev.coronka.presentation

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextMap
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextNews
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextRankings
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextStatistics
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextTips
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.backgroundGradient
import com.arsvechkarev.viewdsl.drawables
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.padding
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.CheckmarkView
import com.arsvechkarev.views.DrawerGroupLinearLayout
import com.arsvechkarev.views.DrawerLayout
import com.arsvechkarev.views.ProgressBar
import com.arsvechkarev.views.ProgressBar.Thickness.THICK
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.CheckmarkHeight
import core.viewbuilding.Dimens.CheckmarkWidth
import core.viewbuilding.Dimens.ErrorLayoutImageSize
import core.viewbuilding.Dimens.ErrorLayoutTextPadding
import core.viewbuilding.Dimens.LogoIconSize
import core.viewbuilding.Dimens.ProgressBarSizeBig
import core.viewbuilding.Styles
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.TextSizes

fun Context.buildMainActivityLayout() = withViewBuilder {
  DrawerLayout(context).apply {
    tag(MainActivity.DrawerLayout)
    size(MatchParent, MatchParent)
    child<FrameLayout, LayoutParams>(MatchParent, MatchParent) {
      child<LinearLayout>(MatchParent, WrapContent) {
        tag(MainActivity.LayoutLoading)
        invisible()
        orientation(LinearLayout.VERTICAL)
        layoutGravity(Gravity.CENTER)
        gravity(Gravity.CENTER)
        child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
          tag(MainActivity.TextVerifyingLink)
          text(R.string.text_verifying_link)
          padding(24.dp)
          textSize(TextSizes.H1)
        }
        child<FrameLayout>(WrapContent, WrapContent) {
          child<CheckmarkView>(CheckmarkWidth, CheckmarkHeight) {
            invisible()
            tag(MainActivity.CheckmarkView)
          }
          addView(ProgressBar(context, Colors.Accent, THICK).apply {
            tag(MainActivity.ProgressBar)
            size(ProgressBarSizeBig, ProgressBarSizeBig)
          })
        }
      }
      child<LinearLayout, LayoutParams>(MatchParent, MatchParent) {
        tag(MainActivity.LayoutError)
        invisible()
        gravity(Gravity.CENTER)
        layoutGravity(Gravity.CENTER)
        orientation(LinearLayout.VERTICAL)
        child<ImageView>(ErrorLayoutImageSize, ErrorLayoutImageSize) {
          image(R.drawable.image_unknown_error)
          margins(bottom = ErrorLayoutTextPadding)
        }
        child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
          tag(MainActivity.TextError)
          gravity(Gravity.CENTER)
          paddings(
            start = ErrorLayoutTextPadding,
            end = ErrorLayoutTextPadding,
            bottom = ErrorLayoutTextPadding
          )
          textSize(TextSizes.H2)
          text(R.string.error_email_link_expired)
        }
        child<TextView>(WrapContent, WrapContent, style = Styles.ClickableTextView) {
          tag(MainActivity.ButtonRetry)
          text(R.string.text_resend_link)
        }
      }
      child<FrameLayout, LayoutParams>(MatchParent, MatchParent) {
        id = R.id.fragmentContainer
      }
    }
    child<DrawerGroupLinearLayout, LayoutParams>(MatchParent, MatchParent) {
      tag(MainActivity.DrawerGroupLinearLayout)
      orientation(LinearLayout.VERTICAL)
      backgroundGradient(GradientDrawable.Orientation.BL_TR, Colors.GradientHeaderStart,
        Colors.GradientHeaderEnd)
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

private fun DrawerGroupLinearLayout.drawerTextVew(tag: String, drawableRes: Int, textRes: Int) {
  addView(TextView(context).apply {
    size(MatchParent, WrapContent)
    apply(Styles.DrawerTextView)
    tag(tag)
    drawables(start = drawableRes)
    text(textRes)
  })
}