package com.arsvechkarev.rankings.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.rankings.R
import com.arsvechkarev.rankings.di.RankingsDiInjector
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsScreenState.Loaded
import com.arsvechkarev.views.behaviors.HeaderBehavior
import core.extenstions.getBehavior
import core.state.BaseScreenState
import core.state.Loading
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderLayout
import kotlinx.android.synthetic.main.fragment_rankings.rankingsIconDrawer
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRecyclerView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsTitle

class RankingsFragment : Fragment(R.layout.fragment_rankings) {
  
  private lateinit var viewModel: RankingsViewModel
  private val adapter = RankingsAdapter()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = RankingsDiInjector.provideViewModel(this)
    viewModel.startInitialLoading()
    viewModel.state.observe(this, Observer(::handleState))
    rankingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    rankingsRecyclerView.adapter = adapter
    setupBehavior(view)
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> ""
      is Loaded -> adapter.submitList(state.list)
    }
  }
  
  private fun setupBehavior(view: View) {
    val behavior = rankingsHeaderLayout.getBehavior<HeaderBehavior<*>>()
    behavior.slideRangeCoefficient = 0.5f
    view.post {
      val initialTitleY = rankingsTitle.y
      val drawerIconDistanceToTitle = rankingsIconDrawer.y - initialTitleY
      behavior.addOnOffsetListener { fraction ->
        rankingsTitle.y = initialTitleY + (fraction * initialTitleY / 2)
        rankingsIconDrawer.y = rankingsTitle.y + (1 - fraction) * drawerIconDistanceToTitle
      }
    }
  }
}