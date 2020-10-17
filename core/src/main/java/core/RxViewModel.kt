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
  
  protected val compositeDisposable = CompositeDisposable()
  
  protected val loadingNowList = CopyOnWriteArraySet<String>()
  
  protected fun <T> Observable<T>.smartSubscribe(onNext: (T) -> Unit): Disposable? {
    return smartSubscribe(DEFAULT_LOADING_CONSTANT, onNext)
  }
  
  protected open fun <T> Observable<T>.smartSubscribe(
    loadingConstant: String,
    onNext: (T) -> Unit
  ): Disposable? {
    return subscribe { state ->
      if (state !is Loading) {
        loadingNowList.remove(loadingConstant)
      }
      onNext(state)
    }
  }
  
  protected fun rxCall(onSubscribe: () -> Disposable?) {
    rxCall(DEFAULT_LOADING_CONSTANT, onSubscribe)
  }
  
  protected open fun rxCall(loadingConstant: String, onSubscribe: () -> Disposable?) {
    if (loadingNowList.contains(loadingConstant)) return
    loadingNowList.add(loadingConstant)
    val disposable = onSubscribe()
    if (disposable != null) {
      compositeDisposable.add(disposable)
    }
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
  
  companion object {
    
    private const val DEFAULT_LOADING_CONSTANT = "isLoadingNow"
  }
}