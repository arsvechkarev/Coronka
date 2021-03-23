package core

import io.reactivex.Single

interface WebApi {
  
  /**
   * Returns observable with string by given url
   */
  fun request(url: String): Single<String>
  
  /**
   * Factory for creating instances of this api
   */
  interface Factory {
    
    fun create(): WebApi
  }
}