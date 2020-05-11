package es.jfechevarria.data.cloud

import android.content.Context
import es.jfechevarria.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor(context: Context) {
    private var client: OkHttpClient? = null

    companion object {
        @Volatile
        private var retrofit: Retrofit? = null

        fun getClient(context: Context): Retrofit {
            if (retrofit == null) {
                synchronized(Retrofit::class) {
                    if (retrofit == null) {
                        RetrofitClient(context)
                    }
                }
            }
            return retrofit!!
        }
    }
    init {

        val interceptorHeaders = HttpLoggingInterceptor()
        interceptorHeaders.level = HttpLoggingInterceptor.Level.HEADERS

        val interceptorBody = HttpLoggingInterceptor()
        interceptorBody.level = HttpLoggingInterceptor.Level.NONE
        client = OkHttpClient.Builder()
            .addInterceptor(interceptorHeaders)
            .addInterceptor(interceptorBody)
            .addInterceptor(HeadersInterceptor(context))
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.URL_BASE)
            .client(client!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
