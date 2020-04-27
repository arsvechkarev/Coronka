package com.arsvechkarev.faq.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.faq.R
import com.arsvechkarev.faq.di.FAQModuleInjector
import com.arsvechkarev.faq.list.FAQAdapter
import core.extenstions.retrieveColor

class FAQFragment : Fragment() {
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val adapter = FAQAdapter()
    val frameLayout = FrameLayout(requireContext()).apply {
      setBackgroundColor(requireContext().retrieveColor(R.color.dark_background))
      layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
    val recyclerView = RecyclerView(requireContext()).apply {
      layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
      layoutManager = LinearLayoutManager(requireContext())
      this.adapter = adapter
    }
    frameLayout.addView(recyclerView)
    FAQModuleInjector.provideViewModel(this).apply {
      data.observe(this@FAQFragment, Observer {
        adapter.submitList(it)
      })
      loadData()
    }
    return frameLayout
  }
}