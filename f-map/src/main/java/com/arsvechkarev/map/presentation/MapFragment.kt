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
import kotlinx.android.synthetic.main.fragment_map.fragment_map_root
import kotlinx.android.synthetic.main.fragment_map.mapBottomSheet
import kotlinx.android.synthetic.main.fragment_map.mapEarthView
import kotlinx.android.synthetic.main.fragment_map.mapLayoutFailure
import kotlinx.android.synthetic.main.fragment_map.mapLayoutLoading
import kotlinx.android.synthetic.main.fragment_map.mapLayoutNoConnection
import kotlinx.android.synthetic.main.fragment_map.mapLayoutUnknownError
import kotlinx.android.synthetic.main.fragment_map.mapStatsView
import kotlinx.android.synthetic.main.fragment_map.mapTextFailureReason
import kotlinx.android.synthetic.main.fragment_map.mapTextRetry
import kotlinx.android.synthetic.main.fragment_map.mapTextRetryUnknown
import kotlinx.android.synthetic.main.fragment_map.mapTextViewCountryName

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
    mapBottomSheet.setOnClickListener { mapBottomSheet.hide() }
    mapTextRetry.setOnClickListener { viewModel.updateFromNetwork() }
    mapTextRetryUnknown.setOnClickListener { viewModel.updateFromNetwork() }
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
    mapLayoutFailure.animateInvisibleAndScale()
    mapLayoutLoading.animateVisibleAndScale()
  }
  
  private fun handleCountriesLoadedFromCache(state: LoadedFromCache) {
    displayLoadedResult(state.countries)
  }
  
  private fun handleCountriesLoadedFromNetwork(state: LoadedFromNetwork) {
    displayLoadedResult(state.countries)
  }
  
  private fun handleFoundCountry(state: FoundCountry) {
    if (state.isFresh) {
      mapBottomSheet.show()
    } else {
      mapDelegate.drawCountries(state.countries)
    }
    mapTextViewCountryName.text = state.country.name
    mapStatsView.updateNumbers(
      state.country.confirmed,
      state.country.recovered,
      state.country.deaths
    )
  }
  
  private fun displayLoadedResult(countries: List<Country>) {
    fragment_map_root.animateVisible()
    mapLayoutLoading.animateInvisibleAndScale()
    mapDelegate.drawCountries(countries)
  }
  
  private fun onCountrySelected(country: Country) {
    viewModel.showCountryInfo(country)
  }
  
  private fun handleFailure(state: Failure) {
    fragment_map_root.invisible()
    mapLayoutUnknownError.invisible()
    mapLayoutNoConnection.invisible()
    when (state.reason) {
      NO_CONNECTION -> {
        mapTextFailureReason.text = getString(R.string.text_no_connection)
        mapLayoutNoConnection.visible()
        mapEarthView.animateWifi()
      }
      TIMEOUT -> {
        mapTextFailureReason.text = getString(R.string.text_timeout)
        mapLayoutNoConnection.visible()
        mapEarthView.animateHourglass()
      }
      UNKNOWN -> mapLayoutUnknownError.visible()
    }
    mapTextRetry.isClickable = false
    mapLayoutFailure.animateVisibleAndScale(andThen = { mapTextRetry.isClickable = true })
    mapLayoutLoading.animateInvisibleAndScale()
  }
}