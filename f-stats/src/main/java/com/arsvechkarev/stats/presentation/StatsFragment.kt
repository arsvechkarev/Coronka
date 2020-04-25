package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.list.StatsAdapter
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromCache
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromNetwork
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Loggable
import core.extenstions.addBackPressedCallback
import core.extenstions.animateGoneAndScale
import core.extenstions.animateVisibleAndScale
import core.extenstions.visible
import core.recycler.DisplayableItem
import core.state.StateHandle
import core.state.isFresh
import kotlinx.android.synthetic.main.fragment_stats.layoutLoadingStats
import kotlinx.android.synthetic.main.fragment_stats.recyclerView
import kotlinx.android.synthetic.main.fragment_stats.simpleDialog
import kotlinx.android.synthetic.main.fragment_stats.textExplanation
import kotlinx.android.synthetic.main.fragment_stats.textGotIt
import kotlin.math.min

class StatsFragment : Fragment(R.layout.fragment_stats), Loggable {
  
  override val logTag = "StatsFragment"
  
  private lateinit var viewModel: StatsViewModel
  private val adapter = StatsAdapter(
    onOptionClick = { viewModel.filterList(it) },
    onOptionExplanationClick = { showOptionExplanationDialog(it) }
  )
  
  private val onBackPressedCallback = object : OnBackPressedCallback(false) {
    
    override fun handleOnBackPressed() {
      simpleDialog.dismiss()
      isEnabled = false
    }
  }
  
  private var savedInstanceState: Bundle? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    viewModel = StatsModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    textGotIt.setOnClickListener { simpleDialog.dismiss() }
    val width = min(resources.displayMetrics.widthPixels,
      resources.displayMetrics.heightPixels) * 0.7
    textExplanation.maxWidth = width.toInt()
    addBackPressedCallback(onBackPressedCallback)
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading(savedInstanceState != null)
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<StatsScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is Loading -> handleLoading()
        is LoadedFromCache -> handleLoadedFromCache(state)
        is LoadedFromNetwork -> handleLoadedFromNetwork(state)
        is FilteredCountries -> handleFilteringCountries(state)
      }
    }
  }
  
  private fun handleLoading() {
    layoutLoadingStats.visible()
  }
  
  private fun handleLoadedFromCache(state: LoadedFromCache) {
    displayLoadedResult(state.items)
  }
  
  private fun handleLoadedFromNetwork(state: LoadedFromNetwork) {
    displayLoadedResult(state.items)
  }
  
  private fun handleFilteringCountries(state: FilteredCountries) {
    if (state.isFresh) {
      adapter.updateFiltered(state.list)
    } else {
      adapter.submitList(state.list)
    }
  }
  
  private fun displayLoadedResult(items: List<DisplayableItem>) {
    adapter.submitList(items)
    layoutLoadingStats.animateGoneAndScale()
    recyclerView.animateVisibleAndScale()
  }
  
  private fun showOptionExplanationDialog(text: String) {
    textExplanation.text = text
    simpleDialog.show()
    onBackPressedCallback.isEnabled = true
  }
}