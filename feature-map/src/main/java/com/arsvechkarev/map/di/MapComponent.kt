package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.di.CoreComponent.schedulers
import core.di.ModuleInterceptorManager.interceptModuleOrDefault

object MapComponent {
  
  fun getViewModel(fragment: MapFragment): MapViewModel {
    return ViewModelProviders.of(fragment, mapViewModelFactory).get(MapViewModel::class.java)
  }
  
  private val mapViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      val module = interceptModuleOrDefault<MapModule> { DefaultMapModule }
      @Suppress("UNCHECKED_CAST")
      return MapViewModel(module.mapInteractor, schedulers) as T
    }
  }
}