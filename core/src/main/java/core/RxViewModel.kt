package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxViewModel : ViewModel() {
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  protected val compositeDisposable = CompositeDisposable()
  
  protected val loadingNowList = ArrayList<String>()
  
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
  
  protected fun <T> Observable<T>.smartSubscribe(onNext: (T) -> Unit): Disposable {
    return smartSubscribe(DEFAULT_LOADING_CONSTANT, onNext)
  }
  
  protected fun Completable.smartSubscribe(
    onComplete: () -> Unit,
    onError: (Throwable) -> Unit
  ): Disposable {
    return smartSubscribe(DEFAULT_LOADING_CONSTANT, onComplete, onError)
  }
  
  protected open fun <T> Observable<T>.smartSubscribe(
    loadingConstant: String,
    onNext: (T) -> Unit
  ): Disposable {
    return subscribe { state ->
      if (state !is Loading) {
        loadingNowList.remove(loadingConstant)
      }
      onNext(state)
    }
  }
  
  protected open fun Completable.smartSubscribe(
    loadingConstant: String,
    onComplete: () -> Unit,
    onError: (Throwable) -> Unit = {}
  ): Disposable {
    return subscribe({
      loadingNowList.remove(loadingConstant)
      onComplete()
    }, {
      loadingNowList.remove(loadingConstant)
      onError(it)
    })
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
  
  companion object {
    
    private const val DEFAULT_LOADING_CONSTANT = "isLoadingNow"
  }
}