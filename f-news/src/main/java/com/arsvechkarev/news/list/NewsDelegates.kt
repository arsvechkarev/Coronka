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
import com.arsvechkarev.news.R
import com.arsvechkarev.news.presentation.LoadingNextPage
import com.arsvechkarev.views.ProgressBar
import com.arsvechkarev.views.newsitem.NewsItemImage
import com.arsvechkarev.views.newsitem.NewsItemView
import com.arsvechkarev.views.newsitem.NewsItemViewApi22
import com.arsvechkarev.views.newsitem.NewsItemViewApi23Plus
import com.arsvechkarev.views.newsitem.NewsItemsUtils
import core.extenstions.assertThat
import core.imageloading.ImageLoader
import core.model.NewsItemWithPicture
import core.recycler.delegate
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.ProgressBarSize
import core.viewbuilding.Fonts
import core.viewbuilding.Styles
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.TextSizes
import viewdsl.Ints.dp
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.ViewBuilder
import viewdsl.buildView
import viewdsl.childView
import viewdsl.font
import viewdsl.gravity
import viewdsl.image
import viewdsl.invisible
import viewdsl.layoutGravity
import viewdsl.margins
import viewdsl.onClick
import viewdsl.orientation
import viewdsl.padding
import viewdsl.rippleBackground
import viewdsl.tag
import viewdsl.text
import viewdsl.textColor
import viewdsl.visible

fun newsItemDelegate(
  fragment: Fragment,
  imageLoader: ImageLoader,
  onNewsItemClicked: (NewsItemWithPicture) -> Unit
) = delegate<NewsItemWithPicture> {
  buildView {
    buildNewsLayout(this)
  }
  onInitViewHolder {
    itemView.setOnClickListener { onNewsItemClicked.invoke(item) }
    onViewRecycled()
  }
  onRecycled { itemView ->
    assertThat(itemView is NewsItemView)
    imageLoader.clear(fragment, itemView)
  }
  onBind { itemView, item ->
    assertThat(itemView is NewsItemView)
    itemView.setData(item.title, item.description, item.publishedDate)
    val width = itemView.context.resources.displayMetrics.widthPixels
    val size = NewsItemsUtils.getImageSize(width)
    imageLoader.load(fragment, item.imageUrl, itemView, size, size)
  }
}

fun loadingNextPageDelegate(onRetryItemClicked: () -> Unit) = delegate<LoadingNextPage> {
  buildView {
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
          text(R.string.text_unknown_error_short)
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
        setColor(Colors.Accent)
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
}

private fun buildNewsLayout(viewBuilder: ViewBuilder): View {
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
    return NewsItemViewApi23Plus(viewBuilder.context, titlePaint, descriptionPaint, datePaint)
  } else {
    return viewBuilder.context.buildView {
      val textTitle = viewBuilder.TextView(style = Styles.NewsTextView) {
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
