package core

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpWebApiFactory(private val client: OkHttpClient) : WebApi.Factory {
  
  override fun create(): WebApi {
    return OkHttpWebApi(client)
  }
}

class OkHttpWebApi(private val client: OkHttpClient) : WebApi {
  
  override fun request(url: String) = Observable.create<String> { emitter ->
    try {
      val request: Request = Request.Builder()
          .url(url)
          .build()
      client.newCall(request).execute().use { response ->
        if (emitter.isDisposed) return@create
        if (!response.isSuccessful && !emitter.isDisposed) {
          emitter.onError(HttpException(response))
          emitter.onComplete()
          return@create
        }
        response.body()!!.use { body ->
          emitter.onNext(body.string())
        }
        emitter.onComplete()
      }
    } catch (e: Throwable) {
      if (!emitter.isDisposed) {
        emitter.onError(e)
      }
    }
  }
}