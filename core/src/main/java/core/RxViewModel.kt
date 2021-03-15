package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

abstract class RxViewModel : ViewModel() {
  
  private val compositeDisposable = CompositeDisposable()
  private var isLoadingNow = AtomicBoolean(false)
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  protected open fun rxCall(onSubscribe: () -> Disposable?) {
    if (isLoadingNow.get()) return
    isLoadingNow.set(true)
    val disposable = onSubscribe()
    if (disposable != null) {
      compositeDisposable.add(disposable)
    }
  }
  
  protected open fun <T> Observable<T>.smartSubscribe(
    onNext: (T) -> Unit
  ): Disposable {
    return subscribe { state ->
      if (state !is Loading) {
        isLoadingNow.set(false)
      }
      onNext(state)
    }
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
}