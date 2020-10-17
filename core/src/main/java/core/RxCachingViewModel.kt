package core

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap

class RxCachingViewModel : RxViewModel() {
  
  private val cachedData = ConcurrentHashMap<String, Any>()
  
  override fun <T> Observable<T>.smartSubscribe(
    loadingConstant: String,
    onNext: (T) -> Unit
  ): Disposable? {
    return subscribe { state ->
      if (state !is Loading) {
        loadingNowList.remove(loadingConstant)
        if (state !is Failure) {
          cachedData[loadingConstant] = state!!
        }
      }
      onNext(state)
    }
  }
  
  override fun rxCall(loadingConstant: String, onSubscribe: () -> Disposable?) {
    if (loadingNowList.contains(loadingConstant)) return
    if (cachedData.containsKey(loadingConstant)) {
      _state.value = cachedData[loadingConstant] as BaseScreenState
      return
    }
    loadingNowList.add(loadingConstant)
    val disposable = onSubscribe()
    if (disposable != null) {
      compositeDisposable.add(disposable)
    }
  }
}