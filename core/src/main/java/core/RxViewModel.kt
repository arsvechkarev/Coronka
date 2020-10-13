package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxViewModel : ViewModel() {
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  private val compositeDisposable = CompositeDisposable()
  
  fun rxCall(block: () -> Disposable) {
    compositeDisposable.add(block())
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
}