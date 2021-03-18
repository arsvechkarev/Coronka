package com.arsvechkarev.stats.presentation

import core.BaseScreenState
import core.model.WorldCasesInfo

class LoadedWorldCasesInfo(val worldCasesInfo: WorldCasesInfo) : BaseScreenState

sealed class SuccessOrError {
  
  class Success<T>(val value: T) : SuccessOrError()
  
  class Error(val throwable: Throwable) : SuccessOrError()
}