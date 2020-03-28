package core

import androidx.lifecycle.ViewModel

abstract class NetworkViewModel(protected val connection: NetworkConnection) : ViewModel() {
  
  override fun onCleared() {
    connection.release()
  }
}