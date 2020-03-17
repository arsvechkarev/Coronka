package com.arsvechkarev.core.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxViewModel : ViewModel() {
  
  private val disposables = CompositeDisposable()
  
  protected fun rxCall(block: () -> Disposable) {
    val disposable = block()
    disposables.add(disposable)
  }
  
  override fun onCleared() {
    super.onCleared()
    disposables.clear()
  }
}