package es.jfechevarria.data.cloud

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeadersInterceptor(private val context: Context) : Interceptor {

    companion object {
        const val JSON = "application/json"
        const val ACCEPT = "accept"
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()

        requestBuilder.addHeader(ACCEPT, JSON)
        return chain.proceed(requestBuilder.build())
    }
}