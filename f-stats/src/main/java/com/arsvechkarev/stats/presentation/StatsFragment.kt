package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.list.HeaderAdapterDelegate.HeaderViewHolder
import com.arsvechkarev.stats.list.StatsAdapter
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedAll
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Loggable
import core.StateHandle
import core.extenstions.invisible
import core.extenstions.visible
import core.log
import kotlinx.android.synthetic.main.fragment_stats.layoutLoadingStats
import kotlinx.android.synthetic.main.fragment_stats.recyclerView

class StatsFragment : Fragment(R.layout.fragment_stats), Loggable {
  
  override val logTag = "StatsFragment"
  
  private lateinit var viewModel: StatsViewModel
  private val adapter = StatsAdapter(onOptionClick = { viewModel.filterList(it) })
  private var savedInstanceState: Bundle? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    viewModel = StatsModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading(savedInstanceState != null)
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<StatsScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is Loading -> handleLoading()
        is LoadedAll -> handleLoadedAll(state)
        is FilteredCountries -> handleFilteringCountries(state)
      }
    }
  }
  
  private fun handleLoading() {
    log { "loading" }
    layoutLoadingStats.visible()
  }
  
  private fun handleLoadedAll(state: LoadedAll) {
    log { "loaded all, from cache = ${state.isFromCache}" }
    adapter.submitList(state.items)
    if (!state.isFromCache) {
      layoutLoadingStats.invisible()
    }
  }
  
  private fun handleFilteringCountries(state: FilteredCountries) {
    log { "filtering" }
    recyclerView!!.post {
      (recyclerView.findViewHolderForAdapterPosition(0) as HeaderViewHolder).updateChip(
        state.optionType)
      adapter.updateFilteredCountries(state.countries)
    }
  }
}