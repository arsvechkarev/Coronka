package core.extenstions

import com.google.android.gms.tasks.Task
import io.reactivex.Completable
import io.reactivex.Single

fun Task<Void>.asCompletable() = Completable.create { emitter ->
  addOnCompleteListener { task ->
    if (!emitter.isDisposed) {
      if (task.isSuccessful) {
        emitter.onComplete()
      } else {
        emitter.onError(task.exception!!)
      }
    }
  }
  addOnFailureListener { exception ->
    if (!emitter.isDisposed) {
      emitter.onError(exception)
    }
  }
}

fun <T> Task<T>.asSingle() = Single.create<T> { emitter ->
  addOnCompleteListener { task ->
    if (!emitter.isDisposed) {
      if (task.isSuccessful && task.result != null) {
        emitter.onSuccess(task.result!!)
      } else {
        emitter.onError(task.exception!!)
      }
    }
  }
  addOnFailureListener { exception ->
    if (!emitter.isDisposed) {
      emitter.onError(exception)
    }
  }
}