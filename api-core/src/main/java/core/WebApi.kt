package core

import io.reactivex.Single

interface WebApi {
  
  /**
   * Returns single with string by given url
   */
  fun request(url: String): Single<String>
}