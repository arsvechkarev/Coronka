package com.arsvechkarev.stats.presentation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.behaviors.ScrollableContentBehavior
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import com.arsvechkarev.views.drawables.MainStatsInfoLoadingStub
import com.arsvechkarev.views.drawables.StatsGraphLoadingStub
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Failure.FailureReason
import core.Failure.FailureReason.NO_CONNECTION
import core.Failure.FailureReason.TIMEOUT
import core.Failure.FailureReason.UNKNOWN
import core.Loading
import core.hostActivity
import core.model.GeneralInfo
import core.model.WorldCasesInfo
import kotlinx.android.synthetic.main.fragment_stats.statsContentView
import kotlinx.android.synthetic.main.fragment_stats.statsErrorLayout
import kotlinx.android.synthetic.main.fragment_stats.statsErrorMessage
import kotlinx.android.synthetic.main.fragment_stats.statsGeneralStatsView
import kotlinx.android.synthetic.main.fragment_stats.statsIconDrawer
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
import viewdsl.animateChildrenInvisible
import viewdsl.animateChildrenVisible
import viewdsl.animateInvisible
import viewdsl.animateVisible
import viewdsl.getBehavior

class StatsFragment : BaseFragment(R.layout.fragment_stats) {
  
  private var viewModel: StatsViewModel? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = StatsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    initClickListeners()
    initLoadingStubs()
  }
  
  override fun onNetworkAvailable() {
    val viewModel = viewModel ?: return
    val value = viewModel.state.value ?: return
    if (value !is LoadedWorldCasesInfo) {
      viewModel.startLoadingData()
    }
  }
  
  override fun onDrawerOpened() = toggleScrollingContent(false)
  
  override fun onDrawerClosed() {
    val state = viewModel?.state?.value
    if (state is Loading || state is Failure) {
      toggleScrollingContent(false)
    } else {
      toggleScrollingContent(true)
    }
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
        renderLoading()
      }
      is LoadedWorldCasesInfo -> {
        hostActivity.enableTouchesOnDrawer()
        toggleScrollingContent(enable = true)
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
    statsGeneralStatsView.updateNumbers(generalInfo)
    statsGeneralStatsView.animateVisible(andThen = { statsMainInfoLoadingStub.background = null })
  }
  
  private fun renderFailure(reason: FailureReason) {
    hostActivity.enableTouchesOnDrawer()
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
    toggleScrollingContent(enable = false)
    if (putLoading) {
      statsErrorLayout.animateInvisible()
      statsContentView.animateChildrenVisible()
    } else {
      statsErrorLayout.animateVisible()
      statsContentView.animateChildrenInvisible()
    }
  }
  
  private fun toggleScrollingContent(enable: Boolean) {
    val behavior = statsScrollingContentView.getBehavior<ScrollableContentBehavior<*>>()
    behavior.respondToTouches = enable
    statsScrollingContentView.isEnabled = enable
    statsNewCasesChart.isEnabled = enable
    statsTotalCasesChart.isEnabled = enable
  }
  
  private fun initClickListeners() {
    val onDown = { hostActivity.disableTouchesOnDrawer() }
    val onUp = { hostActivity.enableTouchesOnDrawer() }
    statsTotalCasesChart.onDown = onDown
    statsNewCasesChart.onDown = onDown
    statsTotalCasesChart.onUp = onUp
    statsNewCasesChart.onUp = onUp
    statsIconDrawer.setOnClickListener { hostActivity.openDrawer() }
    statsTotalCasesChart.onDailyCaseClicked { statsTotalCasesLabel.drawCase(it) }
    statsNewCasesChart.onDailyCaseClicked { statsNewCasesLabel.drawCase(it) }
    statsRetryButton.setOnClickListener { viewModel!!.startLoadingData() }
  }
  
  private fun initLoadingStubs() {
    statsMainInfoLoadingStub.setLoadingDrawable(MainStatsInfoLoadingStub(requireContext()))
    statsTotalCasesLoadingStub.setLoadingDrawable(StatsGraphLoadingStub(requireContext()))
    statsNewCasesLoadingStub.setLoadingDrawable(StatsGraphLoadingStub(requireContext()))
  }
}