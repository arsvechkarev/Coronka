package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedWorldCasesInfo
import com.arsvechkarev.views.CoronavirusMainStatsView
import com.arsvechkarev.views.loadingstubs.BaseStubDrawable.Companion.applyLoadingDrawable
import com.arsvechkarev.views.loadingstubs.MainStatsInfoLoadingDrawable
import com.arsvechkarev.views.loadingstubs.StatsGraphLoadingDrawable
import core.extenstions.visible
import core.model.GeneralInfo
import core.model.WorldCasesInfo
import core.state.BaseScreenState
import core.state.Loading
import kotlinx.android.synthetic.main.fragment_stats.statsLabelNewCases
import kotlinx.android.synthetic.main.fragment_stats.statsLabelTotalCases
import kotlinx.android.synthetic.main.fragment_stats.statsMainInfoElements
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesLayout
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesLayout
import kotlinx.android.synthetic.main.fragment_stats.statsViewConfirmed
import kotlinx.android.synthetic.main.fragment_stats.statsViewDeaths
import kotlinx.android.synthetic.main.fragment_stats.statsViewRecovered

class StatsFragment : Fragment(R.layout.fragment_stats) {
  
  private lateinit var viewModel: StatsViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = StatsModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(::handleState))
    viewModel.startInitialLoading()
    initLoadingStubs()
    initChartsClickListeners()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
    
      }
      is LoadedWorldCasesInfo -> {
        //        renderGeneralInfo(state.worldCasesInfo.generalInfo)
        //        renderCharts(state.worldCasesInfo)
      }
    }
  }
  
  private fun renderCharts(info: WorldCasesInfo) {
    statsTotalCasesLayout.background = requireContext().getDrawable(R.drawable.bg_overlay_corners_small)
    statsNewCasesLayout.background = requireContext().getDrawable(R.drawable.bg_overlay_corners_small)
    statsTotalCasesChart.update(info.totalDailyCases)
    statsNewCasesChart.update(info.newDailyCases)
  }
  
  
  private fun renderGeneralInfo(generalInfo: GeneralInfo) {
    view!!.post {
      statsMainInfoElements.background = null
      statsViewConfirmed.visible()
      statsViewRecovered.visible()
      statsViewDeaths.visible()
      val confirmedTextSize = getTextSize(generalInfo.confirmed)
      val recoveredTextSize = getTextSize(generalInfo.confirmed)
      val deathsTextSize = getTextSize(generalInfo.confirmed)
      val textSize = minOf(confirmedTextSize, recoveredTextSize, deathsTextSize)
      statsViewConfirmed.prepareNumber(generalInfo.confirmed, textSize)
      statsViewRecovered.prepareNumber(generalInfo.recovered, textSize)
      statsViewDeaths.prepareNumber(generalInfo.deaths, textSize)
    }
  }
  
  private fun getTextSize(number: Int): Float {
    return CoronavirusMainStatsView.getTextSize(statsViewConfirmed.width,
      CoronavirusMainStatsView.getTextForNumber(number))
  }
  
  private fun initLoadingStubs() {
    statsMainInfoElements.applyLoadingDrawable(MainStatsInfoLoadingDrawable(requireContext()))
    statsTotalCasesLayout.applyLoadingDrawable(StatsGraphLoadingDrawable(requireContext()))
    statsNewCasesLayout.applyLoadingDrawable(StatsGraphLoadingDrawable(requireContext()))
  }
  
  private fun initChartsClickListeners() {
    statsTotalCasesChart.onDailyCaseClicked { statsLabelTotalCases.drawCase(it) }
    statsNewCasesChart.onDailyCaseClicked { statsLabelNewCases.drawCase(it) }
  }
}