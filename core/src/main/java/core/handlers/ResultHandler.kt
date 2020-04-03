package core.handlers

class ResultHandler<S, F>(
  private val onSuccess: (S) -> Unit,
  private val onFailure: (F) -> Unit
) {
  
  @Volatile
  var isRunning = false
    private set
  
  fun runIfNotAlready(action: (ResultHandler<S, F>) -> Unit): ResultHandler<S, F> {
    if (!isRunning) {
      isRunning = true
      action(this)
    }
    return this
  }
  
  fun dispatchSuccess(result: S) {
    onSuccess(result)
    isRunning = false
  }
  
  fun dispatchFailure(error: F) {
    onFailure(error)
    isRunning = false
  }
}

class ResultHandlerBuilder<S, F> {
  private lateinit var onSuccess: (S) -> Unit
  private lateinit var onFailure: (F) -> Unit
  
  fun onSuccess(onSuccess: (S) -> Unit) {
    this.onSuccess = onSuccess
  }
  
  fun onFailure(onFailure: (F) -> Unit) {
    this.onFailure = onFailure
  }
  
  fun build() = ResultHandler(onSuccess, onFailure)
}

typealias ResultAction<S, F> = ResultHandlerBuilder<S, F>.() -> Unit

fun <S, F> createResultHandler(action: ResultAction<S, F>): ResultHandler<S, F> {
  return ResultHandlerBuilder<S, F>().apply(action).build()
}