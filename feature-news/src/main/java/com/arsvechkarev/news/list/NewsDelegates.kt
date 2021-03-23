package com.arsvechkarev.news.list

import android.graphics.Paint
import android.os.Build
import android.text.TextPaint
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import base.resources.Colors
import base.resources.Dimens.ProgressBarSize
import base.resources.Fonts
import base.resources.Styles
import base.resources.Styles.BoldTextView
import base.resources.TextSizes
import base.views.ProgressBar
import base.views.newsitem.NewsItemImage
import base.views.newsitem.NewsItemView
import base.views.newsitem.NewsItemViewApi22
import base.views.newsitem.NewsItemViewApi23Plus
import base.views.newsitem.NewsItemsUtils
import com.arsvechkarev.news.R
import com.arsvechkarev.news.presentation.AdditionalItem
import com.arsvechkarev.recycler.delegate
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.childView
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.padding
import com.arsvechkarev.viewdsl.rippleBackground
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textColor
import com.arsvechkarev.viewdsl.visible
import com.arsvechkarev.viewdsl.withViewBuilder
import core.ImageLoader
import core.model.NewsItemWithPicture

fun newsItemDelegate(
  fragment: Fragment,
  imageLoader: ImageLoader,
  onNewsItemClicked: (NewsItemWithPicture) -> Unit
) = delegate<NewsItemWithPicture> {
  view(::buildNewsLayout)
  onInitViewHolder {
    itemView.onClick { onNewsItemClicked.invoke(item) }
  }
  onRecycled { itemView ->
    require(itemView is NewsItemView)
    imageLoader.clear(fragment, itemView)
  }
  onBind { itemView, item ->
    require(itemView is NewsItemView)
    itemView.setData(item.title, item.description, item.publishedDate)
    val width = itemView.context.resources.displayMetrics.widthPixels
    val size = NewsItemsUtils.getImageSize(width)
    imageLoader.load(fragment, item.imageUrl, itemView, size, size)
  }
}

fun additionalItemDelegate(onRetryItemClicked: () -> Unit) = delegate<AdditionalItem> {
  view { parent ->
    parent.withViewBuilder {
      FrameLayout(MatchParent, WrapContent) {
        padding(12.dp)
        child<LinearLayout>(WrapContent, WrapContent) {
          invisible()
          tag(NewsAdapter.FailureLayout)
          orientation(LinearLayout.HORIZONTAL)
          layoutGravity(Gravity.CENTER)
          gravity(Gravity.CENTER)
          child<ImageView>(WrapContent, WrapContent) {
            image(R.drawable.ic_error)
            padding(16.dp)
          }
          child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
            text(R.string.error_unknown_short)
          }
          child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
            tag(NewsAdapter.RetryButton)
            text(R.string.text_retry_short)
            textColor(Colors.Failure)
            margins(start = 16.dp)
          }
        }
        child<ProgressBar>(ProgressBarSize, ProgressBarSize) {
          tag(NewsAdapter.ProgressBar)
          layoutGravity(Gravity.CENTER)
        }
      }
    }
  }
  onInitViewHolder {
    itemView.childView(NewsAdapter.RetryButton).onClick {
      onRetryItemClicked.invoke()
      itemView.childView(NewsAdapter.ProgressBar).visible()
      itemView.childView(NewsAdapter.FailureLayout).invisible()
    }
  }
  onBind { itemView, element ->
    when (element.mode) {
      AdditionalItem.Mode.FAILURE -> {
        itemView.childView(NewsAdapter.FailureLayout).visible()
        itemView.childView(NewsAdapter.ProgressBar).invisible()
      }
      AdditionalItem.Mode.LOADING -> {
        itemView.childView(NewsAdapter.FailureLayout).invisible()
        itemView.childView(NewsAdapter.ProgressBar).visible()
      }
    }
  }
}

private fun buildNewsLayout(parent: View): View {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      typeface = Fonts.SegoeUiBold
      textSize = TextSizes.H5
      color = Colors.TextPrimary
    }
    val descriptionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      typeface = Fonts.SegoeUi
      textSize = TextSizes.H5
      color = Colors.TextSecondary
    }
    val datePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      typeface = Fonts.SegoeUi
      textSize = TextSizes.H5
      color = Colors.TextSecondary
    }
    return NewsItemViewApi23Plus(parent.context, titlePaint, descriptionPaint, datePaint)
  } else {
    return parent.withViewBuilder {
      val textTitle = TextView(style = Styles.NewsTextView) {
        font(Fonts.SegoeUiBold)
        maxLines = 3
      }
      val textDescription = TextView(style = Styles.NewsTextView) {
        maxLines = 2
      }
      val textTime = TextView().apply { setTextColor(Colors.TextSecondary) }
      val image = NewsItemImage(context)
      return NewsItemViewApi22(image, textTitle, textDescription, textTime).apply {
        rippleBackground(Colors.Ripple)
      }
    }
  }
}
