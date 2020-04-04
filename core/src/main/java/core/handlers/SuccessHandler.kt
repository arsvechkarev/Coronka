package core.handlers

class SuccessHandler<S>(
  private val onSuccess: (S) -> Unit,
  private val onNothing: () -> Unit
) {
  
  @Volatile
  var isRunning = false
    private set
  
  fun runIfNotAlready(action: (SuccessHandler<S>) -> Unit): SuccessHandler<S> {
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
  
  fun dispatchNothing() {
    onNothing()
    isRunning = false
  }
}

class SuccessHandlerBuilder<S> {
  private lateinit var onSuccess: (S) -> Unit
  private var onNothing: () -> Unit = {}
  
  fun onSuccess(onSuccess: (S) -> Unit) {
    this.onSuccess = onSuccess
  }
  
  fun onNothing(onNothing: () -> Unit) {
    this.onNothing = onNothing
  }
  
  fun build() = SuccessHandler(onSuccess, onNothing)
}

typealias SuccessAction<S> = SuccessHandlerBuilder<S>.() -> Unit

fun <S> createSuccessHandler(action: SuccessAction<S>): SuccessHandler<S> {
  return SuccessHandlerBuilder<S>().apply(action).build()
}