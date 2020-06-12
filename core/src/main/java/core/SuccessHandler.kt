package core

class SuccessHandler<S>(
  private val onSuccess: (S) -> Unit,
  private val onNothing: () -> Unit
) {
  
  fun dispatchSuccess(result: S) {
    onSuccess(result)
  }
  
  fun dispatchNothing() {
    onNothing()
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