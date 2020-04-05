package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import com.arsvechkarev.map.presentation.MapScreenState.Loading
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import com.arsvechkarev.map.utils.MapDelegate
import core.Application
import core.FontManager
import core.Loggable
import core.StateHandle
import core.extenstions.invisible
import core.extenstions.visible
import core.log
import kotlinx.android.synthetic.main.fragment_map.bottomSheet
import kotlinx.android.synthetic.main.fragment_map.layoutLoadingMap
import kotlinx.android.synthetic.main.fragment_map.statsView
import kotlinx.android.synthetic.main.fragment_map.textViewCountryName

class MapFragment : Fragment(R.layout.fragment_map), Loggable {
  
  override val logTag = "Map_Fragment"
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: MapViewModel
  
  private var savedInstanceState: Bundle? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    mapDelegate.init(requireContext(), childFragmentManager,
      ::onMapClicked, ::onCountrySelected, Application.Threader)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    textViewCountryName.typeface = FontManager.rubik
    bottomSheet.setOnClickListener { bottomSheet.hide() }
  }
  
  override fun onResume() {
    super.onResume()
    viewModel.startInitialLoading(savedInstanceState != null)
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<MapScreenState>) {
    stateHandle.handleUpdate { state ->
      when (state) {
        is Loading -> handleStartLoading()
        is LoadingCountryInfo -> handleStartLoadingCountryInfo()
        is LoadedFromCache -> handleCountriesLoadedFromCache(state)
        is LoadedFromNetwork -> handleCountriesLoadedFromNetwork(state)
        is FoundCountry -> handleFoundCountry(state)
        is Failure -> handleFailure(state)
      }
    }
  }
  
  private fun handleStartLoading() {
    layoutLoadingMap.visible()
  }
  
  private fun handleCountriesLoadedFromCache(state: LoadedFromCache) {
    mapDelegate.drawCountriesMarks(state.generalInfo, state.countriesList)
  }
  
  private fun handleCountriesLoadedFromNetwork(state: LoadedFromNetwork) {
    layoutLoadingMap.invisible()
    mapDelegate.drawCountriesMarks(state.generalInfo, state.countriesList)
  }
  
  private fun handleStartLoadingCountryInfo() {
  }
  
  private fun handleFoundCountry(state: FoundCountry) {
    textViewCountryName.text = state.country.name
    statsView.updateNumbers(
      state.country.confirmed,
      state.country.recovered,
      state.country.deaths
    )
    bottomSheet.show()
  }
  
  private fun handleFailure(state: Failure) {
    layoutLoadingMap.invisible()
    Toast.makeText(context, "Failure: ${state.reason}", Toast.LENGTH_SHORT).show()
    log { "error, reason = ${state.reason}" }
  }
  
  private fun onMapClicked() {
  
  }
  
  private fun onCountrySelected(countryCode: String) {
    viewModel.findCountryByCode(countryCode)
  }
}