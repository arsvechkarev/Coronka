package core

import okhttp3.Response

/**
 * Exception for a non-2xx HTTP response
 */
class HttpException(response: Response) : RuntimeException() {
  
  override val message = "HttpException, response=$response"
  
  val code = response.code()
}