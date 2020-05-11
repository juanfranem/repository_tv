package es.jfechevarria.data.cloud

import es.jfechevarria.domain.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Services {

    @GET("channels")
    suspend fun all(@Query("page") page: Int = 1, @Query("search") search: String? = null): DefaultResponse<List<Channel>>

    @GET("channels/{id}")
    suspend fun report(@Path("id") id: Long): DefaultResponse<String>
}