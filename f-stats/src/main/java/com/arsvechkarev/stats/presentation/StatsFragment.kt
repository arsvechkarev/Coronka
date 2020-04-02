package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.list.StatsAdapter
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedAll
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingCountriesInfo
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingGeneralInfo
import core.Loggable
import core.StateHandle
import core.extenstions.invisible
import core.extenstions.visible
import core.log
import kotlinx.android.synthetic.main.fragment_stats.layoutLoading
import kotlinx.android.synthetic.main.fragment_stats.recyclerView

class StatsFragment : Fragment(R.layout.fragment_stats), Loggable {
  
  override val logTag = "StatsFragment"
  
  private lateinit var viewModel: StatsViewModel
  private val adapter = StatsAdapter(onOptionClick = { viewModel.filterList(it) })
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = StatsModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading()
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<StatsScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is LoadingCountriesInfo, LoadingGeneralInfo -> handleLoading()
        is LoadedAll -> handleLoadedAll(state)
        is FilteredCountries -> handleFilteringCountries(state)
      }
    }
  }
  
  private fun handleLoading() {
    log { "loading" }
    layoutLoading.visible()
  }
  
  private fun handleLoadedAll(state: LoadedAll) {
    log { "loaded all, from cache = ${state.isFromCache}" }
    adapter.submitList(state.items)
    if (!state.isFromCache) {
      layoutLoading.invisible()
    }
  }
  
  private fun handleFilteringCountries(state: FilteredCountries) {
    log { "filtering" }
    adapter.updateFilteredCountries(state.countries)
  }
}