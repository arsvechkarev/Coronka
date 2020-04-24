package com.arsvechkarev.faq.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.faq.R
import com.arsvechkarev.faq.di.FAQModuleInjector
import com.arsvechkarev.faq.list.FAQAdapter
import core.extenstions.getAttrColor

class FAQFragment : Fragment() {
  
  private lateinit var recyclerView: RecyclerView
  private lateinit var viewModel: FAQViewModel
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val adapter = FAQAdapter()
    recyclerView = RecyclerView(requireContext()).apply {
      setBackgroundColor(requireContext().getAttrColor(R.attr.colorBackground))
      layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
      layoutManager = LinearLayoutManager(requireContext())
      this.adapter = adapter
    }
    viewModel = FAQModuleInjector.provideViewModel(this).apply {
      data.observe(this@FAQFragment, Observer { adapter.submitList(it) })
      loadData()
    }
    return recyclerView
  }
}