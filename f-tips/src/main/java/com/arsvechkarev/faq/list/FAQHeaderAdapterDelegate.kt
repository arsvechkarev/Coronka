package com.arsvechkarev.faq.list

import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.faq.R
import core.extenstions.inflate
import core.model.FAQHeader
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_faq_header.view.textVisitWHO

class FAQHeaderAdapterDelegate : AdapterDelegate(FAQHeader::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return object : RecyclerView.ViewHolder(parent.inflate(R.layout.item_faq_header)) {
      init {
        itemView.textVisitWHO.text = HtmlCompat.fromHtml(
          itemView.resources.getString(R.string.text_visit_who),
          HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        itemView.textVisitWHO.movementMethod = LinkMovementMethod.getInstance()
      }
    }
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {}
}