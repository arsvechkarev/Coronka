package core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxViewModel : ViewModel() {
  
  private val compositeDisposable = CompositeDisposable()
  
  fun rxCall(block: () -> Disposable) {
    compositeDisposable.add(block())
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
}