package com.arsvechkarev.news.list

import com.arsvechkarev.news.R
import com.arsvechkarev.news.presentation.NewsFragment
import com.bumptech.glide.Glide
import core.model.BasicNewsItem
import core.model.NewsItemWithPicture
import core.recycler.BaseListAdapter
import core.recycler.delegate
import kotlinx.android.synthetic.main.item_news.view.newsItemTitle
import kotlinx.android.synthetic.main.item_news_with_picture.view.newsItemWithPictureImage
import kotlinx.android.synthetic.main.item_news_with_picture.view.newsItemWithPictureTitle

class NewsAdapter(
  fragment: NewsFragment,
  onNewsItemClicked: (BasicNewsItem) -> Unit
) : BaseListAdapter(
  delegates = listOf(
    delegate<NewsItemWithPicture> {
      layoutRes(R.layout.item_news_with_picture)
      onInitViewHolder {
        itemView.setOnClickListener { onNewsItemClicked(item) }
      }
      onBind { itemView, element ->
        itemView.newsItemWithPictureTitle.text = element.title
        Glide.with(fragment)
            .load(element.imageUrl)
            .into(itemView.newsItemWithPictureImage)
      }
    },
    
    delegate<BasicNewsItem> {
      layoutRes(R.layout.item_news)
      onInitViewHolder {
        itemView.setOnClickListener { onNewsItemClicked(item) }
      }
      onBind { itemView, element ->
        itemView.newsItemTitle.text = element.title
      }
    },
  )
)