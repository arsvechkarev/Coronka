package com.arsvechkarev.coronka.presentation

import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.ButtonRetry
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.LayoutError
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextError
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.padding
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.progressbar.ProgressBar
import core.viewbuilding.Colors
import core.viewbuilding.Dimens
import core.viewbuilding.Dimens.ErrorLayoutImageSize
import core.viewbuilding.Styles
import core.viewbuilding.Styles.ClickableTextView
import core.viewbuilding.TextSizes

fun FrameLayout.addFailureLayout() = withViewBuilder {
  child<LinearLayout, FrameLayout.LayoutParams>(Size.MatchParent, Size.MatchParent) {
    tag(LayoutError)
    invisible()
    gravity(Gravity.CENTER)
    orientation(LinearLayout.VERTICAL)
    child<ImageView>(ErrorLayoutImageSize, ErrorLayoutImageSize) {
      image(R.drawable.image_unknown_error)
      margins(bottom = Dimens.ErrorLayoutTextPadding)
    }
    child<TextView>(WrapContent, WrapContent, style = Styles.BoldTextView) {
      tag(TextError)
      gravity(Gravity.CENTER)
      paddings(
        start = Dimens.ErrorLayoutTextPadding,
        end = Dimens.ErrorLayoutTextPadding,
        bottom = Dimens.ErrorLayoutTextPadding
      )
      textSize(TextSizes.H2)
      text("Email link has expired")
    }
    child<TextView>(WrapContent, WrapContent, style = ClickableTextView) {
      tag(ButtonRetry)
      text("Send link again")
    }
  }
}

fun FrameLayout.addLoadingLayout() = withViewBuilder {
  child<LinearLayout, FrameLayout.LayoutParams>(Size.MatchParent, WrapContent) {
    tag(MainActivity.LayoutLoading)
    orientation(LinearLayout.VERTICAL)
    gravity(Gravity.CENTER)
    child<TextView>(WrapContent, WrapContent, style = Styles.BoldTextView) {
      text("Verifying link...")
      padding(24.dp)
      textSize(TextSizes.H1)
    }
    addView(
      ProgressBar(context, Colors.Accent, ProgressBar.Thickness.NORMAL).apply {
        size(Dimens.ProgressBarSizeBig, Dimens.ProgressBarSizeBig)
      })
  }
}