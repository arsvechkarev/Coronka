package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.CopyOnWriteArraySet

abstract class RxViewModel : ViewModel() {
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  private val compositeDisposable = CompositeDisposable()
  
  private var loadingNow = CopyOnWriteArraySet<String>()
  
  protected fun <T> Observable<T>.smartSubscribe(onNext: (T) -> Unit): Disposable {
    return smartSubscribe(DEFAULT_LOADING_CONSTANT, onNext)
  }
  
  protected fun <T> Observable<T>.smartSubscribe(
    loadingConstant: String,
    onNext: (T) -> Unit
  ) = subscribe { state ->
    if (state !is Loading) {
      loadingNow.remove(loadingConstant)
    }
    onNext(state)
  }
  
  protected fun rxCall(onSubscribe: () -> Disposable) {
    rxCall(DEFAULT_LOADING_CONSTANT, onSubscribe)
  }
  
  protected fun rxCall(loadingConstant: String, onSubscribe: () -> Disposable) {
    if (loadingNow.contains(loadingConstant)) return
    loadingNow.add(loadingConstant)
    compositeDisposable.add(onSubscribe())
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
  
  companion object {
    
    private const val DEFAULT_LOADING_CONSTANT = "isLoadingNow"
  }
}