package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.NO_CONNECTION
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.TIMEOUT
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.UNKNOWN
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import com.arsvechkarev.map.utils.MapDelegate
import core.FontManager
import core.Loggable
import core.extenstions.invisible
import core.extenstions.visible
import core.log
import core.model.Country
import core.state.StateHandle
import core.state.isFresh
import kotlinx.android.synthetic.main.fragment_map.bottomSheet
import kotlinx.android.synthetic.main.fragment_map.layoutFailure
import kotlinx.android.synthetic.main.fragment_map.layoutLoadingMap
import kotlinx.android.synthetic.main.fragment_map.statsView
import kotlinx.android.synthetic.main.fragment_map.textFailureReason
import kotlinx.android.synthetic.main.fragment_map.textRetry
import kotlinx.android.synthetic.main.fragment_map.textViewCountryName

class MapFragment : Fragment(R.layout.fragment_map), Loggable {
  
  override val logTag = "Map_Fragment"
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: MapViewModel
  
  private var savedInstanceState: Bundle? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    mapDelegate.init(requireContext(), childFragmentManager, ::onCountrySelected)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    textViewCountryName.typeface = FontManager.rubik
    bottomSheet.setOnClickListener { bottomSheet.hide() }
    textRetry.setOnClickListener { viewModel.updateFromNetwork() }
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading(savedInstanceState != null)
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<MapScreenState>) {
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
    layoutFailure.invisible()
    layoutLoadingMap.visible()
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
  
  private fun handleFailure(state: Failure) {
    Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
    val message = when (state.reason) {
      NO_CONNECTION -> "No connection"
      TIMEOUT -> "Too slow connection"
      UNKNOWN -> "Unknown error"
    }
    textFailureReason.text = message
    layoutFailure.visible()
    layoutLoadingMap.invisible()
    Toast.makeText(context, "Failure: ${state.reason}", Toast.LENGTH_SHORT).show()
    log { "error, reason = ${state.reason}" }
  }
  
  private fun displayLoadedResult(countries: List<Country>) {
    layoutLoadingMap.invisible()
    mapDelegate.drawCountries(countries)
  }
  
  private fun onCountrySelected(countryCode: String) {
    viewModel.findCountryByCode(countryCode)
  }
}