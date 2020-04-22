package com.arsvechkarev.map.presentation

import core.model.Country
import core.state.BaseScreenState
import java.net.UnknownHostException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

sealed class MapScreenState : BaseScreenState() {
  
  object Loading : MapScreenState()
  
  class LoadedFromCache(
    val countries: List<Country>
  ) : MapScreenState()
  
  class LoadedFromNetwork(
    val countries: List<Country>
  ) : MapScreenState()
  
  class FoundCountry(
    val countries: List<Country>,
    val country: Country
  ) : MapScreenState()
  
  data class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
    
    companion object {
      fun Throwable.toReason() = when (this) {
        is TimeoutException -> FailureReason.TIMEOUT
        is ExecutionException -> {
          when (cause) {
            is TimeoutException -> FailureReason.TIMEOUT
            is UnknownHostException -> FailureReason.NO_CONNECTION
            else -> FailureReason.UNKNOWN
          }
        }
        else -> FailureReason.UNKNOWN
      }
    }
  }
}