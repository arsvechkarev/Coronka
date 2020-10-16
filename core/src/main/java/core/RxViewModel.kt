package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

abstract class RxViewModel : ViewModel() {
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  private val compositeDisposable = CompositeDisposable()
  
  private val loadingNow = CopyOnWriteArraySet<String>()
  
  private val cachedData = ConcurrentHashMap<String, Any>()
  
  protected fun <T> Observable<T>.smartSubscribe(onNext: (T) -> Unit): Disposable? {
    return smartSubscribe(DEFAULT_LOADING_CONSTANT, onNext)
  }
  
  protected fun <T> Observable<T>.smartSubscribe(
    loadingConstant: String,
    onNext: (T) -> Unit
  ): Disposable? {
    return subscribe { state ->
      if (state !is Loading) {
        loadingNow.remove(loadingConstant)
        if (state !is Failure) {
          cachedData[loadingConstant] = state!!
        }
      }
      onNext(state)
    }
  }
  
  protected fun rxCall(onSubscribe: () -> Disposable?) {
    rxCall(DEFAULT_LOADING_CONSTANT, onSubscribe)
  }
  
  protected fun rxCall(loadingConstant: String, onSubscribe: () -> Disposable?) {
    if (loadingNow.contains(loadingConstant)) return
    if (cachedData.containsKey(loadingConstant)) {
      _state.value = cachedData[loadingConstant] as BaseScreenState
      return
    }
    loadingNow.add(loadingConstant)
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