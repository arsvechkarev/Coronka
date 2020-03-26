package core

abstract class ResultHandler<S, F> {
  
  open fun onSuccess(value: S) {}
  
  open fun onFailure(error: F) {}
}