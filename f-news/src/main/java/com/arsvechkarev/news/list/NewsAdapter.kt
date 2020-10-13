package com.arsvechkarev.news.list

import com.arsvechkarev.news.R
import core.model.BasicNewsItem
import core.model.NewsItemWithPicture
import core.recycler.BaseListAdapter
import core.recycler.delegate
import kotlinx.android.synthetic.main.item_news_with_picture.view.newsItemTitle

class NewsAdapter(onNewsItemClicked: (NewsItemWithPicture) -> Unit) : BaseListAdapter(
  delegates = listOf(
    delegate<NewsItemWithPicture> {
      layoutRes(R.layout.item_news_with_picture)
      onInitViewHolder {
        itemView.newsItemTitle.setOnClickListener {
          println("qqq1 = ${item.title}")
        }
      }
      onBind { itemView, element ->
        itemView.newsItemTitle.text = element.title
      }
    },
    
    delegate<BasicNewsItem> {
      layoutRes(R.layout.item_news)
    },
  )
)