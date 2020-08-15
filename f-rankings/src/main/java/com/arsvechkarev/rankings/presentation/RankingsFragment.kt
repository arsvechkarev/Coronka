package com.arsvechkarev.rankings.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.rankings.R
import com.arsvechkarev.rankings.di.RankingsDiInjector
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsScreenState.Success
import com.arsvechkarev.views.behaviors.HeaderBehavior
import core.extenstions.getBehavior
import core.state.BaseScreenState
import core.state.Loading
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderLayout
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
      is Loading -> {
      }
      is Success -> {
        adapter.submitList(state.list)
      }
    }
  }
  
  private fun setupBehavior(view: View) {
    val curveSize = requireContext().resources.getDimensionPixelSize(R.dimen.rankings_header_curve_size)
    rankingsHeaderLayout.getBehavior<HeaderBehavior<*>>().apply {
      reactToTouches = false
      val coefficient = 0.5f
      slideRangeCoefficient = coefficient
      view.post {
        val initialTitleY = rankingsTitle.y
        addOnOffsetListener { fraction ->
          rankingsTitle.y = initialTitleY + (fraction * (initialTitleY + curveSize) * (1 - coefficient))
        }
      }
    }
  }
}