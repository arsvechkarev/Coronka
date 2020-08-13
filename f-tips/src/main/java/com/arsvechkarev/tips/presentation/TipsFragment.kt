package com.arsvechkarev.tips.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.common.BottomSheetBehavior
import com.arsvechkarev.tips.R
import core.recycler.DisplayableItem
import core.recycler.createAdapter
import kotlinx.android.synthetic.main.fragment_tips.recyclerView
import kotlinx.android.synthetic.main.fragment_tips.textAnswer
import kotlinx.android.synthetic.main.fragment_tips.textTitle
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheet
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheetCross
import kotlinx.android.synthetic.main.item_faq_2.view.tipsItemFaq
import kotlinx.android.synthetic.main.item_header.view.tipsImageDrawer

class TipsFragment : Fragment(R.layout.fragment_tips) {
  
  object Header : DisplayableItem
  class FAQItem(
    val questionLayoutRes: Int,
    val answerLayoutRes: Int
  ) : DisplayableItem
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val behavior = (tipsBottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
    val bottomSheetBehavior = behavior as BottomSheetBehavior<View>
    tipsBottomSheetCross.setOnClickListener { bottomSheetBehavior.hide() }
    
    val adapter = createAdapter {
      delegate(Header::class) {
        data(Header)
        layoutRes(R.layout.item_header)
        onViewHolderInitialization { header, _ ->
          header.itemView.tipsImageDrawer.setOnClickListener {
            Toast.makeText(requireContext(), "Lol", Toast.LENGTH_SHORT).show()
          }
        }
      }
      delegate(FAQItem::class) {
        data(listOf(
          FAQItem(R.string.q1, R.string.a1),
          FAQItem(R.string.q2, R.string.a2),
          FAQItem(R.string.q3, R.string.a3),
          FAQItem(R.string.q4, R.string.a4),
          FAQItem(R.string.q5, R.string.a5)
        ))
        layoutRes(R.layout.item_faq_2)
        onViewHolderInitialization { header, data ->
          header.itemView.setOnClickListener {
            val item = data[header.adapterPosition - 1]
            textTitle.text = getString(item.questionLayoutRes)
            textAnswer.text = getString(item.answerLayoutRes)
            behavior.show()
          }
        }
        onBindViewHolder { view, faqItem ->
          view.tipsItemFaq.text = getString(faqItem.questionLayoutRes)
        }
      }
    }
    
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter
  }
  
}