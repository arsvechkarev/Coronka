package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.behaviors.ScrollableContentBehavior
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedWorldCasesInfo
import com.arsvechkarev.views.CoronavirusMainStatsView
import com.arsvechkarev.views.loadingstubs.BaseStubDrawable.Companion.applyLoadingDrawable
import com.arsvechkarev.views.loadingstubs.MainStatsInfoLoadingDrawable
import com.arsvechkarev.views.loadingstubs.StatsGraphLoadingDrawable
import core.extenstions.animateChildrenInvisible
import core.extenstions.animateChildrenVisible
import core.extenstions.animateInvisible
import core.extenstions.animateVisible
import core.extenstions.getBehavior
import core.model.GeneralInfo
import core.model.WorldCasesInfo
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.FailureReason
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.Failure.FailureReason.TIMEOUT
import core.state.Failure.FailureReason.UNKNOWN
import core.state.Loading
import kotlinx.android.synthetic.main.fragment_stats.statsContentView
import kotlinx.android.synthetic.main.fragment_stats.statsErrorLayout
import kotlinx.android.synthetic.main.fragment_stats.statsErrorMessage
import kotlinx.android.synthetic.main.fragment_stats.statsImageFailure
import kotlinx.android.synthetic.main.fragment_stats.statsMainInfoLoadingStub
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesLabel
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesLoadingStub
import kotlinx.android.synthetic.main.fragment_stats.statsRetryButton
import kotlinx.android.synthetic.main.fragment_stats.statsScrollingContentView
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesLabel
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesLoadingStub
import kotlinx.android.synthetic.main.fragment_stats.statsViewConfirmed
import kotlinx.android.synthetic.main.fragment_stats.statsViewDeaths
import kotlinx.android.synthetic.main.fragment_stats.statsViewRecovered

class StatsFragment : Fragment(R.layout.fragment_stats) {
  
  private lateinit var viewModel: StatsViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = StatsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    initLoadingStubs()
    initClickListeners()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
        renderLoading()
      }
      is LoadedWorldCasesInfo -> {
        toggleScrollingContent(turnOn = true)
        renderGeneralInfo(state.worldCasesInfo.generalInfo)
        renderCharts(state.worldCasesInfo)
      }
      is Failure -> {
        renderFailure(state.reason)
      }
    }
  }
  
  private fun renderLoading() {
    updateContentView(putLoading = true)
  }
  
  private fun renderCharts(info: WorldCasesInfo) {
    statsTotalCasesChart.update(info.totalDailyCases)
    statsNewCasesChart.update(info.newDailyCases)
    statsTotalCasesChart.animateVisible(andThen = { statsTotalCasesLoadingStub.background = null })
    statsNewCasesChart.animateVisible(andThen = { statsNewCasesLoadingStub.background = null })
    statsTotalCasesLabel.animateVisible()
    statsNewCasesLabel.animateVisible()
  }
  
  private fun renderGeneralInfo(generalInfo: GeneralInfo) {
    view!!.post {
      statsViewConfirmed.animateVisible(andThen = { statsMainInfoLoadingStub.background = null })
      statsViewRecovered.animateVisible()
      statsViewDeaths.animateVisible()
      val confirmedTextSize = getTextSize(generalInfo.confirmed)
      val recoveredTextSize = getTextSize(generalInfo.confirmed)
      val deathsTextSize = getTextSize(generalInfo.confirmed)
      val textSize = minOf(confirmedTextSize, recoveredTextSize, deathsTextSize)
      statsViewConfirmed.prepareNumber(generalInfo.confirmed, textSize)
      statsViewRecovered.prepareNumber(generalInfo.recovered, textSize)
      statsViewDeaths.prepareNumber(generalInfo.deaths, textSize)
    }
  }
  
  private fun renderFailure(reason: FailureReason) {
    updateContentView(putLoading = false)
    when (reason) {
      NO_CONNECTION -> {
        statsImageFailure.setImageResource(R.drawable.image_no_connection)
        statsErrorMessage.setText(R.string.text_no_connection)
      }
      TIMEOUT, UNKNOWN -> {
        statsImageFailure.setImageResource(R.drawable.image_unknown_error)
        statsErrorMessage.setText(R.string.text_unknown_error)
      }
    }
  }
  
  private fun updateContentView(putLoading: Boolean) {
    toggleScrollingContent(turnOn = false)
    if (putLoading) {
      statsErrorLayout.animateInvisible()
      statsContentView.animateChildrenVisible()
    } else {
      statsErrorLayout.animateVisible()
      statsContentView.animateChildrenInvisible()
    }
  }
  
  private fun toggleScrollingContent(turnOn: Boolean) {
    val behavior = statsScrollingContentView.getBehavior<ScrollableContentBehavior<*>>()
    behavior.respondToTouches = turnOn
    statsScrollingContentView.isEnabled = turnOn
  }
  
  private fun getTextSize(number: Int): Float {
    return CoronavirusMainStatsView.getTextSize(statsViewConfirmed.width,
      CoronavirusMainStatsView.getTextForNumber(number))
  }
  
  private fun initLoadingStubs() {
    statsMainInfoLoadingStub.applyLoadingDrawable(MainStatsInfoLoadingDrawable(requireContext()))
    statsTotalCasesLoadingStub.applyLoadingDrawable(StatsGraphLoadingDrawable(requireContext()))
    statsNewCasesLoadingStub.applyLoadingDrawable(StatsGraphLoadingDrawable(requireContext()))
  }
  
  private fun initClickListeners() {
    statsTotalCasesChart.onDailyCaseClicked { statsTotalCasesLabel.drawCase(it) }
    statsNewCasesChart.onDailyCaseClicked { statsNewCasesLabel.drawCase(it) }
    statsRetryButton.setOnClickListener { viewModel.startLoadingData() }
  }
}