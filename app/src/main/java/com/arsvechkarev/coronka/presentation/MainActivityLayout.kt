package com.arsvechkarev.coronka.presentation

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arsvechkarev.coronka.R
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size
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
import core.viewbuilding.Colors
import core.viewbuilding.Dimens
import core.viewbuilding.Styles
import core.viewbuilding.TextSizes

fun Context.buildMainActivityLayout() = withViewBuilder {
  DrawerLayout(context).apply {
    tag(MainActivity.DrawerLayout)
    size(Size.MatchParent, Size.MatchParent)
    child<FrameLayout, FrameLayout.LayoutParams>(Size.MatchParent, Size.MatchParent) {
      child<LinearLayout>(Size.MatchParent, Size.WrapContent) {
        tag(MainActivity.LayoutLoading)
        invisible()
        orientation(LinearLayout.VERTICAL)
        layoutGravity(Gravity.CENTER)
        gravity(Gravity.CENTER)
        child<TextView>(Size.WrapContent, Size.WrapContent, style = Styles.BoldTextView) {
          tag(MainActivity.TextVerifyingLink)
          text(R.string.text_verifying_link)
          padding(24.dp)
          textSize(TextSizes.H1)
        }
        child<FrameLayout>(Size.WrapContent, Size.WrapContent) {
          child<CheckmarkView>(Dimens.CheckmarkWidth, Dimens.CheckmarkHeight) {
            invisible()
            tag(MainActivity.CheckmarkView)
          }
          addView(ProgressBar(context, Colors.Accent, ProgressBar.Thickness.THICK).apply {
            tag(MainActivity.ProgressBar)
            size(Dimens.ProgressBarSizeBig, Dimens.ProgressBarSizeBig)
          })
        }
      }
      child<LinearLayout, FrameLayout.LayoutParams>(Size.MatchParent, Size.MatchParent) {
        tag(MainActivity.LayoutError)
        invisible()
        gravity(Gravity.CENTER)
        layoutGravity(Gravity.CENTER)
        orientation(LinearLayout.VERTICAL)
        child<ImageView>(Dimens.ErrorLayoutImageSize, Dimens.ErrorLayoutImageSize) {
          image(R.drawable.image_unknown_error)
          margins(bottom = Dimens.ErrorLayoutTextPadding)
        }
        child<TextView>(Size.WrapContent, Size.WrapContent, style = Styles.BoldTextView) {
          tag(MainActivity.TextError)
          gravity(Gravity.CENTER)
          paddings(
            start = Dimens.ErrorLayoutTextPadding,
            end = Dimens.ErrorLayoutTextPadding,
            bottom = Dimens.ErrorLayoutTextPadding
          )
          textSize(TextSizes.H2)
          text(R.string.error_email_link_expired)
        }
        child<TextView>(Size.WrapContent, Size.WrapContent, style = Styles.ClickableTextView) {
          tag(MainActivity.ButtonRetry)
          text(R.string.text_resend_link)
        }
      }
      child<FrameLayout, FrameLayout.LayoutParams>(Size.MatchParent, Size.MatchParent) {
        id = R.id.fragmentContainer
      }
    }
    child<DrawerGroupLinearLayout, FrameLayout.LayoutParams>(Size.MatchParent, Size.MatchParent) {
      tag(MainActivity.DrawerGroupLinearLayout)
      orientation(LinearLayout.VERTICAL)
      backgroundGradient(GradientDrawable.Orientation.BL_TR, Colors.GradientHeaderStart,
        Colors.GradientHeaderEnd)
      child<TextView>(Size.WrapContent, Size.WrapContent, style = Styles.BoldTextView) {
        margins(start = 16.dp, top = 24.dp, bottom = 24.dp)
        drawables(start = R.drawable.logo_icon)
        compoundDrawablePadding = 16.dp
        gravity(Gravity.CENTER)
        text(R.string.title_covid_19)
        textSize(TextSizes.H0)
      }
      drawerTextVew(MainActivity.TextStatistics, R.drawable.ic_statistics, R.string.label_stats)
      drawerTextVew(MainActivity.TextNews, R.drawable.ic_newspaper, R.string.label_news)
      drawerTextVew(MainActivity.TextMap, R.drawable.ic_map, R.string.label_map)
      drawerTextVew(MainActivity.TextRankings, R.drawable.ic_rankings, R.string.label_rankings)
      drawerTextVew(MainActivity.TextTips, R.drawable.ic_tips, R.string.label_tips)
    }
  }
}

private fun DrawerGroupLinearLayout.drawerTextVew(tag: String, drawableRes: Int, textRes: Int) {
  addView(TextView(context).apply {
    size(Size.MatchParent, Size.WrapContent)
    apply(Styles.DrawerTextView)
    tag(tag)
    drawables(start = drawableRes)
    text(textRes)
  })
}