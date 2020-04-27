package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import com.arsvechkarev.map.utils.MapDelegate
import core.Loggable
import core.extenstions.animateInvisibleAndScale
import core.extenstions.animateVisible
import core.extenstions.animateVisibleAndScale
import core.extenstions.invisible
import core.extenstions.visible
import core.model.Country
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.Failure.FailureReason.TIMEOUT
import core.state.Failure.FailureReason.UNKNOWN
import core.state.StateHandle
import core.state.isFresh
import kotlinx.android.synthetic.main.fragment_map.bottomSheet
import kotlinx.android.synthetic.main.fragment_map.earthView
import kotlinx.android.synthetic.main.fragment_map.fragment_map_root
import kotlinx.android.synthetic.main.fragment_map.layoutFailure
import kotlinx.android.synthetic.main.fragment_map.layoutLoadingMap
import kotlinx.android.synthetic.main.fragment_map.layoutNoConnection
import kotlinx.android.synthetic.main.fragment_map.layoutUnknownError
import kotlinx.android.synthetic.main.fragment_map.statsView
import kotlinx.android.synthetic.main.fragment_map.textRetry
import kotlinx.android.synthetic.main.fragment_map.textRetryUnknown
import kotlinx.android.synthetic.main.fragment_map.textViewCountryName

class MapFragment : Fragment(R.layout.fragment_map), Loggable {
  
  override val logTag = "Map_Fragment"
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: MapViewModel
  
  private var savedInstanceState: Bundle? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    mapDelegate.init(requireContext(), requireFragmentManager(), ::onCountrySelected)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    bottomSheet.setOnClickListener { bottomSheet.hide() }
    textRetry.setOnClickListener { viewModel.updateFromNetwork() }
    textRetryUnknown.setOnClickListener { viewModel.updateFromNetwork() }
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading(savedInstanceState != null)
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<BaseScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is Loading -> handleStartLoading()
        is LoadedFromCache -> handleCountriesLoadedFromCache(state)
        is LoadedFromNetwork -> handleCountriesLoadedFromNetwork(state)
        is FoundCountry -> handleFoundCountry(state)
        is Failure -> handleFailure(state)
      }
    }
  }
  
  private fun handleStartLoading() {
    layoutFailure.animateInvisibleAndScale()
    layoutLoadingMap.animateVisibleAndScale()
  }
  
  private fun handleCountriesLoadedFromCache(state: LoadedFromCache) {
    displayLoadedResult(state.countries)
  }
  
  private fun handleCountriesLoadedFromNetwork(state: LoadedFromNetwork) {
    displayLoadedResult(state.countries)
  }
  
  private fun handleFoundCountry(state: FoundCountry) {
    if (state.isFresh) {
      bottomSheet.show()
    } else {
      mapDelegate.drawCountries(state.countries)
    }
    textViewCountryName.text = state.country.name
    statsView.updateNumbers(
      state.country.confirmed,
      state.country.recovered,
      state.country.deaths
    )
  }
  
  private fun displayLoadedResult(countries: List<Country>) {
    fragment_map_root.animateVisible()
    layoutLoadingMap.animateInvisibleAndScale()
    mapDelegate.drawCountries(countries)
  }
  
  private fun onCountrySelected(countryCode: String) {
    viewModel.findCountryByCode(countryCode)
  }
  
  private fun handleFailure(state: Failure) {
    fragment_map_root.invisible()
    layoutUnknownError.invisible()
    layoutNoConnection.invisible()
    when (state.reason) {
      NO_CONNECTION -> {
        layoutNoConnection.visible()
        earthView.animateWifi()
      }
      TIMEOUT -> {
        layoutNoConnection.visible()
        earthView.animateHourglass()
      }
      UNKNOWN -> layoutUnknownError.visible()
    }
    textRetry.isClickable = false
    layoutFailure.animateVisibleAndScale(andThen = { textRetry.isClickable = true })
    layoutLoadingMap.animateInvisibleAndScale()
  }
}