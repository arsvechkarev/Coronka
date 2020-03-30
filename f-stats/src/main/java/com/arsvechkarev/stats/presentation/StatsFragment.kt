package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.list.StatsAdapter
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedAll
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingCountriesInfo
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadingGeneralInfo
import core.StateHandle
import core.extenstions.invisible
import core.extenstions.visible
import kotlinx.android.synthetic.main.fragment_stats.layoutLoading
import kotlinx.android.synthetic.main.fragment_stats.recyclerView

class StatsFragment : Fragment(R.layout.fragment_stats) {
  
  private lateinit var viewModel: StatsViewModel
  private val adapter = StatsAdapter()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = StatsModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    viewModel.loadData()
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<StatsScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is LoadingCountriesInfo, LoadingGeneralInfo -> handleLoading()
        is LoadedAll -> handleLoadedAll(state)
      }
    }
  }
  
  private fun handleLoadedAll(state: LoadedAll) {
    layoutLoading.invisible()
    adapter.submitList(state.displayableCountries)
  }
  
  private fun handleLoading() {
    layoutLoading.visible()
  }
}