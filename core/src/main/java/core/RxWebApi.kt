package core

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpWebApiFactory(private val client: OkHttpClient) : WebApi.Factory {
  
  override fun create(): WebApi {
    return OkHttpWebApi(client)
  }
}

class OkHttpWebApi(private val client: OkHttpClient) : WebApi {
  
  override fun request(url: String) = Single.fromCallable {
    val request: Request = Request.Builder()
        .url(url)
        .build()
    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) {
        throw HttpException(response)
      }
      return@fromCallable response.body()!!.string()
    }
  }
}