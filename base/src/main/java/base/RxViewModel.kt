package base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import core.BaseScreenState
import core.Loading
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * View model that facilitates common operations with rx
 */
abstract class RxViewModel : ViewModel() {
  
  private val compositeDisposable = CompositeDisposable()
  private val isLoadingNow = AtomicBoolean(false)
  
  protected val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  /**
   * Performs rxCall if current state is not loading. Use it with combination of [smartSubscribe]
   */
  protected open fun rxCall(onSubscribe: () -> Disposable?) {
    if (isLoadingNow.getAndSet(true)) return
    val disposable = onSubscribe()
    if (disposable != null) {
      compositeDisposable.add(disposable)
    }
  }
  
  /**
   * Subscribes to given observable and automatically sets flag [isLoadingNow] to false
   * if received item in onNext() is not loading item
   *
   * @see isItemLoading
   */
  protected open fun <T> Observable<T>.smartSubscribe(
    onNext: (T) -> Unit
  ): Disposable {
    return subscribe { item ->
      if (!isItemLoading(item)) {
        isLoadingNow.set(false)
      }
      onNext(item)
    }
  }
  
  /**
   * Returns true, if given [item] represents a loading state
   */
  protected open fun isItemLoading(item: Any?): Boolean {
    return item is Loading
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
}