package com.arsvechkarev.news.list

import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arsvechkarev.news.R
import com.arsvechkarev.news.presentation.LoadingNextPage
import com.arsvechkarev.views.NewsItemImage
import com.arsvechkarev.views.NewsItemView
import com.arsvechkarev.views.ProgressBar
import com.arsvechkarev.views.RetryButton
import com.bumptech.glide.Glide
import core.extenstions.assertThat
import core.model.NewsItemWithPicture
import core.recycler.delegate
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.ProgressBarSize
import core.viewbuilding.Fonts
import core.viewbuilding.Styles
import core.viewbuilding.Styles.BoldTextView
import viewdsl.Ints.dp
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
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
import viewdsl.visible

fun newsItemDelegate(
  fragment: Fragment,
  onNewsItemClicked: (NewsItemWithPicture) -> Unit
) = delegate<NewsItemWithPicture> {
  buildView fn@{
    val textTitle = TextView(style = Styles.NewsTextView) {
      font(Fonts.SegoeUiBold)
      maxLines = 3
    }
    val textDescription = TextView(style = Styles.NewsTextView) {
      maxLines = 2
    }
    val textTime = TextView().apply { setTextColor(Colors.TextSecondary) }
    val image = NewsItemImage(context)
    return@fn NewsItemView(image, textTitle, textDescription, textTime).apply {
      rippleBackground(Colors.Ripple)
    }
  }
  onInitViewHolder {
    itemView.setOnClickListener { onNewsItemClicked.invoke(item) }
  }
  onBind { itemView, element ->
    assertThat(itemView is NewsItemView)
    itemView.textTitle.text = element.title
    itemView.textDescription.text = element.description
    itemView.textTime.text = element.publishedDate
    Glide.with(fragment)
        .load(element.imageUrl)
        .into(itemView.image)
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
          text(R.string.text_unknown_error)
        }
        child<RetryButton>(WrapContent, WrapContent) {
          tag(NewsAdapter.RetryButton)
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